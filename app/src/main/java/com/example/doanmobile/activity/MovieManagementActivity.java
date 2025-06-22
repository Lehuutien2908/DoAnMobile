package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
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
    private List<Movie> fullMovieList = new ArrayList<>(); // list đầy đủ để search
    private EditText editSearchMovie;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_management_admin);

        recyclerMovies = findViewById(R.id.recyclerMovies);
        recyclerMovies.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdminAdapter(this, movieList);
        recyclerMovies.setAdapter(adapter);

        editSearchMovie = findViewById(R.id.editSearchMovie);

        // Quay lại
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Nút thêm phim
        Button btnAdd = findViewById(R.id.btnAddMovie);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MovieManagementActivity.this, AddMovieActivity.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        loadMoviesFromFirebase();

        // Tìm kiếm realtime
        editSearchMovie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMoviesFromFirebase(); // Refresh lại danh sách khi quay lại
    }

    private void filterMovies(String query) {
        movieList.clear();
        if (query.isEmpty()) {
            movieList.addAll(fullMovieList);
        } else {
            for (Movie m : fullMovieList) {
                if (m.getName() != null && m.getName().toLowerCase().contains(query.toLowerCase())) {
                    movieList.add(m);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadMoviesFromFirebase() {
        db.collection("movies").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    movieList.clear();
                    fullMovieList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Movie movie = doc.toObject(Movie.class);
                        if (movie != null) {
                            movie.setId(doc.getId());
                            movieList.add(movie);
                            fullMovieList.add(movie);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải phim: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
