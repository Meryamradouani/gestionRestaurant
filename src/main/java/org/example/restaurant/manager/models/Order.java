package org.example.restaurant.manager.models;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Order {

    private final IntegerProperty orderId = new SimpleIntegerProperty();
    private final StringProperty customerName = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    // Constructeur
    public Order(int orderId, int customerId, double totalAmount, LocalDateTime orderDate) {
        this.orderId.set(orderId);
        this.customerName.set("Customer " + customerId); // Exemple: Nom de client basé sur l'ID (tu peux l'ajuster selon ton besoin)
        this.status.set("Pending"); // Exemple de statut, tu peux le changer selon ta logique
    }

    // Getters et setters pour JavaFX Binding
    public int getOrderId() {
        return orderId.get();
    }

    public IntegerProperty orderIdProperty() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }
}
