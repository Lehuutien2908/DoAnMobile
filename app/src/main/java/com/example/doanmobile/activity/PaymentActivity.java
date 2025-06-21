package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class PaymentActivity extends AppCompatActivity {

    private ImageView imgMoviePoster;
    private TextView txtMovieName, txtShowTime, txtCinema, txtSeats;
    private TextView txtTotalSnackPrice, txtTotalPrice;
    private RadioGroup radioPaymentMethod;
    private Button btnConfirmPayment;

    // Firebase
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    // Dữ liệu từ Intent
    private String movieName, showTime, cinema, seats, posterUrl;
    private int totalSnackPrice, totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Ánh xạ view
        imgMoviePoster = findViewById(R.id.imgMoviePoster);
        txtMovieName = findViewById(R.id.txtMovieName);
        txtShowTime = findViewById(R.id.txtShowTime);
        txtCinema = findViewById(R.id.txtCinema);
        txtSeats = findViewById(R.id.txtSeats);
        txtTotalSnackPrice = findViewById(R.id.txtTotalSnackPrice);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        radioPaymentMethod = findViewById(R.id.radioPaymentMethod);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        // Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        movieName = intent.getStringExtra("movieName");
        showTime = intent.getStringExtra("showTime");
        cinema = intent.getStringExtra("cinema");
        seats = intent.getStringExtra("seats");
        posterUrl = intent.getStringExtra("posterUrl");
        totalSnackPrice = intent.getIntExtra("totalSnackPrice", 0);
        totalPrice = intent.getIntExtra("totalPrice", 0);

        // Hiển thị dữ liệu
        txtMovieName.setText("Phim: " + movieName);
        txtShowTime.setText("Suất: " + showTime);
        txtCinema.setText("Rạp: " + cinema);
        txtSeats.setText("Ghế: " + seats);
        txtTotalSnackPrice.setText(totalSnackPrice + "đ");
        txtTotalPrice.setText(totalPrice + "đ");

        Glide.with(this).load(posterUrl).into(imgMoviePoster);

        // Xử lý nút thanh toán
        btnConfirmPayment.setOnClickListener(v -> {
            int selectedId = radioPaymentMethod.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            String paymentMethod = "";
            if (selectedId == R.id.radioZaloPay) {
                paymentMethod = "ZaloPay";
            } else if (selectedId == R.id.radioCash) {
                paymentMethod = "Ví Momo";
            }

            String userId = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : "guest";

            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("userId", userId);
            paymentData.put("movieName", movieName);
            paymentData.put("showTime", showTime);
            paymentData.put("cinema", cinema);
            paymentData.put("seats", seats);
            paymentData.put("paymentMethod", paymentMethod);
            paymentData.put("totalSnackPrice", totalSnackPrice);
            paymentData.put("totalPrice", totalPrice);
            paymentData.put("timestamp", System.currentTimeMillis());

            // Lưu dữ liệu vào Firestore
            firestore.collection("payments")
                    .add(paymentData)
                    .addOnSuccessListener(documentReference -> {
                        // Thành công → chuyển sang BookingConfirmActivity
                        Intent confirmIntent = new Intent(PaymentActivity.this, BookingConfirmActivity.class);
                        startActivity(confirmIntent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
