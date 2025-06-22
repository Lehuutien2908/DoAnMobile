package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.example.doanmobile.model.ComboModel;
import com.example.doanmobile.model.TicketModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class PaymentActivity extends AppCompatActivity {

    private ImageView imgMoviePoster;
    private TextView txtMovieName, txtShowTime, txtCinema, txtSeats, txtRoom;
    private TextView txtTotalSnackPrice, txtTotalPrice;
    private RadioGroup radioPaymentMethod;
    private Button btnConfirmPayment;

    // Firebase
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    // Dữ liệu từ Intent
    private String movieName, showTime, cinema, seats, room, posterUrl;
    private ArrayList<ComboModel> comboList = new ArrayList<>();
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
        txtRoom = findViewById(R.id.txtRoom);
        txtTotalSnackPrice = findViewById(R.id.txtTotalComboPrice);
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
        room = intent.getStringExtra("room");
        posterUrl = intent.getStringExtra("posterUrl");
        totalSnackPrice = intent.getIntExtra("totalSnackPrice", 0);
        totalPrice = intent.getIntExtra("totalPrice", 0);
        comboList = (ArrayList<ComboModel>) intent.getSerializableExtra("comboList");

        // Hiển thị dữ liệu
        txtMovieName.setText("Phim: " + movieName);
        txtShowTime.setText("Suất: " + showTime);
        txtCinema.setText("Rạp: " + cinema);
        txtSeats.setText("Ghế: " + seats);
        txtRoom.setText("Phòng: " + room);
        txtTotalSnackPrice.setText(totalSnackPrice + "đ");
        txtTotalPrice.setText(totalPrice + "đ");

        LinearLayout layoutComboList = findViewById(R.id.layoutComboList);
        layoutComboList.removeAllViews(); // clear trước

        for (ComboModel combo : comboList) {
            if (combo.getQuantity() > 0) {
                TextView tv = new TextView(this);
                tv.setText("- " + combo.getName() + " x" + combo.getQuantity() + " (" + combo.getTotalPrice() + "đ)");
                tv.setTextSize(14);
                layoutComboList.addView(tv);
            }
        }

        Glide.with(this).load(posterUrl).into(imgMoviePoster);

        // Xử lý nút thanh toán
        btnConfirmPayment.setOnClickListener(v -> {
            if (auth.getCurrentUser() == null) {
                // Chưa đăng nhập thì chuyển sang LoginActivity
                Toast.makeText(this, "Vui lòng đăng nhập trước khi thanh toán", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }

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

            saveTicket(paymentMethod);
        });
    }

    private void saveTicket(String paymentMethod) {
        String ticketId = UUID.randomUUID().toString();

        TicketModel ticket = new TicketModel(
                auth.getCurrentUser().getUid(),
                movieName,
                showTime,
                cinema,
                seats,
                room,
                comboList,
                totalPrice,
                new Date()
        );

        firestore.collection("tickets")
                .document(ticketId)
                .set(ticket)
                .addOnSuccessListener(unused -> {
                    // Thành công thì chuyển sang màn hình xác nhận
                    Intent confirmIntent = new Intent(PaymentActivity.this, BookingConfirmActivity.class);
                    confirmIntent.putExtra("ticketId", ticketId); // nếu muốn xem vé sau
                    startActivity(confirmIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
