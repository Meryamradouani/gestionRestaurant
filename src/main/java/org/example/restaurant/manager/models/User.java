package org.example.restaurant.manager.models;

import javafx.beans.property.*;

public class User {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty email;
    private final IntegerProperty roleId;
    private final StringProperty roleName;
    private final StringProperty password; // Pour la création seulement

    // Constructeur complet
    public User(int id, String name, String email, int roleId, String roleName, String text) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.roleId = new SimpleIntegerProperty(roleId);
        this.roleName = new SimpleStringProperty(roleName);
        this.password = new SimpleStringProperty("");
    }

    // Getters
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getEmail() { return email.get(); }
    public int getRoleId() { return roleId.get(); }
    public String getRoleName() { return roleName.get(); }
    public String getPassword() { return password.get(); }

    // Property Getters
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public IntegerProperty roleIdProperty() { return roleId; }
    public StringProperty roleNameProperty() { return roleName; }
    public StringProperty passwordProperty() { return password; }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setEmail(String email) { this.email.set(email); }
    public void setRoleId(int roleId) { this.roleId.set(roleId); }
    public void setRoleName(String roleName) { this.roleName.set(roleName); }
    public void setPassword(String password) { this.password.set(password); }
}