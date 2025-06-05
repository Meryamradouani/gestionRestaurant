package org.example.restaurant.staff.model;

import java.math.BigDecimal;

public class OrderItem {
    private int id;
    private int orderId;
    private int menuItemId;
    private String menuItemName;
    private int quantity;
    private BigDecimal price;

    public OrderItem(int id, int orderId, int menuItemId, int quantity, BigDecimal price) {
        this.id = id;
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}