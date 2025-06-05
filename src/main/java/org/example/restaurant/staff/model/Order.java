package org.example.restaurant.staff.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int staffId;
    private int reservationId;
    private String userName;
    private LocalDateTime createdAt;
    private BigDecimal total;
    private String status;
    private List<OrderItem> items;

    public Order(int id, int staffId, int reservationId, String userName, LocalDateTime createdAt, BigDecimal total, String status) {
        this.id = id;
        this.staffId = staffId;
        this.reservationId = reservationId;
        this.userName = userName;
        this.createdAt = createdAt;
        this.total = total;
        this.status = status;
        this.items = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getStaffId() {
        return staffId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getUserName() {
        return userName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}