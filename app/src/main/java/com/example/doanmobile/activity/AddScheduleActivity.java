package com.example.doanmobile.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmobile.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Thêm lịch chiếu mới vào collection "showtimes".
 * - Chọn phim (Spinner)
 * - Chọn rạp (Spinner)
 * - Chọn ngày (DatePicker)
 * - Nhập danh sách giờ chiếu (dạng "15:00, 18:00, 21:00")
 */
public class AddScheduleActivity extends AppCompatActivity {

    private Spinner spMovie, spCinema;
    private EditText edtDate, edtTimes;
    private Button btnSave, btnCancel;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final Map<String, String> movieIdByName   = new HashMap<>();
    private final Map<String, String> cinemaIdByName  = new HashMap<>();

    private final SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        spMovie  = findViewById(R.id.spMovie);
        spCinema = findViewById(R.id.spCinema);
        edtDate  = findViewById(R.id.edtDate);
        edtTimes = findViewById(R.id.edtTimes);
        btnSave  = findViewById(R.id.btnSave);
        btnCancel= findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> finish());

        loadMovies();
        loadCinemas();

        edtDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveSchedule());
    }

    /* ---------------- LOAD MOVIES ---------------- */
    private void loadMovies() {
        db.collection("movies").get().addOnSuccessListener(q -> {
            List<String> names = new ArrayList<>();
            for (var d : q.getDocuments()) {
                String name = d.getString("name");
                names.add(name);
                movieIdByName.put(name, d.getId());
            }
            ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names);
            spMovie.setAdapter(ad);
        });
    }

    /* ---------------- LOAD CINEMAS ---------------- */
    private void loadCinemas() {
        db.collection("cinemas").get().addOnSuccessListener(q -> {
            List<String> names = new ArrayList<>();
            for (var d : q.getDocuments()) {
                String name = d.getString("name");
                names.add(name);
                cinemaIdByName.put(name, d.getId());
            }
            ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names);
            spCinema.setAdapter(ad);
        });
    }

    /* ---------------- DATE PICKER ---------------- */
    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            String picked = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
            edtDate.setText(picked);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /* ---------------- SAVE ---------------- */
    private void saveSchedule() {
        String movieName  = (String) spMovie.getSelectedItem();
        String cinemaName = (String) spCinema.getSelectedItem();
        String dateStr    = edtDate.getText().toString().trim();
        String timesStr   = edtTimes.getText().toString().trim();

        if (TextUtils.isEmpty(movieName) || TextUtils.isEmpty(cinemaName) ||
                TextUtils.isEmpty(dateStr)  || TextUtils.isEmpty(timesStr)) {
            Toast.makeText(this, "Điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Date parsedDate;
        try {
            parsedDate = sdfInput.parse(dateStr);
        } catch (ParseException e) {
            Toast.makeText(this, "Ngày sai định dạng dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> timeList = Arrays.stream(timesStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        if (timeList.isEmpty()) {
            Toast.makeText(this, "Nhập ít nhất 1 giờ chiếu", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String,Object> data = new HashMap<>();
        data.put("movieId",  movieIdByName.get(movieName));
        data.put("cinemasId",cinemaIdByName.get(cinemaName));
        data.put("date", new Timestamp(parsedDate));
        data.put("times", timeList);

        db.collection("showtimes").add(data)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Đã thêm lịch", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
