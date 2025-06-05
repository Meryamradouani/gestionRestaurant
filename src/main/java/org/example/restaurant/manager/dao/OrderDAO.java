package org.example.restaurant.manager.dao;

import org.example.restaurant.manager.models.Order;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final Connection connection;

    public OrderDAO(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("La connexion ne peut pas être nulle.");
        }
        this.connection = connection;
    }
    public int getOrdersCountByDate(LocalDate date) throws SQLException {
        String query = "SELECT COUNT(*) AS order_count FROM orders WHERE DATE(order_date) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("order_count");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du nombre de commandes : " + e.getMessage());
            throw e;
        }
        return 0;
    }


    // Récupérer toutes les commandes
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT id, customer_id, total_amount, order_date FROM orders";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Récupère les données de la base et crée un objet Order
                Order order = new Order(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getDouble("total_amount"),
                        rs.getTimestamp("order_date").toLocalDateTime() // Assure-toi que `order_date` est de type TIMESTAMP ou DATETIME
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de toutes les commandes : " + e.getMessage());
        }
        return orders;
    }
}
