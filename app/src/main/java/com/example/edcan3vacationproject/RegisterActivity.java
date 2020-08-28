package com.example.edcan3vacationproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.Toast;

import com.example.edcan3vacationproject.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.setId("");
        binding.setName("");
        binding.setEmail("");
        binding.setPw("");
        binding.setPwcheck("");
        binding.btnRegiSignup.setOnClickListener(view -> {
            register(binding.getId(), binding.getName(), binding.getEmail(), binding.getPw(), binding.getPwcheck());
        });
    }

    private void register(String id, String name, String email, String pw, String pwcheck) {
        if (id.isEmpty()||name.isEmpty() || email.isEmpty() || pw.isEmpty() || pwcheck.isEmpty()) {
            Toast.makeText(this, "빈칸을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pw.equals(pwcheck)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseFirestore
                .collection("users")
                .document(email)
                .set(new UserModel(id, name, email, getTime()))
                .addOnSuccessListener(runnable -> {
                    firebaseAuth
                            .createUserWithEmailAndPassword(email, pw)
                            .addOnSuccessListener(runnable1 -> {
                                Toast.makeText(this, "정상적으로 가입되었습니다!", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());

    }
    private String getTime(){
        return new SimpleDateFormat("yyyy/MM/dd hh:mm aa", Locale.ENGLISH).format( new Date());
    }
}