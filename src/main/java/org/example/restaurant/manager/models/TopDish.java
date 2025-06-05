package org.example.restaurant.manager.models;

import javafx.beans.property.*;

public class TopDish {
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty orders = new SimpleIntegerProperty();

    public TopDish(String name, int orders) {
        this.name.set(name);
        this.orders.set(orders);
    }

    public StringProperty nameProperty() { return name; }
    public IntegerProperty ordersProperty() { return orders; }
}