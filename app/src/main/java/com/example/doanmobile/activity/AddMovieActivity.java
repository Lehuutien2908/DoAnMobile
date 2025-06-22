package com.example.doanmobile.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddMovieActivity extends AppCompatActivity {

    private EditText addName, addDuration, addGenre, addAge, addDescription;
    private EditText addImgBanner, addImgPoster, addStatus;
    private ImageView imgBannerPreview, imgPosterPreview;
    private Button btnSave;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        // Ánh xạ view
        addName = findViewById(R.id.add_name);
        addDuration = findViewById(R.id.add_duration);
        addGenre = findViewById(R.id.add_genre);
        addAge = findViewById(R.id.add_age);
        addDescription = findViewById(R.id.add_description);
        addImgBanner = findViewById(R.id.add_img_banner);
        addImgPoster = findViewById(R.id.add_img_poster);
        addStatus = findViewById(R.id.add_status);
        imgBannerPreview = findViewById(R.id.img_banner_preview);
        imgPosterPreview = findViewById(R.id.img_poster_preview);
        btnSave = findViewById(R.id.btn_save);

        db = FirebaseFirestore.getInstance();
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        // Preview ảnh khi nhập URL
        setupImagePreview();

        // Xử lý nút Thêm phim
        btnSave.setOnClickListener(v -> addMovieToFirestore());
    }

    private void setupImagePreview() {
        addImgBanner.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Glide.with(this).load(addImgBanner.getText().toString()).into(imgBannerPreview);
            }
        });

        addImgPoster.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Glide.with(this).load(addImgPoster.getText().toString()).into(imgPosterPreview);
            }
        });
    }

    private void addMovieToFirestore() {
        String id = UUID.randomUUID().toString();
        String name = addName.getText().toString().trim();
        String duration = addDuration.getText().toString().trim();
        String genre = addGenre.getText().toString().trim();
        String age = addAge.getText().toString().trim();
        String description = addDescription.getText().toString().trim();
        String imgBanner = addImgBanner.getText().toString().trim();
        String imgPoster = addImgPoster.getText().toString().trim();
        String status = addStatus.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(duration)) {
            Toast.makeText(this, "Vui lòng nhập tên và thời lượng phim!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> movie = new HashMap<>();
        movie.put("name", name);
        movie.put("duration", duration);
        movie.put("genre", genre);
        movie.put("age", age);
        movie.put("description", description);
        movie.put("imgBanner", imgBanner);
        movie.put("imgMovie", imgPoster);
        movie.put("status", status);

        db.collection("movies").document(id)
                .set(movie)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Thêm phim thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
