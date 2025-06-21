package com.example.doanmobile.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MovieDetailActivity extends AppCompatActivity {
    private ImageView imgBanner;
    private TextView tvTitle, tvAge, tvInfo, tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        // === Ánh xạ view ===
        imgBanner = findViewById(R.id.moviePoster);       // ảnh ngang
        tvTitle   = findViewById(R.id.movieTitle);
        tvAge     = findViewById(R.id.movieAgeLabel);
        tvInfo    = findViewById(R.id.movieInfo);
        tvDesc    = findViewById(R.id.movieDescription);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // === NHẬN ID PHIM GỬI SANG ===
        String movieId = getIntent().getStringExtra("movieId");   // <-- dòng này
        if (movieId == null) {
            Toast.makeText(this, "Không tìm thấy ID phim", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // === TẢI CHI TIẾT PHIM TỪ FIRESTORE ===
        loadMovieDetail(movieId);

        // === Nút quay lại ===
        btnBack.setOnClickListener(v -> finish());
    }
    private void loadMovieDetail(String movieId) {
        FirebaseFirestore.getInstance()
                .collection("movies")
                .document(movieId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "Phim không tồn tại", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    Glide.with(this)
                            .load(doc.getString("imgBanner"))   // ảnh ngang
                            .into(imgBanner);

                    tvTitle.setText(doc.getString("name"));
                    tvAge.setText(doc.getString("age"));     // ví dụ: T16
                    tvInfo.setText("Thể loại: " + doc.getString("genre")
                            + " | Thời lượng: " + doc.getString("duration") + " phút");
                    tvDesc.setText(doc.getString("description"));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải chi tiết: "+e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
