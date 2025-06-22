package com.example.doanmobile.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.example.doanmobile.adapter.MovieListAdapter;
import com.example.doanmobile.model.Movie;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CinemaDetailActivity extends AppCompatActivity {

    private TextView tvCinemaName, tvCinemaAddress;
    private ImageView ivCinemaImage;
    private RecyclerView moviesRecyclerView;
    private MovieListAdapter movieAdapter;
    private List<Movie> movieList;
    private FirebaseFirestore db;

    private String cinemaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chi Tiết Rạp");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvCinemaName = findViewById(R.id.cinema_name);
        tvCinemaAddress = findViewById(R.id.cinema_address);
        ivCinemaImage = findViewById(R.id.cinema_image);
        moviesRecyclerView = findViewById(R.id.movies_recycler_view);

        db = FirebaseFirestore.getInstance();
        movieList = new ArrayList<>();
        // Reuse MovieListAdapter as it fits the purpose of displaying movie items
        movieAdapter = new MovieListAdapter(this, movieList);
        moviesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        moviesRecyclerView.setAdapter(movieAdapter);

        // Get data from Intent
        if (getIntent() != null) {
            cinemaId = getIntent().getStringExtra("cinemaId");
            String cinemaName = getIntent().getStringExtra("cinemaName");
            String cinemaAddress = getIntent().getStringExtra("cinemaAddress");
            String cinemaImage = getIntent().getStringExtra("cinemaImage");

            tvCinemaName.setText(cinemaName);
            tvCinemaAddress.setText(cinemaAddress);
            if (cinemaImage != null && !cinemaImage.isEmpty()) {
                Glide.with(this).load(cinemaImage).into(ivCinemaImage);
            } else {
                ivCinemaImage.setImageResource(R.drawable.placeholder_image); // Default image
            }

            if (cinemaId != null) {
                loadMoviesForCinema(cinemaId);
            } else {
                Toast.makeText(this, "Không tìm thấy ID rạp.", Toast.LENGTH_SHORT).show();
                Log.e("CinemaDetailActivity", "Cinema ID is null");
            }
        } else {
            Toast.makeText(this, "Không có dữ liệu rạp.", Toast.LENGTH_SHORT).show();
            Log.e("CinemaDetailActivity", "Intent is null");
        }
    }

    private void loadMoviesForCinema(String cinemaId) {
        db.collection("showtimes")
                .whereEqualTo("cinemasId", cinemaId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Set<String> movieIds = new HashSet<>(); // Use a Set to store unique movie IDs
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String movieId = document.getString("movieId");
                            if (movieId != null) {
                                movieIds.add(movieId);
                            }
                        }
                        fetchMovieDetails(new ArrayList<>(movieIds));
                    } else {
                        Log.e("CinemaDetailActivity", "Error getting showtimes: ", task.getException());
                        Toast.makeText(CinemaDetailActivity.this, "Lỗi tải suất chiếu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchMovieDetails(List<String> movieIds) {
        movieList.clear();
        if (movieIds.isEmpty()) {
            movieAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Không có phim nào đang chiếu tại rạp này.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String movieId : movieIds) {
            db.collection("movies").document(movieId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Movie movie = documentSnapshot.toObject(Movie.class);
                            if (movie != null) {
                                movieList.add(movie);
                                movieAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CinemaDetailActivity", "Error fetching movie details for ID " + movieId + ": " + e.getMessage());
                    });
        }
    }
}
