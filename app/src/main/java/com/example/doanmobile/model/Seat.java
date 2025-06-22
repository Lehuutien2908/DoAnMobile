package com.example.doanmobile.model;

public class Seat {
    private String code;      // Mã ghế (ví dụ: A1, B2)
    private String type;      // Loại ghế (ví dụ: normal, vip, double)
    private int price;        // Giá ghế
    private String status;    // Trạng thái (available, sold)
    private String userId;    // User ID nếu ghế đã bán
    private String showtimeId;
    public Seat() {
    }

    public Seat(String code, String type, int price, String status, String userId) {
        this.code = code;
        this.type = type;
        this.price = price;
        this.status = status;
        this.userId = userId;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }

    // Setters (nếu cần cập nhật các trường này từ client)
    public void setCode(String code) {
        this.code = code;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
} 