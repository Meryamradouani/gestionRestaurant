package org.example.restaurant.staff.dao;

import org.example.restaurant.staff.model.Order;
import org.example.restaurant.staff.model.OrderItem;
import org.example.restaurant.staff.config.DatabaseConfig;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private Connection connection;

    public OrderDAO() {
        try {
            this.connection = DatabaseConfig.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> getOrdersForToday() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u.name as user_name FROM orders o " +
                "JOIN reservations r ON o.reservation_id = r.id " +
                "JOIN users u ON r.user_id = u.id " +
                "WHERE DATE(o.created_at) = CURDATE() " +
                "ORDER BY o.created_at DESC";

        System.out.println("Executing query: " + query); // Debug log

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("Query executed successfully"); // Debug log

            while (rs.next()) {
                System.out.println("Found order: " + rs.getInt("id")); // Debug log
                Order order = new Order(
                        rs.getInt("id"),
                        rs.getInt("staff_id"),
                        rs.getInt("reservation_id"),
                        rs.getString("user_name"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getBigDecimal("total"),
                        rs.getString("status")
                );
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }

            System.out.println("Total orders found: " + orders.size()); // Debug log
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage()); // Debug log
            e.printStackTrace();
            throw e;
        }
        return orders;
    }

    public List<OrderItem> getOrderItems(int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String query = "SELECT oi.*, mi.name as menu_item_name FROM order_items oi " +
                "JOIN menu_items mi ON oi.menu_item_id = mi.id " +
                "WHERE oi.order_id = ?";

        System.out.println("Executing query: " + query); // Debug log
        System.out.println("Parameters: orderId=" + orderId); // Debug log

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Found item: " + rs.getString("menu_item_name")); // Debug log
                    OrderItem item = new OrderItem(
                            rs.getInt("id"),
                            rs.getInt("order_id"),
                            rs.getInt("menu_item_id"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("price")
                    );
                    item.setMenuItemName(rs.getString("menu_item_name"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting order items: " + e.getMessage()); // Debug log
            e.printStackTrace();
            throw e;
        }

        System.out.println("Total items found: " + items.size()); // Debug log
        return items;
    }

    public int createOrder(int staffId, int reservationId) throws SQLException {
        String query = "INSERT INTO orders (staff_id, reservation_id, created_at, total, status) VALUES (?, ?, NOW(), 0, 'EN_ATTENTE')";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, staffId);
            pstmt.setInt(2, reservationId);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Échec de la création de la commande, aucun ID généré.");
                }
            }
        }
    }

    public void addOrderItem(OrderItem item) throws SQLException {
        String query = "INSERT INTO order_items (order_id, menu_item_id, quantity, price) VALUES (?, ?, ?, ?)";

        System.out.println("Executing query: " + query); // Debug log
        System.out.println("Parameters: orderId=" + item.getOrderId() +
                ", menuItemId=" + item.getMenuItemId() +
                ", quantity=" + item.getQuantity() +
                ", price=" + item.getPrice()); // Debug log

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, item.getOrderId());
            pstmt.setInt(2, item.getMenuItemId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setBigDecimal(4, item.getPrice());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected); // Debug log

            // Mise à jour du total de la commande
            updateOrderTotal(item.getOrderId());
        } catch (SQLException e) {
            System.err.println("Error adding order item: " + e.getMessage()); // Debug log
            e.printStackTrace();
            throw e;
        }
    }

    public void removeOrderItem(int itemId) throws SQLException {
        String query = "DELETE FROM order_items WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, itemId);
            pstmt.executeUpdate();

            // Mise à jour du total de la commande
            updateOrderTotal(getOrderIdByItemId(itemId));
        }
    }

    private int getOrderIdByItemId(int itemId) throws SQLException {
        String query = "SELECT order_id FROM order_items WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("order_id");
                }
                throw new SQLException("Article de commande non trouvé");
            }
        }
    }

    private void updateOrderTotal(int orderId) throws SQLException {
        String query = "UPDATE orders SET total = (SELECT SUM(quantity * price) FROM order_items WHERE order_id = ?) WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
        }
    }

    public void updateOrderStatus(int orderId, String status) throws SQLException {
        String query = "UPDATE orders SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
        }
    }

    public void updateOrderItemQuantity(int itemId, int quantity) throws SQLException {
        String query = "UPDATE order_items SET quantity = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();

            // Mise à jour du total de la commande
            updateOrderTotal(getOrderIdByItemId(itemId));
        }
    }

    public Order getOrderById(int orderId) throws SQLException {
        String sql = "SELECT o.*, u.name as user_name FROM orders o " +
                "JOIN reservations r ON o.reservation_id = r.id " +
                "JOIN users u ON r.user_id = u.id " +
                "WHERE o.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Order(
                        rs.getInt("id"),
                        rs.getInt("staff_id"),
                        rs.getInt("reservation_id"),
                        rs.getString("user_name"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getBigDecimal("total"),
                        rs.getString("status")
                );
            }
        }
        return null;
    }
}