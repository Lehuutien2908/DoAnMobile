package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.R;
import com.example.doanmobile.adapter.MovieAdminAdapter;
import com.example.doanmobile.model.Movie;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MovieManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerMovies;
    private MovieAdminAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_management_admin);

        // Khởi tạo view
        recyclerMovies = findViewById(R.id.recyclerMovies);
        recyclerMovies.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdminAdapter(this, movieList);
        recyclerMovies.setAdapter(adapter);

        // Quay lại
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Nút thêm phim
        Button btnAdd = findViewById(R.id.btnAddMovie);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MovieManagementActivity.this, AddMovieActivity.class);
            startActivity(intent);
        });
        // Firebase
        db = FirebaseFirestore.getInstance();
        loadMoviesFromFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMoviesFromFirebase(); // Refresh khi quay lại
    }

    private void loadMoviesFromFirebase() {
        db.collection("movies").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    movieList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Movie movie = doc.toObject(Movie.class);
                        if (movie != null) {
                            movie.setId(doc.getId());
                            movieList.add(movie);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải phim: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
