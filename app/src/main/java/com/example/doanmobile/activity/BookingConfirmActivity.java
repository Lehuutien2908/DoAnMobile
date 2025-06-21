package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmobile.R;

public class BookingConfirmActivity extends AppCompatActivity {

    private Button btnGoHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirm);

        btnGoHome = findViewById(R.id.btnGoHome);

        btnGoHome.setOnClickListener(v -> {
            // Chuyển về MainActivity (hoặc activity trang chủ bạn đang dùng)
            Intent intent = new Intent(BookingConfirmActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // đóng luôn BookingConfirmActivity
        });
    }
}
