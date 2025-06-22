package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmobile.R;
import com.example.doanmobile.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Button registerBtn;
    private TextView toLogin;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // file bạn vừa gửi

        // Ánh xạ
        emailInput           = findViewById(R.id.emailInput);
        passwordInput        = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerBtn          = findViewById(R.id.registerBtn);
        toLogin              = findViewById(R.id.toLogin);

        // Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        registerBtn.setOnClickListener(v -> handleRegister());

        toLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void handleRegister() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (!validate(email, password, confirmPassword)) return;

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Đăng ký thất bại: " +
                                        (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String uid = auth.getCurrentUser().getUid();
                    String hashedPassword = sha256(password); // hoặc không lưu nếu không cần

                    UserModel user = new UserModel(uid, email, hashedPassword); // role = "user"

                    firestore.collection("users")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this,
                                    "Lưu thông tin thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                });
    }

    private boolean validate(String email, String pw, String confirmPw) {
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (pw.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải ≥ 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!pw.equals(confirmPw)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
}
