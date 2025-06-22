package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MovieDetailActivity extends AppCompatActivity {
    private ImageView imgBanner;
    private TextView tvTitle, tvAge, tvInfo, tvDesc;
    private LinearLayout dateContainer;
    private RadioGroup cinemaGroup;
    private ChipGroup chipGroupTimes;
    private Button btnDatVe;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String moviePosterUrl;
    private String movieId;
    private String selectedDate;
    private String selectedCinemaId;
    private String selectedTime;
    private final Map<String, Map<String, List<String>>> showtimeMap = new LinkedHashMap<>();
    // endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Ánh xạ view ...
        imgBanner = findViewById(R.id.moviePoster);
        tvTitle   = findViewById(R.id.movieTitle);
        tvAge     = findViewById(R.id.movieAgeLabel);
        tvInfo    = findViewById(R.id.movieInfo);
        tvDesc    = findViewById(R.id.movieDescription);

        dateContainer   = findViewById(R.id.dateContainer);
        cinemaGroup     = findViewById(R.id.cinemaGroup);
        chipGroupTimes  = findViewById(R.id.chipGroupTimes);
        btnDatVe        = findViewById(R.id.btnDatVe);
        ImageButton btnBack = findViewById(R.id.btnBack);

        movieId = getIntent().getStringExtra("movieId");
        Log.d("MovieDetailActivity", "Received movieId: " + movieId);

        if (movieId == null) {
            Toast.makeText(this, "Không tìm thấy ID phim", Toast.LENGTH_SHORT).show();
            finish(); return;
        }


        loadMovieDetail(movieId);
        loadShowtimes();

        btnBack.setOnClickListener(v -> finish());

        btnDatVe.setOnClickListener(v -> {
            if (selectedDate == null || selectedCinemaId == null || selectedTime == null) {
                Toast.makeText(this, "Vui lòng chọn ngày, rạp và giờ chiếu!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, SeatBookingActivity.class);
            intent.putExtra("movieId", movieId);
            intent.putExtra("cinemaId", selectedCinemaId);
            intent.putExtra("date", selectedDate);
            intent.putExtra("time", selectedTime);
            Log.d("MovieDetailActivity", "URL being passed to SeatBookingActivity: " + moviePosterUrl);
            intent.putExtra("posterUrl",moviePosterUrl);
            startActivity(intent);
        });

    }
    private void loadShowtimes() {
        if (movieId == null || movieId.isEmpty()) {
            Log.e("MovieDetailActivity", "Movie ID is null, cannot fetch showtimes.");
            return;
        }

        db.collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .orderBy("date")
                .addSnapshotListener((querySnapshot, error) -> {
                    // 1. Xử lý lỗi kết nối hoặc quyền truy cập
                    if (error != null) {
                        Log.e("MovieDetailActivity", "Firestore listen failed", error);
                        Toast.makeText(MovieDetailActivity.this, "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (querySnapshot == null) {
                        Log.w("MovieDetailActivity", "Query snapshot came back null.");
                        return;
                    }

                    Log.d("MovieDetailActivity", "Firestore returned " + querySnapshot.size() + " documents.");

                    // 2. Xử lý và lọc dữ liệu
                    showtimeMap.clear(); // Luôn xóa dữ liệu cũ khi có cập nhật mới
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date today = new Date();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Timestamp timestamp = doc.getTimestamp("date");
                        if (timestamp == null || timestamp.toDate().before(today)) {
                            continue; // Bỏ qua nếu không có ngày hoặc ngày đã qua
                        }

                        String dateKey = sdf.format(timestamp.toDate());
                        String cinemaId = doc.getString("cinemasId");
                        List<String> times = (List<String>) doc.get("times");

                        if (cinemaId == null || times == null || times.isEmpty()) {
                            continue; // Bỏ qua nếu thiếu dữ liệu
                        }

                        // Gộp và loại bỏ các giờ chiếu trùng lặp
                        showtimeMap
                                .computeIfAbsent(dateKey, k -> new LinkedHashMap<>())
                                .computeIfAbsent(cinemaId, k -> new ArrayList<>())
                                .addAll(times);
                    }
                    
                    // Đảm bảo các giờ chiếu là duy nhất
                    showtimeMap.values().forEach(map ->
                            map.replaceAll((k, v) -> new ArrayList<>(new LinkedHashSet<>(v))));

                    Log.d("MovieDetailActivity", "Processed data, found " + showtimeMap.size() + " available dates.");

                    // 3. Cập nhật giao diện
                    if (!showtimeMap.isEmpty()) {
                        buildDateButtons();
                    } else {
                        // Nếu không có suất chiếu nào, dọn dẹp UI và thông báo
                        dateContainer.removeAllViews();
                        cinemaGroup.removeAllViews();
                        chipGroupTimes.removeAllViews();
                        Toast.makeText(this, "Không có suất chiếu sắp tới", Toast.LENGTH_SHORT).show();
                        Log.w("MovieDetailActivity", "No valid showtimes found after filtering.");
                    }
                });
    }

    private void buildDateButtons() {
        dateContainer.removeAllViews();
        SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat out = new SimpleDateFormat("EEE\ndd/MM", new Locale("vi"));

        int i = 0;
        for (String dateKey : showtimeMap.keySet()) {
            TextView tv = new TextView(this);
            tv.setText(out.format(new Date(parseDate(in, dateKey))));
            tv.setPadding(32, 16, 32, 16);
            tv.setBackgroundResource(R.drawable.bg_date_unselected);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTag(dateKey);
            int index = i++;
            tv.setOnClickListener(v -> selectDate(dateKey));
            dateContainer.addView(tv);

            if (index == 0) selectDate(dateKey);
        }
    }

    private long parseDate(SimpleDateFormat sdf, String key) {
        try {
            return sdf.parse(key).getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    private void selectDate(String dateKey) {
        selectedDate = dateKey;
        for (int i = 0; i < dateContainer.getChildCount(); i++) {
            View v = dateContainer.getChildAt(i);
            v.setBackgroundResource(v.getTag().equals(dateKey) ?
                    R.drawable.bg_date_selected : R.drawable.bg_date_unselected);
        }
        buildCinemaGroup();
    }

    private void buildCinemaGroup() {
        cinemaGroup.setOnCheckedChangeListener(null);
        cinemaGroup.removeAllViews();

        Map<String, List<String>> cinemasToday = showtimeMap.get(selectedDate);
        if (cinemasToday == null || cinemasToday.isEmpty()) {
            Log.w("MovieDetailActivity", "No cinemas found for date: " + selectedDate);
            return;
        }

        int i = 0;
        for (String cinemaId : cinemasToday.keySet()) {
            Log.d("MovieDetailActivity", "Building radio button for cinemaId: " + cinemaId);

            final RadioButton rb = new RadioButton(this);
            rb.setTag(cinemaId);
            rb.setId(View.generateViewId());
            rb.setTextSize(14);
            rb.setTextColor(ContextCompat.getColor(this, R.color.black));
            rb.setText("Đang tải tên rạp..."); // Thay đổi text mặc định

            // Lấy tên rạp bất đồng bộ
            db.collection("cinemas").document(cinemaId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String cinemaName = doc.getString("name");
                            rb.setText(cinemaName);
                            Log.d("MovieDetailActivity", "Successfully fetched cinema name: " + cinemaName);
                        } else {
                            rb.setText("Rạp không rõ (ID: " + cinemaId + ")"); // Hiển thị ID để dễ debug
                            Log.w("MovieDetailActivity", "Cinema document not found for ID: " + cinemaId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        rb.setText("Lỗi tải tên rạp");
                        Log.e("MovieDetailActivity", "Error fetching cinema name for ID: " + cinemaId, e);
                    });

            cinemaGroup.addView(rb);

            if (i++ == 0) {
                rb.setChecked(true);
            }
        }

        // Khi người dùng chọn rạp
        cinemaGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = group.findViewById(checkedId);
            if (rb != null) {
                selectedCinemaId = (String) rb.getTag();
                buildTimeChips();
            }
        });

        // Gọi lần đầu cho rạp mặc định
        if (cinemaGroup.getChildCount() > 0) {
            RadioButton first = (RadioButton) cinemaGroup.getChildAt(0);
            selectedCinemaId = (String) first.getTag();
            buildTimeChips();
        }
    }

    private void buildTimeChips() {
        chipGroupTimes.removeAllViews();
        List<String> times = showtimeMap.get(selectedDate).get(selectedCinemaId);
        if (times == null) return;

        int i = 0;
        for (String time : times) {
            Chip chip = new Chip(this);
            chip.setText(time);
            chip.setTag(time);
            chip.setCheckable(true);
            chip.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelLarge);
            chipGroupTimes.addView(chip);
            if (i++ == 0) chip.setChecked(true);
        }

        chipGroupTimes.setOnCheckedStateChangeListener((group, ids) -> {
            if (ids.isEmpty()) {
                selectedTime = null;
            } else {
                View v = group.findViewById(ids.iterator().next());
                selectedTime = (String) v.getTag();
            }
        });

        if (chipGroupTimes.getChildCount() > 0)
            selectedTime = (String) chipGroupTimes.getChildAt(0).getTag();
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
                    this.moviePosterUrl = doc.getString("imgBanner");

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
