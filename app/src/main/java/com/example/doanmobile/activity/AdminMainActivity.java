package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.doanmobile.R;

public class AdminMainActivity extends AppCompatActivity {

    CardView layoutMovieManagement, layoutSchedule, layoutDashboard;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Ánh xạ view
        layoutMovieManagement = findViewById(R.id.layoutMovieManagement);
        layoutSchedule = findViewById(R.id.layoutSchedule);
        layoutDashboard = findViewById(R.id.layoutDashboard);
        btnLogout = findViewById(R.id.btnLogout);

        // Xử lý sự kiện mở Quản lý phim
        layoutMovieManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, MovieManagementActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện mở Quản lý lịch chiếu
        layoutSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, ScheduleManagementActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện mở Dashboard doanh thu
        layoutDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

    }
}
