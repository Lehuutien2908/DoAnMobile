package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.R;
import com.example.doanmobile.adapter.TheaterAdapter;
import com.example.doanmobile.model.Theater;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TheaterActivity extends AppCompatActivity {

    private RecyclerView theaterRecyclerView;
    private TheaterAdapter theaterAdapter;
    private List<Theater> theaterList;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater);

        // Cài đặt Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Rạp Phim");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo các thành phần
        db = FirebaseFirestore.getInstance();
        theaterRecyclerView = findViewById(R.id.theaterRecyclerView);
        theaterRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        theaterList = new ArrayList<>();
        theaterAdapter = new TheaterAdapter(theaterList);
        theaterRecyclerView.setAdapter(theaterAdapter);

        // Bắt đầu tải dữ liệu
        loadTheaters();

        // Xử lý Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.nav_theater); // Đặt mục này là mục đang được chọn

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Chuyển về MainActivity
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (itemId == R.id.nav_movie) {
                // Chuyển sang MovieListActivity
                startActivity(new Intent(getApplicationContext(), MovieListActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_theater) {
                // Đang ở đây rồi, không làm gì cả
                return true;
            } else if (itemId == R.id.nav_account) {

                return true;
            }
            return false;
        });
    }

    private void loadTheaters() {
        db.collection("cinemas") // Lấy dữ liệu từ collection "cinemas"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        theaterList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Theater theater = document.toObject(Theater.class);
                            theaterList.add(theater);
                        }
                        theaterAdapter.notifyDataSetChanged();
                        Log.d("TheaterActivity", "Loaded " + theaterList.size() + " theaters.");
                    } else {
                        Log.e("TheaterActivity", "Error loading theaters: ", task.getException());
                        Toast.makeText(TheaterActivity.this, "Lỗi tải danh sách rạp.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
