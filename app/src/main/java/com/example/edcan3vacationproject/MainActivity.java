package com.example.edcan3vacationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.edcan3vacationproject.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import okio.Utf8;


public class MainActivity extends AppCompatActivity {
    private Context mContext;
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


    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();


        try {
            setSocket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.imgSendBtn.setOnClickListener(view -> {
            if (binding.editText3.getText().toString() != null || !binding.editText3.getText().toString().equals(""))
                send();
        });
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        Message message = new Message();
        //ChatClient chatClient =  new ChatClient(UserCache.getUser(m));

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

    public void setSocket(String ip, int port) throws IOException {

        try {
            socket = new Socket(ip, port);
            networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }

    }

    public void send() {
        Message msg = new Message(new ChatClient(
                UserCache.getUser(mContext).getId(),
                UserCache.getUser(mContext).getName(),
                UserCache.getUser(mContext).getEmail()),
                binding.getMessage1().toString());
        String msgGson = MsgToJson(mContext, msg);
        byte[]  arr = msgGson.getBytes(StandardCharsets.UTF_8);
        byte[] header = ByteBuffer.allocate(4).putInt(arr.length).array();
        byte[] content = new byte[header.length + arr.length];
        System.arraycopy(header,0, content, 0, header.length );
        System.arraycopy(arr,0, content, header.length, arr.length );



    }

    public static String MsgToJson(Context context, Message msg) {
        Gson msgGson = new Gson();
        return msgGson.toJson(msg);
    }

}