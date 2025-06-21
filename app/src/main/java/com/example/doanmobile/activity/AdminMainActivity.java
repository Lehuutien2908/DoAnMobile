package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmobile.R;

public class AdminMainActivity extends AppCompatActivity {

    View layoutMovie, layoutSchedule, layoutRevenue;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        layoutRevenue = findViewById(R.id.layoutDashboard);
        layoutSchedule = findViewById(R.id.layoutSchedule);
        layoutMovie = findViewById(R.id.layoutMovieManagement);
        btnLogout = findViewById(R.id.btnLogout);

        layoutMovie.setOnClickListener(v -> startActivity(new Intent(this, MovieManagementActivity.class)));

        layoutSchedule.setOnClickListener(v -> startActivity(new Intent(this, ScheduleManagementActivity.class)));

        layoutRevenue.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));

        btnLogout.setOnClickListener(v -> finishAffinity());
    }
}
