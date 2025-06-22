package com.example.doanmobile.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmobile.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ScheduleManagementActivity hiển thị danh sách lịch chiếu cho admin
 * – Lấy dữ liệu từ collection "showtimes" và hiển thị TÊN phim + TÊN rạp
 * – Tìm kiếm theo tên phim / tên rạp / ngày chiếu
 * – Xoá lịch bằng long‑click
 */
public class ScheduleManagementActivity extends AppCompatActivity {

    // ======= UI =======
    private ListView listView;
    private EditText edtSearch;
    private Button   btnAdd;

    // ======= DATA =======
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final List<Showtime> allSchedules      = new ArrayList<>();
    private       List<Showtime> filteredSchedules = new ArrayList<>();

    private final List<String> displayList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private final Map<String,String> movieNameMap  = new HashMap<>();
    private final Map<String,String> cinemaNameMap = new HashMap<>();

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules_admin);

        listView   = findViewById(R.id.listSchedules);
        edtSearch  = findViewById(R.id.editSearch);
        btnAdd     = findViewById(R.id.btnAdd);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddScheduleActivity.class)));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Showtime s = filteredSchedules.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Xoá lịch chiếu")
                    .setMessage("Bạn có chắc muốn xoá lịch chiếu này?")
                    .setPositiveButton("Xoá", (d,i)-> deleteShowtime(s.id))
                    .setNegativeButton("Huỷ", null)
                    .show();
            return true;
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){}
            public void afterTextChanged(Editable s){ filterAndDisplay(s.toString().trim()); }
        });

        // Tải map phim & rạp rồi load schedule
        preloadNames();
    }

    /* --------------------------------------------------- */
    private void preloadNames() {
        Task<Void> moviesTask = db.collection("movies").get().addOnSuccessListener(q -> {
            for (DocumentSnapshot d : q) movieNameMap.put(d.getId(), d.getString("name"));
        }).continueWithTask(t-> Tasks.forResult(null));

        Task<Void> cinemasTask = db.collection("cinemas").get().addOnSuccessListener(q -> {
            for (DocumentSnapshot d : q) cinemaNameMap.put(d.getId(), d.getString("name"));
        }).continueWithTask(t-> Tasks.forResult(null));

        Tasks.whenAllComplete(moviesTask, cinemasTask).addOnSuccessListener(l -> loadSchedules());
    }

    /* ---------------- LOAD SHOWTIMES ------------------- */
    private void loadSchedules() {
        db.collection("showtimes").get().addOnSuccessListener(result -> {
            allSchedules.clear();
            for (QueryDocumentSnapshot doc : result) {
                Showtime s = new Showtime();
                s.id        = doc.getId();
                s.movieId   = doc.getString("movieId");
                s.cinemaId  = doc.getString("cinemasId");
                s.date      = doc.getTimestamp("date");
                s.times     = (List<String>) doc.get("times");
                s.movieName  = movieNameMap.getOrDefault(s.movieId, s.movieId);
                s.cinemaName = cinemaNameMap.getOrDefault(s.cinemaId, s.cinemaId);
                allSchedules.add(s);
            }
            filterAndDisplay(edtSearch.getText().toString().trim());
        });
    }

    /* ---------------- FILTER + DISPLAY ----------------- */
    private void filterAndDisplay(String keyword) {
        String q = keyword.toLowerCase();
        filteredSchedules = allSchedules.stream().filter(s ->
                        s.movieName.toLowerCase().contains(q) ||
                                s.cinemaName.toLowerCase().contains(q) ||
                                sdf.format(s.date.toDate()).contains(q))
                .collect(Collectors.toList());

        displayList.clear();
        for (Showtime s : filteredSchedules) {
            displayList.add("Phim: " + s.movieName + "\nRạp: " + s.cinemaName +
                    "\nNgày: " + sdf.format(s.date.toDate()) +
                    "\nSuất: " + String.join(", ", s.times));
        }
        adapter.notifyDataSetChanged();
    }

    /* ---------------- DELETE --------------------------- */
    private void deleteShowtime(String id) {
        db.collection("showtimes").document(id).delete()
                .addOnSuccessListener(u -> {
                    Toast.makeText(this, "Đã xoá", Toast.LENGTH_SHORT).show();
                    loadSchedules();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /* ---------------- MODEL ---------------------------- */
    private static class Showtime {
        String id;
        String movieId;
        String cinemaId;
        String movieName;
        String cinemaName;
        Timestamp date;
        List<String> times;
    }
}
