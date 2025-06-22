package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.R;
import com.example.doanmobile.adapter.MovieAdapter;
import com.example.doanmobile.adapter.MovieListAdapter;
import com.example.doanmobile.model.Movie;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    private RecyclerView movieRecyclerView;
    private MovieListAdapter movieAdapter;
    private List<Movie> movieList;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // Cài đặt Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo
        db = FirebaseFirestore.getInstance();
        movieRecyclerView = findViewById(R.id.movieListRecyclerView);
        movieRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Dạng lưới 2 cột

        movieList = new ArrayList<>();

        movieAdapter = new MovieListAdapter(this, movieList);
        movieRecyclerView.setAdapter(movieAdapter);

        loadAllMovies();

        // Xử lý Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.nav_movie); // Đặt mục này là đang được chọn

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_theater) {
                startActivity(new Intent(getApplicationContext(), TheaterActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_movie) {
                return true; // Đang ở đây
            } else if (itemId == R.id.nav_account) {
                  startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                  overridePendingTransition(0, 0);
                  finish();
                return true;
            }
            return false;
        });
    }

    private void loadAllMovies() {
        db.collection("movies")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        movieList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Movie movie = document.toObject(Movie.class);
                            movie.setId(document.getId()); // Lưu ID để chuyển trang chi tiết
                            movieList.add(movie);
                        }
                        movieAdapter.notifyDataSetChanged();
                        Log.d("MovieListActivity", "Loaded " + movieList.size() + " total movies.");
                    } else {
                        Log.e("MovieListActivity", "Error loading movies: ", task.getException());
                        Toast.makeText(MovieListActivity.this, "Lỗi tải danh sách phim.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
