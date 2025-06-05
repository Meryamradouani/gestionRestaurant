package org.example.restaurant.manager.dao;

import org.example.restaurant.manager.models.MenuItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {
    private final Connection connection;

    public MenuItemDAO(Connection connection) {
        this.connection = connection;
    }

    public List<MenuItem> getAllMenuItems() throws SQLException {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("category_id")
                ));
            }
        }
        return items;
    }

    public void addMenuItem(MenuItem item) throws SQLException {
        String sql = "INSERT INTO menu_items (name, description, price, category_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setInt(4, item.getCategoryId());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateMenuItem(MenuItem item) throws SQLException {
        String sql = "UPDATE menu_items SET name = ?, description = ?, price = ?, category_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setInt(4, item.getCategoryId());
            pstmt.setInt(5, item.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteMenuItem(int id) throws SQLException {
        String sql = "DELETE FROM menu_items WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}