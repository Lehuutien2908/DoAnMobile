package com.example.doanmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doanmobile.R;
import com.example.doanmobile.model.Seat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SeatBookingActivity extends AppCompatActivity {

    // Các biến để lưu trữ dữ liệu nhận được từ Intent (MovieDetailActivity)
    private String movieId;
    private String cinemaId;
    private String selectedTime; // Định dạng HH:mm (ví dụ: "15:00")
    private String movieTitle; // Thêm biến để lưu tên phim
    private String cinemaName; // Thêm biến để lưu tên rạp
    private String posterUrl ; // Thêm biến để lưu URL poster phim

    // Các View trong layout activity_seat_booking.xml
    private GridLayout gridSeats;
    private TextView tvSeatInfo, tvTotalPrice;
    private Button btnContinue;
    private TextView tvCinemaName, tvMovieTitle, tvMovieType, tvSelectedTimeDisplay; // Các TextView mới

    // Firebase Firestore instance
    private FirebaseFirestore db;

    // Danh sách ghế
    private List<Seat> allSeats = new ArrayList<>(); // Danh sách tất cả ghế từ Firebase cho suất chiếu hiện tại
    private List<Seat> selectedSeats = new ArrayList<>(); // Danh sách ghế người dùng đã chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_booking);

        // Ánh xạ các View từ layout
        gridSeats = findViewById(R.id.gridSeats);
        tvSeatInfo = findViewById(R.id.tvSeatInfo);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnContinue = findViewById(R.id.btnContinue);
        tvCinemaName = findViewById(R.id.tvCinemaName);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvMovieType = findViewById(R.id.tvMovieType);
        tvSelectedTimeDisplay = findViewById(R.id.tvSelectedTimeDisplay);

        // Khởi tạo Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Nhận dữ liệu từ Intent được truyền từ MovieDetailActivity
        Intent intent = getIntent();
        movieId = intent.getStringExtra("movieId");
        cinemaId = intent.getStringExtra("cinemaId");
        selectedTime = intent.getStringExtra("time");
        posterUrl  =intent.getStringExtra("posterUrl");




        createSampleSeatsForShowtime("time3");




        // Log dữ liệu nhận được để kiểm tra
        Log.d("SeatBookingActivity", "Received MovieId: " + movieId + ", CinemaId: " + cinemaId + ", Time: " + selectedTime);

        // Hiển thị thông tin suất chiếu
        loadCinemaDetails(cinemaId);
        loadMovieDetails(movieId);
        tvSelectedTimeDisplay.setText(selectedTime);

        // Bước 1: Tìm showtimeId (ID document của suất chiếu) và sau đó tải danh sách ghế
        findShowtimeIdAndLoadSeats();

        // Xử lý sự kiện click cho nút "Tiếp tục"
        btnContinue.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ghế!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent toComboIntent = new Intent(this, ComboActivity.class);

            // Truyền thông tin phim, rạp, suất chiếu
            toComboIntent.putExtra("movieName", movieTitle);
            toComboIntent.putExtra("showTime", selectedTime);
            toComboIntent.putExtra("cinema", cinemaName);
            Log.d("MovieDetailActivity", "URL being passed to SeatBookingActivity: " + posterUrl);
            toComboIntent.putExtra("posterUrl", posterUrl); // Truyền URL poster phim

            // Truyền danh sách ghế đã chọn
            List<String> seatCodes = new ArrayList<>();
            for (Seat seat : selectedSeats) {
                seatCodes.add(seat.getCode());
            }
            toComboIntent.putExtra("seats", TextUtils.join(", ", seatCodes));
            toComboIntent.putExtra("room", "1");

            // Truyền tổng tiền ghế (tổng tiền đồ ăn vặt ban đầu là 0)
            int seatsTotalPrice = calculateTotalPrice();
            toComboIntent.putExtra("totalPrice", seatsTotalPrice); // Tổng tiền chỉ bao gồm tiền ghế ban đầu

            startActivity(toComboIntent);
        });

    }

    private void loadCinemaDetails(String cinemaId) {
        if (cinemaId == null || cinemaId.isEmpty()) {
            tvCinemaName.setText("Không có thông tin rạp");
            Log.e("SeatBookingActivity", "Cinema ID is null or empty, cannot load cinema details.");
            return;
        }

        db.collection("cinemas").document(cinemaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String cinemaName = documentSnapshot.getString("name");
                        if (cinemaName != null) {
                            tvCinemaName.setText(cinemaName);
                            this.cinemaName = cinemaName; // Lưu tên rạp vào biến
                        } else {
                            tvCinemaName.setText("Tên rạp không có");
                        }
                    } else {
                        tvCinemaName.setText("Rạp không tìm thấy");
                    }
                })
                .addOnFailureListener(e -> {
                    tvCinemaName.setText("Lỗi tải tên rạp");
                    Log.e("SeatBookingActivity", "Error loading cinema details: " + e.getMessage());
                });
    }


    private void loadMovieDetails(String movieId) {
        if (movieId == null || movieId.isEmpty()) {
            tvMovieTitle.setText("Không có thông tin phim");
            tvMovieType.setText("");
            Log.e("SeatBookingActivity", "Movie ID is null or empty, cannot load movie details.");
            return;
        }

        db.collection("movies").document(movieId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String movieTitle = documentSnapshot.getString("name");
                        String movieGenre = documentSnapshot.getString("genre");
                        String movieDuration = documentSnapshot.getString("duration"); // Giả sử có duration
                        String posterUrl = documentSnapshot.getString("imgBanner"); // Lấy URL poster

                        if (movieTitle != null) {
                            tvMovieTitle.setText(movieTitle);
                            this.movieTitle = movieTitle; // Lưu tên phim vào biến
                        }
                        // Cần điều chỉnh logic hiển thị loại phim (2D LỒNG TIẾNG) tùy theo dữ liệu Firebase
                        // Hiện tại đang lấy genre và duration. Có thể cần trường 'type' riêng (2D LỒNG TIẾNG) trong Movie model
                        if (movieGenre != null && movieDuration != null) {
                            tvMovieType.setText(movieGenre + " | " + movieDuration + " phút");
                        } else if (movieGenre != null) {
                            tvMovieType.setText(movieGenre);
                        } else {
                            tvMovieType.setText("Thông tin loại phim không có");
                        }

                        if (posterUrl != null) {
                            this.posterUrl = posterUrl; // Lưu URL poster phim vào biến
                        } else {
                            this.posterUrl = ""; // Hoặc một URL mặc định nếu không có
                        }

                    } else {
                        tvMovieTitle.setText("Phim không tìm thấy");
                        tvMovieType.setText("");
                    }
                })
                .addOnFailureListener(e -> {
                    tvMovieTitle.setText("Lỗi tải chi tiết phim");
                    tvMovieType.setText("");
                    Log.e("SeatBookingActivity", "Error loading movie details: " + e.getMessage());
                });
    }


    private void findShowtimeIdAndLoadSeats() {
        if (movieId == null || cinemaId == null || selectedTime == null) { // Bỏ kiểm tra selectedDate
            Log.e("SeatBookingActivity", "Thiếu tham số suất chiếu từ Intent.");
            Toast.makeText(this, "Thiếu thông tin suất chiếu để tải ghế.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Truy vấn collection "showtimes" để tìm suất chiếu phù hợp
        db.collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .whereEqualTo("cinemasId", cinemaId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String foundShowtimeId = null;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Lấy danh sách giờ chiếu từ Firebase document
                        List<String> firebaseTimes = (List<String>) document.get("times");

                        // Chỉ kiểm tra giờ chiếu
                        if (firebaseTimes != null && firebaseTimes.contains(selectedTime)) {
                            foundShowtimeId = document.getId();
                            break; // Đã tìm thấy suất chiếu phù hợp, thoát vòng lặp
                        }
                    }

                    if (foundShowtimeId != null) {
                        Log.d("SeatBookingActivity", "Tìm thấy showtime ID: " + foundShowtimeId + ". Đang tải ghế...");
                        loadSeatsFromFirebase(foundShowtimeId);
                    } else {
                        Log.e("SeatBookingActivity", "Không tìm thấy suất chiếu phù hợp với các tham số đã cho.");
                        Toast.makeText(SeatBookingActivity.this, "Không tìm thấy suất chiếu phù hợp.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SeatBookingActivity", "Lỗi khi tìm suất chiếu: " + e.getMessage());
                    Toast.makeText(SeatBookingActivity.this, "Lỗi khi tìm suất chiếu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Phương thức tải danh sách ghế từ Firebase cho một suất chiếu cụ thể.
     */
    private void loadSeatsFromFirebase(String showtimeId) {
        db.collection("showtimes").document(showtimeId).collection("seats")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allSeats.clear(); // Xóa danh sách ghế cũ
                    selectedSeats.clear(); // Xóa các ghế đã chọn trong session trước
                    gridSeats.removeAllViews(); // Xóa tất cả các View ghế hiện có trên GridLayout

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Seat seat = document.toObject(Seat.class);
                        allSeats.add(seat);
                    }
                    Log.d("SeatBookingActivity", "Đã tải " + allSeats.size() + " ghế từ Firebase. Đang render...");
                    renderSeats(); // Gọi hàm để vẽ các ghế lên giao diện
                    updateSeatInfoAndTotalPrice(); // Cập nhật thông tin ban đầu (0 ghế, 0đ)
                })
                .addOnFailureListener(e -> {
                    Log.e("SeatBookingActivity", "Lỗi khi tải ghế từ Firebase: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi tải ghế: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Phương thức vẽ các nút ghế lên GridLayout dựa trên danh sách allSeats.
     */
    private void renderSeats() {
        gridSeats.removeAllViews(); // Đảm bảo làm sạch lưới trước khi thêm ghế

        // Sắp xếp ghế theo mã để đảm bảo hiển thị đúng thứ tự (ví dụ: A1, A2, B1...)
        // (Nếu mã ghế có dạng chữ-số, cần logic sắp xếp tùy chỉnh phức tạp hơn nếu muốn A1, A2... B1, B2...)
        // Ở đây giả định so sánh chuỗi đơn giản là đủ (ví dụ: A1, A10, A2, B1...)
        allSeats.sort((s1, s2) -> s1.getCode().compareTo(s2.getCode()));

        // Thiết lập số hàng và cột cho GridLayout
        // Bạn cần chắc chắn số hàng/cột này phù hợp với layout rạp của bạn
        // Giả sử 9 hàng (A-I) và 8 cột (1-8)
        gridSeats.setRowCount(9);
        gridSeats.setColumnCount(8);

        // Tạo và thêm từng nút ghế vào GridLayout
        for (Seat seat : allSeats) {
            Button seatBtn = new Button(this);
            seatBtn.setText(seat.getCode());
            seatBtn.setTextSize(12);
            seatBtn.setPadding(0, 0, 0, 0);

            // Đặt màu nền và trạng thái dựa trên dữ liệu Firebase
            if ("sold".equals(seat.getStatus())) {
                seatBtn.setBackgroundResource(R.drawable.bg_seat_sold);
                seatBtn.setTextColor(getResources().getColor(android.R.color.white));
                seatBtn.setEnabled(false); // Ghế đã bán không thể chọn
            } else if ("available".equals(seat.getStatus())) {
                seatBtn.setBackgroundResource(R.drawable.bg_seat_normal);
                seatBtn.setTextColor(getResources().getColor(android.R.color.black));
                seatBtn.setEnabled(true);
            }
            // Nếu ghế đã được chọn trước đó trong session này (quay lại màn hình)
            if (selectedSeats.contains(seat)) {
                seatBtn.setBackgroundResource(R.drawable.bg_seat_selected);
            }

            // Định nghĩa kích thước và margin cho nút ghế
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = getResources().getDimensionPixelSize(R.dimen.seat_size);
            params.height = params.width;
            params.setMargins(6, 6, 6, 6); // Margin nhỏ giữa các ghế
            seatBtn.setLayoutParams(params);

            // Lưu đối tượng Seat vào tag của Button để dễ truy xuất khi click
            seatBtn.setTag(seat);

            // Xử lý sự kiện click cho nút ghế
            seatBtn.setOnClickListener(v -> {
                Seat clickedSeat = (Seat) v.getTag();
                // Không làm gì nếu ghế đã bán hoặc lỗi dữ liệu (clickedSeat là null)
                if (clickedSeat == null || "sold".equals(clickedSeat.getStatus())) {
                    return;
                }

                if (selectedSeats.contains(clickedSeat)) {
                    // Nếu ghế đang được chọn, bỏ chọn nó
                    selectedSeats.remove(clickedSeat);
                    v.setBackgroundResource(R.drawable.bg_seat_normal);
                } else {
                    // Nếu ghế chưa được chọn, chọn nó
                    selectedSeats.add(clickedSeat);
                    v.setBackgroundResource(R.drawable.bg_seat_selected);
                }
                updateSeatInfoAndTotalPrice(); // Cập nhật thông tin sau khi chọn/bỏ chọn
            });

            gridSeats.addView(seatBtn);
        }
    }

    /**
     * Phương thức cập nhật thông tin ghế đã chọn và tổng tiền trên UI.
     */
    private void updateSeatInfoAndTotalPrice() {
        List<String> codes = new ArrayList<>();
        for (Seat s : selectedSeats) {
            codes.add(s.getCode());
        }
        tvSeatInfo.setText(selectedSeats.size() + "x ghế: " + TextUtils.join(", ", codes));

        tvTotalPrice.setText("Tổng cộng: " + formatCurrency(calculateTotalPrice()));
    }

    /**
     * Phương thức tính tổng tiền từ các ghế đã chọn.
     */
    private int calculateTotalPrice() {
        int total = 0;
        for (Seat seat : selectedSeats) {
            total += seat.getPrice();
        }
        return total;
    }

    /**
     * Phương thức định dạng số tiền thành định dạng tiền tệ Việt Nam Đồng (ví dụ: "760.000₫").
     */
    private String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        // Loại bỏ ký tự định dạng Unicode có thể xuất hiện trên một số thiết bị
        return formatter.format(amount)
                .replace("٫", ",") // Arabic comma to normal comma (if it appears)
                .replace("\u00A0", "") // Non-breaking space
                .replace(" ", "") // Regular space
                .replace("\u202d", "") // Left-to-Right Override
                .replace("\u202c", ""); // Pop Directional Formatting
    }

    /**
     * HÀM NÀY CHỈ DÙNG ĐỂ TẠO DỮ LIỆU GHẾ MẪU LÊN FIREBASE MỘT LẦN DUY NHẤT.
     * SAU KHI CHẠY THÀNH CÔNG VÀ XÁC NHẬN DỮ LIỆU TRÊN FIREBASE CONSOLE,
     * BẠN NÊN XÓA HOẶC COMMENT LẠI LỜI GỌI HÀM NÀY TRONG onCreate() ĐỂ TRÁNH GHI LẠI DỮ LIỆU.
     *
     * @param showtimeId ID của suất chiếu mà bạn muốn tạo ghế mẫu.
     */
    private void createSampleSeatsForShowtime(String showtimeId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String[] rowLabels = {"I", "H", "G", "F", "E", "D", "C", "B", "A"};
        int numRows = rowLabels.length; // Tổng số hàng
        int numCols = 8; // Tổng số cột

        Log.d("SeatBookingActivity", "Bắt đầu tạo dữ liệu ghế mẫu cho suất chiếu: " + showtimeId);

        for (int r = 0; r < numRows; r++) {
            String row = rowLabels[r];
            for (int col = 1; col <= numCols; col++) {
                String seatCode = row + col;
                Map<String, Object> seat = new HashMap<>();
                seat.put("code", seatCode);
                seat.put("type", "normal"); // Mặc định là normal, bạn có thể tùy chỉnh
                seat.put("price", 95000);    // Mặc định giá 95,000đ, bạn có thể tùy chỉnh
                seat.put("status", "available"); // Ban đầu tất cả đều available
                seat.put("userId", ""); // Ban đầu chưa có user nào mua

                // Ghi dữ liệu ghế lên Firebase
                db.collection("showtimes")
                  .document(showtimeId)
                  .collection("seats")
                  .document(seatCode)
                  .set(seat)
                  .addOnSuccessListener(aVoid -> {
                      Log.d("SeatBookingActivity", "Ghế " + seatCode + " tạo thành công.");
                  })
                  .addOnFailureListener(e -> {
                      Log.e("SeatBookingActivity", "Lỗi khi tạo ghế " + seatCode + ": " + e.getMessage());
                  });
            }
        }
        Log.d("SeatBookingActivity", "Hoàn tất yêu cầu tạo ghế mẫu.");
    }
}
