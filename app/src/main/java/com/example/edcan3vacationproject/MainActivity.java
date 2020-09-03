package com.example.edcan3vacationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edcan3vacationproject.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.edcan3vacationproject.BR.msg;


public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private ActivityMainBinding binding;
    private ObservableArrayList<Message> items = new ObservableArrayList<>();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        gpsTracker = new GpsTracker(this);
        binding.setItems(items);
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions( Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
        binding.imgSendBtn.setOnClickListener(view -> {
            items.add(new Message(new ChatClient(
                    UserCache.getUser(this).getId(),
                    UserCache.getUser(this).getName(),
                    UserCache.getUser(this).getEmail()), binding.getMessage1()));
            binding.revMain.smoothScrollToPosition(items.size()-1);

           send(binding.getMessage1());
            binding.setMessage1("");

        });
        binding.editText3.setOnEditorActionListener((v, actionId, event) -> {
            if (binding.getMessage1().isEmpty())
                return true;
            items.add(new Message(new ChatClient(
                    UserCache.getUser(this).getId(),
                    UserCache.getUser(this).getName(),
                    UserCache.getUser(this).getEmail()), binding.getMessage1()));
            binding.revMain.smoothScrollToPosition(items.size()-1);

            send(binding.getMessage1());
            binding.setMessage1("");
            return true;
        });

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");
        binding.revMain.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ChatAdapter chatAdapter = new ChatAdapter(UserCache.getUser(this).getEmail());
        binding.revMain.setAdapter(chatAdapter);


//        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//        if (SDK_INT > 8) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
//        double latitude = gpsTracker.getLatitude();
//        double longitude = gpsTracker.getLongitude();
//        Toast.makeText(MainActivity.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();
//        ClientConnected clientConnected = new ClientConnected(new ChatClient(
//                UserCache.getUser(this).getId(),
//                UserCache.getUser(this).getName(),
//                UserCache.getUser(this).getEmail()), new GPSdata(longitude, latitude));
//
//        String ccdString = ObjectToJson(clientConnected);
//
//        Toast.makeText(getApplicationContext(), "Connecting to server...", Toast.LENGTH_SHORT).show();
//        AsyncConnect(ccdString, (string) -> {
//            Gson gson = new Gson();
//            Packet convertedObject = gson.fromJson(string, Packet.class);
//            switch (convertedObject.PacketType) {
//                case Message:
//                    Message recieveMsg = (Message) gson.fromJson(string, Message.class);
//                    items.add(recieveMsg);
//                    binding.revMain.smoothScrollToPosition(items.size()-1);
//                    break;
//                case LinkInfo:
//                    LinkInfo linkInfo = (LinkInfo) gson.fromJson(string, LinkInfo.class);
//                    binding.clientsnum.setText(String.format("현재 %dm 내에 %d명이 있습니다", linkInfo.LinkedClients, linkInfo.SearchRange));
//                    break;
//            }
//        });

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
    protected void onResume() {
        super.onResume();
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Toast.makeText(getApplicationContext(), "Connecting to server...", Toast.LENGTH_SHORT).show();
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        ClientConnected clientConnected = new ClientConnected(new ChatClient(
                UserCache.getUser(this).getId(),
                UserCache.getUser(this).getName(),
                UserCache.getUser(this).getEmail()), new GPSdata(longitude, latitude));
        String ccdString = ObjectToJson(clientConnected);
        AsyncConnect(ccdString, (string) -> {
            Gson gson = new Gson();
            Packet convertedObject = gson.fromJson(string, Packet.class);
            switch (convertedObject.PacketType) {
                case Message:
                    Message recieveMsg = (Message) gson.fromJson(string, Message.class);
                    items.add(recieveMsg);
                    binding.revMain.smoothScrollToPosition(items.size()-1);
                    break;
                case LinkInfo:
                    LinkInfo linkInfo = (LinkInfo) gson.fromJson(string, LinkInfo.class);
                    binding.clientsnum.setText(String.format("현재 %d명이 %dm 내에 있습니다", linkInfo.LinkedClients, linkInfo.SearchRange));
                    break;
            }
        });
    }

    @Override
    protected void onStop() {  //앱 종료시
        super.onStop();
        ClientDisConnect disConnect = new ClientDisConnect();
        String data = ObjectToJson(disConnect);
        AsyncSend(data);
        //AsyncDelay(3000, () -> {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        //});
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
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            finish();
        }


    };
}