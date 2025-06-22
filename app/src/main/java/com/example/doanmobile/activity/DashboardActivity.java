package com.example.doanmobile.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmobile.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtSoVeTong, txtPhimDangChieu, txtSoNguoiDung, txtTongDoanhThu, txtRecentActivity;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Liên kết các TextView
        txtSoVeTong = findViewById(R.id.sovetong);
        txtPhimDangChieu = findViewById(R.id.phimdangchieu);
        txtSoNguoiDung = findViewById(R.id.songuoidung);
        txtTongDoanhThu = findViewById(R.id.tongdoanhthu);
        txtRecentActivity = findViewById(R.id.txtRecentActivity);

        db = FirebaseFirestore.getInstance();

        loadTotalTickets();
        loadTotalMovies();
        loadTotalUsers();
        loadTotalRevenue();
        loadRecentActivity();
    }

    private void loadTotalTickets() {
        db.collection("tickets").get().addOnSuccessListener(query -> {
            int count = query.size();
            txtSoVeTong.setText(String.valueOf(count));
        }).addOnFailureListener(e -> txtSoVeTong.setText("Lỗi"));
    }

    private void loadTotalMovies() {
        db.collection("movies").get().addOnSuccessListener(query -> {
            int count = query.size();
            txtPhimDangChieu.setText(String.valueOf(count));
        }).addOnFailureListener(e -> txtPhimDangChieu.setText("Lỗi"));
    }

    private void loadTotalUsers() {
        db.collection("accounts").get().addOnSuccessListener(query -> {
            int count = query.size();
            txtSoNguoiDung.setText(String.valueOf(count));
        }).addOnFailureListener(e -> txtSoNguoiDung.setText("Lỗi"));
    }

    private void loadTotalRevenue() {
        db.collection("tickets").get().addOnSuccessListener(query -> {
            long total = 0;
            for (QueryDocumentSnapshot doc : query) {
                Long price = doc.getLong("totalPrice");
                if (price != null) total += price;
            }
            txtTongDoanhThu.setText(String.format("%,d VNĐ", total));
        }).addOnFailureListener(e -> txtTongDoanhThu.setText("Lỗi"));
    }

    private void loadRecentActivity() {
        db.collection("activity_logs")
                .get()
                .addOnSuccessListener(query -> {
                    StringBuilder sb = new StringBuilder();
                    for (QueryDocumentSnapshot doc : query) {
                        String message = doc.getString("message");
                        if (message != null) {
                            sb.append("- ").append(message).append("\n\n");
                        }
                    }
                    txtRecentActivity.setText(sb.toString().trim());
                })
                .addOnFailureListener(e -> txtRecentActivity.setText("Không thể tải hoạt động"));
    }

}
