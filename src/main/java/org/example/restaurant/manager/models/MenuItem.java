package org.example.restaurant.manager.models;

import javafx.beans.property.*;

public class MenuItem {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final DoubleProperty price;
    private final IntegerProperty categoryId;

    public MenuItem(int id, String name, String description, double price, int categoryId) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleDoubleProperty(price);
        this.categoryId = new SimpleIntegerProperty(categoryId);
    }

    // Getters pour les valeurs
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getDescription() { return description.get(); }
    public double getPrice() { return price.get(); }
    public int getCategoryId() { return categoryId.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setDescription(String description) { this.description.set(description); }
    public void setPrice(double price) { this.price.set(price); }
    public void setCategoryId(int categoryId) { this.categoryId.set(categoryId); }

    // Property getters (pour le binding JavaFX)
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty priceProperty() { return price; }
    public IntegerProperty categoryIdProperty() { return categoryId; }
}