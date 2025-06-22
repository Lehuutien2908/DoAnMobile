package com.example.doanmobile.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmobile.R;

import java.util.ArrayList;
import java.util.List;

public class ScheduleManagementActivity extends AppCompatActivity {

    ListView listSchedules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules_admin);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        listSchedules = findViewById(R.id.listSchedules);

        // Dữ liệu lịch chiếu mẫu
        List<String> scheduleList = new ArrayList<>();
        scheduleList.add("Avengers: 10:00 AM - 21/06/2025");
        scheduleList.add("Spider-Man: 13:00 PM - 21/06/2025");
        scheduleList.add("The Batman: 15:30 PM - 21/06/2025");
        scheduleList.add("Oppenheimer: 18:00 PM - 21/06/2025");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                scheduleList
        );
        listSchedules.setAdapter(adapter);
    }
}