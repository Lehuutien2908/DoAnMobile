package com.example.doanmobile.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class TicketModel implements Serializable {
    private String ticketId;
    private String userId;
    private String movieName;
    private String showTime;
    private String cinema;
    private String seats;
    private String room;
    private List<ComboModel> combos;
    private int totalPrice;
    private Timestamp createdAt;

    public TicketModel() {
        // Firestore cần constructor rỗng
    }

    public TicketModel(String userId, String movieName, String showTime, String cinema,
                       String seats, String room, List<ComboModel> combos, int totalPrice, Timestamp createdAt) {
        this.userId = userId;
        this.movieName = movieName;
        this.showTime = showTime;
        this.cinema = cinema;
        this.seats = seats;
        this.room = room;
        this.combos = combos;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getCinema() {
        return cinema;
    }

    public void setCinema(String cinema) {
        this.cinema = cinema;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public List<ComboModel> getCombos() {
        return combos;
    }

    public void setCombos(List<ComboModel> combos) {
        this.combos = combos;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
