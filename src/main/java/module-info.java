module org.example.restaurant {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens org.example.restaurant.user to javafx.fxml;
    opens org.example.restaurant.user.models to javafx.fxml;
    exports org.example.restaurant.user;
    exports org.example.restaurant.user.models;

    opens org.example.restaurant.manager.controllers.manager to javafx.fxml;
    opens org.example.restaurant.manager.models to javafx.base;
    exports org.example.restaurant.manager.controllers.manager;
    exports org.example.restaurant.manager.models;


    opens org.example.restaurant.staff.controller to javafx.fxml;
    opens org.example.restaurant.staff.model to javafx.base;
    opens org.example.restaurant.staff.config to javafx.fxml;
    opens org.example.restaurant.staff.dao to javafx.fxml;
    exports org.example.restaurant.staff.controller;
    exports org.example.restaurant.staff.model;
    exports org.example.restaurant.staff.config;
    exports org.example.restaurant.staff.dao;
}
