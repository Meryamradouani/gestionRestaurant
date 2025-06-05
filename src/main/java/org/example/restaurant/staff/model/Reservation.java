package org.example.restaurant.staff.model;

import java.sql.Timestamp;

public class Reservation {
    private int id;
    private int userId;
    private String userName;
    private Timestamp time;
    private int guests;
    private String status;
    private String contactMessage;

    public Reservation(int id, int userId, String userName, Timestamp time, int guests, String status, String contactMessage) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.time = time;
        this.guests = guests;
        this.status = status;
        this.contactMessage = contactMessage;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Timestamp getTime() {
        return time;
    }

    public int getGuests() {
        return guests;
    }

    public String getStatus() {
        return status;
    }

    public String getContactMessage() {
        return contactMessage;
    }
}