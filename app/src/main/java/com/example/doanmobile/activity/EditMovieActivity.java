package com.example.doanmobile.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditMovieActivity extends AppCompatActivity {

    private EditText edtName, edtDuration, edtGenre, edtAge, edtDescription, edtImgBanner, edtImgMovie, edtStatus;
    private ImageView imgBannerPreview, imgMoviePreview;
    private Button btnSave;
    private String movieId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie);

        db = FirebaseFirestore.getInstance();
        movieId = getIntent().getStringExtra("movieId");

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

        // Nút quay lại
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Hiển thị dữ liệu phim cũ nếu có
        if (movieId != null) {
            db.collection("movies").document(movieId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            edtName.setText(documentSnapshot.getString("name"));
                            edtDuration.setText(documentSnapshot.getString("duration"));
                            edtGenre.setText(documentSnapshot.getString("genre"));
                            edtAge.setText(documentSnapshot.getString("age"));
                            edtDescription.setText(documentSnapshot.getString("description"));
                            edtImgBanner.setText(documentSnapshot.getString("imgBanner"));
                            edtImgMovie.setText(documentSnapshot.getString("imgMovie"));
                            edtStatus.setText(documentSnapshot.getString("status"));

                            Glide.with(this).load(documentSnapshot.getString("imgBanner")).into(imgBannerPreview);
                            Glide.with(this).load(documentSnapshot.getString("imgMovie")).into(imgMoviePreview);
                        }
                    });
        }

        // Cập nhật preview ảnh mỗi khi nhập link mới
        edtImgBanner.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                Glide.with(EditMovieActivity.this).load(s.toString()).into(imgBannerPreview);
            }
        });

        edtImgMovie.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                Glide.with(EditMovieActivity.this).load(s.toString()).into(imgMoviePreview);
            }
        });

        // Sự kiện nút Lưu
        btnSave.setOnClickListener(v -> {
            String updatedName = edtName.getText().toString();

            Map<String, Object> update = new HashMap<>();
            update.put("name", updatedName);
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
                            // Ghi log hoạt động
                            Map<String, Object> log = new HashMap<>();
                            log.put("message", "Đã cập nhật phim: " + updatedName);
                            db.collection("activity_logs").add(log);

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
