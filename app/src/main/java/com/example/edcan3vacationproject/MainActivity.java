package com.example.edcan3vacationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.edcan3vacationproject.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.edcan3vacationproject.BR.msg;


public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private ActivityMainBinding binding;
    private ObservableArrayList<Message> items = new ObservableArrayList<>();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GpsTracker gpsTracker = new GpsTracker(this);

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setItems(items);
        binding.imgSendBtn.setOnClickListener(view -> {
            send(binding.getMessage1());
            items.add(new Message(new ChatClient(
                    UserCache.getUser(this).getId(),
                    UserCache.getUser(this).getName(),
                    UserCache.getUser(this).getEmail()), binding.getMessage1()));
            binding.setMessage1("");
        });
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");
        binding.revMain.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ChatAdapter chatAdapter = new ChatAdapter(UserCache.getUser(this).getEmail());
        binding.revMain.setAdapter(chatAdapter);


        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        Toast.makeText(MainActivity.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();
        ClientConnected clientConnected = new ClientConnected(new ChatClient(
                UserCache.getUser(this).getId(),
                UserCache.getUser(this).getName(),
                UserCache.getUser(this).getEmail()), new GPSdata(longitude, latitude));

        String ccdString = ObjectToJson(clientConnected);

        Toast.makeText(getApplicationContext(), "Connecting to server...", Toast.LENGTH_SHORT).show();
        AsyncConnect(ccdString, (string) -> {
            Gson gson = new Gson();
            Packet convertedObject = gson.fromJson(string, Packet.class);
            Log.e("a", ObjectToJson(new Message(new ChatClient("a", "b", "c"), "asa")));
            Log.e("a", string);
            Log.e("a", convertedObject.PacketType.toString());
            switch (convertedObject.PacketType) {
                case Message:
                    Message recieveMsg = (Message) gson.fromJson(string, Message.class);
                    items.add(recieveMsg);
                    break;
            }
        });

        TimerTask tt = new TimerTask() {
            public void run() {
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                GPS gps = new GPS(new GPSdata(longitude, latitude));
                String GpsSend = ObjectToJson(gps);
                AsyncSend(GpsSend);

            }
        };
        Timer timer = new Timer();
        timer.schedule(tt, 0, 10000);
    }

    @Override
    protected void onStop() {  //앱 종료시
        super.onStop();
        ClientDisConnect disConnect = new ClientDisConnect();
        String data = ObjectToJson(disConnect);
        AsyncSend(data);
        AsyncDelay(3000, () -> {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void AsyncDelay(int initData, DelayFunc delayFunc) {
        DelayThread thread = new DelayThread(initData, delayFunc);
        thread.start();
    }

    public void AsyncConnect(String initData, RecivedDataFunc recivedDataFunc) {
        ConnectThread thread = new ConnectThread(initData, recivedDataFunc);
        thread.start();
    }

    public void AsyncListening(RecivedDataFunc recivedDataFunc) {
        ListenerThread thread = new ListenerThread(recivedDataFunc);
        thread.start();
    }

    public void AsyncSend(String data) {
        DataThread thread = new DataThread(data);
        thread.start();
    }

    interface RecivedDataFunc {
        void OnRecivedData(String data);
    }

    class ConnectThread extends Thread {
        RecivedDataFunc recivedDataFunc;
        String connectPacket;

        public ConnectThread(String connectPacket, RecivedDataFunc recivedDataFunc) {
            this.connectPacket = connectPacket;
            this.recivedDataFunc = recivedDataFunc;
        }

        public void run() {
            try {
                socket = new Socket("112.154.88.112", 20310);

                AsyncListening(recivedDataFunc);
                AsyncSend(connectPacket);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ListenerThread extends Thread {
        RecivedDataFunc Func;
        byte[] data;

        public ListenerThread(RecivedDataFunc recivedDataFunc) {
            this.Func = recivedDataFunc;
        }

        public void run() {
            try {
                while (true) {
                    InputStream input = socket.getInputStream();

                    byte[] header = new byte[4];
                    int recivedBytes = input.read(header);
                    while (recivedBytes < 4) {
                        input.read(header);
                    }
                    ByteBuffer wrapped = ByteBuffer.wrap(header);
                    int contentsize = wrapped.getInt();

                    data = new byte[contentsize];
                    int recivedContentBytes = 0;
                    while (recivedContentBytes < contentsize) {
                        recivedContentBytes += input.read(data);
                    }
                    runOnUiThread(() -> Func.OnRecivedData(new String(data, StandardCharsets.UTF_8)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class DataThread extends Thread {
        private String data;

        public DataThread(String data) {
            this.data = data;
        }

        public void run() {
            try {
                byte[] contentBuffer = data.getBytes(StandardCharsets.UTF_8);
                byte[] header = ByteBuffer.allocate(4).putInt(contentBuffer.length).array();
                byte[] sendBuffer = new byte[header.length + contentBuffer.length];
                System.arraycopy(header, 0, sendBuffer, 0, header.length);
                System.arraycopy(contentBuffer, 0, sendBuffer, header.length, contentBuffer.length);
                if (socket == null)
                    return;
                OutputStream output = socket.getOutputStream();
                output.write(sendBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    interface DelayFunc {
        void OnDelayed();
    }

    class DelayThread extends Thread {
        DelayFunc delayFunc;
        int delay;

        public DelayThread(int delay, DelayFunc delayFunc) {
            this.delay = delay;
            this.delayFunc = delayFunc;
        }

        public void run() {

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> delayFunc.OnDelayed());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_name:
            case R.id.logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        UserCache.clear(this);
        firebaseAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public <T> String ObjectToJson(T object) {
        Gson json = new Gson();
        return json.toJson(object);
    }

    public void send(String message1) {
        Message msg = new Message(new ChatClient(
                UserCache.getUser(this).getId(),
                UserCache.getUser(this).getName(),
                UserCache.getUser(this).getEmail()),
                message1);
        String msgGson = ObjectToJson(msg);
        AsyncSend(msgGson);
    }
}