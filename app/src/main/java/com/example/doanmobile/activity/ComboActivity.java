package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.R;
import com.example.doanmobile.adapter.ComboAdapter;
import com.example.doanmobile.model.ComboModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ComboActivity extends AppCompatActivity implements ComboAdapter.OnComboQuantityChangeListener {

    private ImageView btnBack;
    private TextView tvSeatInfoBottom, tvTotalPriceBottom;
    private Button btnContinueBottom;
    private RecyclerView recyclerViewCombos;

    private String movieName, showTime, cinema, seats, room, posterUrl;
    private int initialTotalPrice;

    private FirebaseFirestore db;
    private List<ComboModel> comboList; // Danh sách combo sẽ được tải từ Firebase
    private ComboAdapter comboAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo);

        // Ánh xạ views
        btnBack = findViewById(R.id.btnBack);
        tvSeatInfoBottom = findViewById(R.id.tvSeatInfoBottom);
        tvTotalPriceBottom = findViewById(R.id.tvTotalPriceBottom);
        btnContinueBottom = findViewById(R.id.btnContinueBottom);
        recyclerViewCombos = findViewById(R.id.recyclerViewCombos);

        // Khởi tạo Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        movieName = intent.getStringExtra("movieName");
        showTime = intent.getStringExtra("showTime");
        cinema = intent.getStringExtra("cinema");
        seats = intent.getStringExtra("seats");
        room = intent.getStringExtra("room");
        posterUrl = intent.getStringExtra("posterUrl");
        initialTotalPrice = intent.getIntExtra("totalPrice", 0);

        // Hiển thị thông tin ghế và tổng tiền ban đầu
        tvSeatInfoBottom.setText("1x ghế: " + seats);

        // Setup RecyclerView
        comboList = new ArrayList<>();
        comboAdapter = new ComboAdapter(this, comboList);
        comboAdapter.setOnComboQuantityChangeListener(this);
        recyclerViewCombos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCombos.setAdapter(comboAdapter);

        updateTotalPrice();

        // Tải dữ liệu combo từ Firebase
        loadCombosFromFirebase();

        // Xử lý sự kiện nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện nút Tiếp Tục
        btnContinueBottom.setOnClickListener(v -> {
            ArrayList<ComboModel> selectedCombos = new ArrayList<>();
            int totalSnackPrice = 0;

            for (ComboModel combo : comboList) {
                if (combo.getQuantity() > 0) {
                    selectedCombos.add(combo);
                    totalSnackPrice += combo.getTotalPrice();
                }
            }

            Intent paymentIntent = new Intent(ComboActivity.this, PaymentActivity.class);

            // Truyền lại các thông tin đã nhận từ SeatBookingActivity
            paymentIntent.putExtra("movieName", movieName);
            paymentIntent.putExtra("showTime", showTime);
            paymentIntent.putExtra("cinema", cinema);
            paymentIntent.putExtra("posterUrl", posterUrl);
            paymentIntent.putExtra("seats", seats);
            paymentIntent.putExtra("room", room);

            // Truyền thông tin combo và tổng tiền cuối cùng
            paymentIntent.putExtra("comboList", selectedCombos);
            paymentIntent.putExtra("totalSnackPrice", totalSnackPrice);
            paymentIntent.putExtra("totalPrice", initialTotalPrice + totalSnackPrice);

            startActivity(paymentIntent);
        });
    }

    private void loadCombosFromFirebase() {
        db.collection("combos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    comboList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ComboModel combo = document.toObject(ComboModel.class);
                        combo.setQuantity(0); // Khởi tạo số lượng là 0
                        comboList.add(combo);
                    }
                    comboAdapter.notifyDataSetChanged();
                    Log.d("ComboActivity", "Đã tải " + comboList.size() + " combo từ Firebase.");
                })
                .addOnFailureListener(e -> {
                    Log.e("ComboActivity", "Lỗi khi tải combo từ Firebase: " + e.getMessage());
                    Toast.makeText(ComboActivity.this, "Lỗi khi tải combo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onQuantityChanged() {
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        long currentTotalSnackPrice = 0;
        for (ComboModel combo : comboList) {
            currentTotalSnackPrice += combo.getTotalPrice();
        }
        long finalTotalPrice = initialTotalPrice + currentTotalSnackPrice;
        tvTotalPriceBottom.setText("Tổng cộng: " + formatCurrency((int) finalTotalPrice));
    }

    private String formatCurrency(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(amount);
    }
}
