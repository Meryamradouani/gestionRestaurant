package org.example.restaurant.manager.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Reservation {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty userId = new SimpleIntegerProperty();
    private final StringProperty userName = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty time = new SimpleStringProperty();
    private final IntegerProperty guests = new SimpleIntegerProperty();
    private final StringProperty status = new SimpleStringProperty();

    // Getters
    public int getId() { return id.get(); }
    public int getUserId() { return userId.get(); }
    public String getUserName() { return userName.get(); }
    public LocalDate getDate() { return date.get(); }
    public String getTime() { return time.get(); }
    public int getGuests() { return guests.get(); }
    public String getStatus() { return status.get(); }

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty userIdProperty() { return userId; }
    public StringProperty userNameProperty() { return userName; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty timeProperty() { return time; }
    public IntegerProperty guestsProperty() { return guests; }
    public StringProperty statusProperty() { return status; }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setUserId(int userId) { this.userId.set(userId); }
    public void setUserName(String userName) { this.userName.set(userName); }
    public void setDate(LocalDate date) { this.date.set(date); }
    public void setTime(String time) { this.time.set(time); }
    public void setGuests(int guests) { this.guests.set(guests); }
    public void setStatus(String status) { this.status.set(status); }
}