package com.example.edcan3vacationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;


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


    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private ActivityMainBinding binding;
    private String html = "";
    private Handler mHandler;

    private Socket socket;

    private BufferedReader networkReader;
    private BufferedWriter networkWriter;

    private String ip = " 127.0.0.1";
    private int port = 20310;
    private ObservableArrayList<Message> items = new ObservableArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        GpsTracker gpsTracker = new GpsTracker(this);
//        TimerTask tt = new TimerTask() {
//            public void run() {
//                GPSdata gpsdata = new GPSdata(gpsTracker.getLongitude(), gpsTracker.getLongitude());
//                GPS gps = new GPS(gpsdata);
//                String GPSlastdata = GpsToJson(gps);
//                AsyncSend(GPSlastdata);
//            }
//        };


//        Timer timer = new Timer();
//        timer.schedule(tt, 0, 10000);

        super.onCreate(savedInstanceState);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        ClientConnected clientConnected = new ClientConnected(new ChatClient(
                UserCache.getUser(this).getId(),
                UserCache.getUser(this).getName(),
                UserCache.getUser(this).getEmail()), new GPSdata(0, 0));
        String ccdString = ObjectToJson(clientConnected);
        AsyncConnect(ccdString, (string) -> {
            Gson gson = new Gson();
            Packet convertedObject = (Packet) new Gson().fromJson(string, Packet.class);
            switch (convertedObject.PacketType) {
                case Message:
                    Message recieveMsg = (Message) new Gson().fromJson(string, Message.class);
                    items.add(recieveMsg);
                    break;
            }
        });


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setItems(items);
        binding.imgSendBtn.setOnClickListener(view -> {
            if (binding.editText3.getText().toString() != null || !binding.editText3.getText().toString().equals(""))
                send(binding.getMessage1().toString());
        });
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        runOnUiThread(() -> {
            Message message = new Message();
        });


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
//

    @Override
    protected void onStop() {  //앱 종료시
        super.onStop();
        ClientDisConnect disConnect = new ClientDisConnect();
        String data = DsctToJson(disConnect);
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

    interface DelayFunc {
        void OnDelayed();
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

    class ListenerThread extends Thread {
        RecivedDataFunc Func;
        byte[] data;

        public ListenerThread(RecivedDataFunc recivedDataFunc) {
            this.Func = recivedDataFunc;
        }

        public String byteArrayToHex(byte[] a) {
            StringBuilder sb = new StringBuilder();
            for (final byte b : a)
                sb.append(String.format("%02x ", b & 0xff));
            return sb.toString();
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
                OutputStream output = socket.getOutputStream();
                output.write(sendBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String DsctToJson(ClientDisConnect clientDisConnect) {
        Gson msgGson = new Gson();
        return msgGson.toJson(clientDisConnect);
    }

    public static String GpsToJson(GPS gps) {
        Gson msgGson = new Gson();
        return msgGson.toJson(gps);
    }

    public static String ObjectToJson(ClientConnected ccd) {
        Gson msgGson = new Gson();
        return msgGson.toJson(ccd);
    }

    public static String MsgToJson(Message msg) {
        Gson msgGson = new Gson();
        return msgGson.toJson(msg);
    }

    public void send(String message1) {

        Message msg = new Message(new ChatClient(
                UserCache.getUser(this).getId(),
                UserCache.getUser(this).getName(),
                UserCache.getUser(this).getEmail()),
                message1);
        String msgGson = MsgToJson(msg);
        AsyncSend(msgGson);
    }
}