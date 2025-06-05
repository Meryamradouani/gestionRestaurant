package org.example.restaurant.manager.models;


public class TopPlat {
    private String name;
    private int quantity;

    public TopPlat(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}