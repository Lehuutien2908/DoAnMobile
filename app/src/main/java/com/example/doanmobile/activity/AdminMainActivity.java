package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.doanmobile.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminMainActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    CardView layoutMovieManagement, layoutSchedule, layoutDashboard, layoutUserList;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Ánh xạ view
        layoutMovieManagement = findViewById(R.id.layoutMovieManagement);
        layoutSchedule = findViewById(R.id.layoutSchedule);
        layoutDashboard = findViewById(R.id.layoutDashboard);
        layoutUserList = findViewById(R.id.layoutUserList); // Thêm dòng này
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

        // Xử lý sự kiện mở Danh sách người dùng
        layoutUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this,UserListAdminActivity.class);
                startActivity(intent);
            }
        });

        // Đăng xuất
        auth = FirebaseAuth.getInstance();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class); // hoặc MainActivity nếu muốn về trang chính
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear stack, không quay lại bằng back
                startActivity(intent);
                finish();
            }
        });

    }
}
