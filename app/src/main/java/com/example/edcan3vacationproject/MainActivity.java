package com.example.edcan3vacationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.edcan3vacationproject.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;


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




        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.imgSendBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (binding.editText3.getText().toString() != null || !binding.editText3.getText().toString().equals("")) {
                    PrintWriter out = new PrintWriter(networkWriter, true);
                    String return_msg = binding.editText3.getText().toString();
                    out.println(return_msg);
                }
            }
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
        switch(item.getItemId()){
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


}