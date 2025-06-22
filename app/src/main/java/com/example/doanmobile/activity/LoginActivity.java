package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmobile.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button   loginBtn;
    private TextView toRegister, forgotPassword;

    private FirebaseAuth      auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ====== ÁNH XẠ VIEW ======
        emailInput      = findViewById(R.id.emailInput);
        passwordInput   = findViewById(R.id.passwordInput);
        loginBtn        = findViewById(R.id.loginBtn);
        toRegister      = findViewById(R.id.toRegister);
        forgotPassword  = findViewById(R.id.forgotPassword);

        // ====== FIREBASE ======
        auth      = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // ====== SỰ KIỆN ======
        loginBtn.setOnClickListener(v -> handleLogin());
        toRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
        forgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private void handleLogin() {
        String email    = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Sai tài khoản hoặc mật khẩu",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Đăng nhập thành công ⇒ lấy role
                    String uid = auth.getCurrentUser().getUid();

                    firestore.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(doc -> {
                                String role = doc.exists() ? doc.getString("role") : "user";
                                if ("admin".equals(role)) {
                                    startActivity(new Intent(this, AdminMainActivity.class));
                                } else {
                                    startActivity(new Intent(this, MainActivity.class));
                                }
                                finish();  // đóng LoginActivity
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            "Lỗi lấy thông tin người dùng: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show());
                });
    }
}
