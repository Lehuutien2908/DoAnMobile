package com.example.doanmobile.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmobile.R;
import com.example.doanmobile.model.ComboModel;
import com.example.doanmobile.model.TicketModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class TicketDetailActivity extends AppCompatActivity {

    private TextView txtMovieTitle, txtCinema, txtShowtime, txtRoom, txtSeats, txtCombo, txtTotalPrice;
    private ImageView imgQrCode;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        // Ánh xạ view
        txtMovieTitle = findViewById(R.id.txtMovieTitle);
        txtCinema = findViewById(R.id.txtCinema);
        txtShowtime = findViewById(R.id.txtShowtime);
        txtRoom = findViewById(R.id.txtRoom);
        txtSeats = findViewById(R.id.txtSeats);
        txtCombo = findViewById(R.id.txtCombo);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        imgQrCode = findViewById(R.id.imgQrCode);
        btnBack = findViewById(R.id.btnBack);

        // Lấy vé từ Intent
        TicketModel ticket = (TicketModel) getIntent().getSerializableExtra("ticket");
        if (ticket == null) {
            Toast.makeText(this,"Không nhận được vé!",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (ticket != null) {
            txtMovieTitle.setText(ticket.getMovieName());
            txtCinema.setText("Rạp: " + ticket.getCinema());
            txtShowtime.setText("Suất chiếu: " + ticket.getShowTime());
            txtRoom.setText("Phòng: " + (ticket.getRoom() != null ? ticket.getRoom() : "Không rõ"));
            txtSeats.setText("Ghế: " + ticket.getSeats());

            StringBuilder comboInfo = new StringBuilder();
            if (ticket.getCombos() != null && !ticket.getCombos().isEmpty()) {
                for (ComboModel combo : ticket.getCombos()) {
                    if (combo.getQuantity() > 0) {
                        comboInfo.append("- ")
                                .append(combo.getName())
                                .append(" x")
                                .append(combo.getQuantity())
                                .append(" (")
                                .append(combo.getTotalPrice())
                                .append("đ)\n");
                    }
                }
            } else {
                comboInfo.append("Không có combo");
            }
            txtCombo.setText("Combo:\n" + comboInfo.toString().trim());

            txtTotalPrice.setText("Tổng tiền: " + ticket.getTotalPrice() + "đ");

            // Tạo mã QR từ ID vé
            if (ticket.getTicketId() != null) {
                try {
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(ticket.getTicketId(),
                            BarcodeFormat.QR_CODE, 400, 400);
                    imgQrCode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        }

        btnBack.setOnClickListener(v -> finish());
    }
}
