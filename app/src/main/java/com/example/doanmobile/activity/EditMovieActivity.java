package com.example.doanmobile.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.example.doanmobile.model.Movie;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditMovieActivity extends AppCompatActivity {

    EditText edtName, edtDuration, edtGenre, edtAge, edtDescription, edtImgBanner, edtImgMovie, edtStatus;
    ImageView imgBannerPreview, imgMoviePreview;
    Button btnSave;
    FirebaseFirestore db;
    String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie);

        edtName = findViewById(R.id.edtName);
        edtDuration = findViewById(R.id.edtDuration);
        edtGenre = findViewById(R.id.edtGenre);
        edtAge = findViewById(R.id.edtAge);
        edtDescription = findViewById(R.id.edtDescription);
        edtImgBanner = findViewById(R.id.edtImgBanner);
        edtImgMovie = findViewById(R.id.edtImgMovie);
        edtStatus = findViewById(R.id.edtStatus);
        imgBannerPreview = findViewById(R.id.imgBannerPreview);
        imgMoviePreview = findViewById(R.id.imgMoviePreview);
        btnSave = findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();

        Movie movie = (Movie) getIntent().getSerializableExtra("movie");
        movieId = getIntent().getStringExtra("movieId");

        if (movie != null) {
            edtName.setText(movie.getName());
            edtDuration.setText(movie.getDuration());
            edtGenre.setText(movie.getGenre());
            edtAge.setText(movie.getAge());
            edtDescription.setText(movie.getDescription());
            edtImgBanner.setText(movie.getImgBanner());
            edtImgMovie.setText(movie.getImgMovie());
            edtStatus.setText(movie.getStatus());

            Glide.with(this)
                    .load(movie.getImgBanner())
                    .placeholder(R.drawable.placeholder_image)
                    .into(imgBannerPreview);

            Glide.with(this)
                    .load(movie.getImgMovie())
                    .placeholder(R.drawable.placeholder_image)
                    .into(imgMoviePreview);
        }

        btnSave.setOnClickListener(v -> {
            Map<String, Object> update = new HashMap<>();
            update.put("name", edtName.getText().toString());
            update.put("duration", edtDuration.getText().toString());
            update.put("genre", edtGenre.getText().toString());
            update.put("age", edtAge.getText().toString());
            update.put("description", edtDescription.getText().toString());
            update.put("imgBanner", edtImgBanner.getText().toString());
            update.put("imgMovie", edtImgMovie.getText().toString());
            update.put("status", edtStatus.getText().toString());

            if (movieId != null) {
                DocumentReference docRef = db.collection("movies").document(movieId);
                docRef.update(update)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
