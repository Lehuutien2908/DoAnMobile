// ComboModel.java
package com.example.doanmobile.model;

import java.io.Serializable;

public class ComboModel implements Serializable {
    private String name;
    private int quantity;
    private long price;

    public ComboModel() {
    }

    public ComboModel(String name, int quantity, long price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getPrice() {
        return price;
    }

    public long getTotalPrice() {
        return price * quantity;
    }
}
