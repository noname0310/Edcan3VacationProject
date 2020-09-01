package com.example.edcan3vacationproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.edcan3vacationproject.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setEmail("");
        binding.setPw("");

        binding.buttonLoginLogin.setOnClickListener(view -> {
            login(binding.getEmail(), binding.getPw());
        });
        binding.btnLoginSignup.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

    }
    private void login(String email, String pw) {
        if ( email.isEmpty() || pw.isEmpty()) {
            Toast.makeText(this, "빈칸을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseFirestore
                .collection("users")
                            .document(email)
                            .get()
                            .addOnSuccessListener(document -> {
                                firebaseAuth
                                        .signInWithEmailAndPassword(email, pw)
                                        .addOnSuccessListener(runnable1 -> {
                                            UserCache.setUser(this, document.toObject(UserModel.class));
                                            startActivity(new Intent( LoginActivity.this, MainActivity.class));
                                            finish();
                                        })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
    }
}
