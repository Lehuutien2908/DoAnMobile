package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.example.doanmobile.adapter.MovieAdapter;
import com.example.doanmobile.model.Movie;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout bannerContainer;
    private HorizontalScrollView bannerScrollView;
    private List<ImageView> bannerViews = new ArrayList<>();
    private Handler autoScrollHandler = new Handler();
    private int currentBannerIndex = 0;
    private final int scrollInterval = 3000; // 3 giây

    private RecyclerView rvMovies;
    private MovieAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();

    private TextView tabDangChieu, tabSapChieu;
    private String currentStatus = "dang_chieu";
    private TextView btnXemTiep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        bannerContainer = findViewById(R.id.bannerContainer);
        bannerScrollView = findViewById(R.id.bannerScrollView);

        loadBannersFromFirestore();

        // Tab
        rvMovies     = findViewById(R.id.rvMovies);
        tabDangChieu = findViewById(R.id.tab_dang_chieu);
        tabSapChieu = findViewById(R.id.tab_sap_chieu);


        //RecyclerView + Adapter
        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MovieAdapter(this, movieList);
        rvMovies.setAdapter(adapter);


        //Load lần đầu (“Đang chiếu”)
        loadMovies(currentStatus);
        highlightTab();
        //Xử lý bấm tab
        tabDangChieu.setOnClickListener(v -> {
            currentStatus = "dang_chieu";
            highlightTab();
            loadMovies(currentStatus);
        });

        tabSapChieu.setOnClickListener(v -> {
            currentStatus = "sap_chieu";
            highlightTab();
            loadMovies(currentStatus);
        });
//thanh điều hướng
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_home);  // đang ở Trang chủ
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true; // Đã ở đây rồi
            } else if (id == R.id.nav_theater) {
                startActivity(new Intent(this, TheaterActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_movie) {
                startActivity(new Intent(this, MovieListActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(this, AccountActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
        // nút xem tiếp ở dưới
        btnXemTiep = findViewById(R.id.btnXemTiep);
        btnXemTiep.setOnClickListener(v -> {
            bottomNav.setSelectedItemId(R.id.nav_movie);
        });


    }
    private void highlightTab() {
        boolean isDang = "dang_chieu".equals(currentStatus);
        tabDangChieu.setTextColor(getColor(isDang ? R.color.black : R.color.darker_gray));
        tabSapChieu.setTextColor(getColor(!isDang ? R.color.black : R.color.darker_gray));
    }
    private void loadMovies(String status) {
        FirebaseFirestore.getInstance().collection("movies")
                .whereEqualTo("status", status)
                .limit(6)
                .get()
                .addOnSuccessListener(snapshot -> {
                    movieList.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {

                        Movie m = doc.toObject(Movie.class);
                        m.setId(doc.getId());          // Lưu documentId
                        movieList.add(m);

                    }
                    adapter.notifyDataSetChanged();
                    rvMovies.post(() -> { rvMovies.requestLayout(); });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải phim: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void loadBannersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference bannerRef = db.collection("banner");

        bannerRef.get().addOnSuccessListener(querySnapshot -> {
            for (QueryDocumentSnapshot doc : querySnapshot) {
                String url = doc.getString("imgBanner");
                if (url != null) {
                    addBanner(url);
                }
            }

            if (!bannerViews.isEmpty()) {
                startAutoScroll();
            }
        }).addOnFailureListener(e -> {
            // Log hoặc thông báo nếu cần
        });
    }

    private void addBanner(String url) {
        ImageView img = new ImageView(this);

        int width = dpToPx(350);
        int marginStart = dpToPx(25);
        int marginEnd = dpToPx(35);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(marginStart, 0, marginEnd, 0);

        img.setLayoutParams(params);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(this).load(url).into(img);

        bannerContainer.addView(img);
        bannerViews.add(img);
    }
    private void startAutoScroll() {
        autoScrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bannerViews.isEmpty()) return;

                ImageView currentView = bannerViews.get(currentBannerIndex);
                bannerScrollView.smoothScrollTo(currentView.getLeft(), 0);

                currentBannerIndex = (currentBannerIndex + 1) % bannerViews.size();
                autoScrollHandler.postDelayed(this, scrollInterval);
            }
        }, scrollInterval);
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoScrollHandler.removeCallbacksAndMessages(null); // ngưng auto-scroll
    }

}