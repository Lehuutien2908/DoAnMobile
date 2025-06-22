// ComboModel.java
package com.example.doanmobile.model;

import java.io.Serializable;

public class ComboModel implements Serializable {
    private String name;
    private String description;
    private String imgCombo;
    private long price;
    private int quantity; // Số lượng combo đã chọn

    public ComboModel() {
        // Constructor rỗng cần thiết cho Firebase Firestore
    }

    public ComboModel(String name, String description, String imgCombo, long price, int quantity) {
        this.name = name;
        this.description = description;
        this.imgCombo = imgCombo;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImgCombo() {
        return imgCombo;
    }

    public long getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImgCombo(String imgCombo) {
        this.imgCombo = imgCombo;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getTotalPrice() {
        return price * quantity;
    }
}
