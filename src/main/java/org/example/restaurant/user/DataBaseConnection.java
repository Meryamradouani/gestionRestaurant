package org.example.restaurant.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    public static Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/restaurant";
        String user = "root";
        String password = ""; // change selon ton mot de passe MySQL

        return DriverManager.getConnection(url, user, password);
    }
}
