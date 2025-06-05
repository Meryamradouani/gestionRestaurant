package org.example.restaurant.staff.dao;

import org.example.restaurant.staff.model.MenuItem;
import org.example.restaurant.staff.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {
    private Connection connection;

    public MenuItemDAO() {
        try {
            this.connection = DatabaseConfig.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String query = "SELECT name FROM categories ORDER BY name";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        return categories;
    }

    public List<MenuItem> getMenuItemsByCategory(String categoryName) throws SQLException {
        List<MenuItem> items = new ArrayList<>();
        String query = """
            SELECT mi.*, c.name as category_name 
            FROM menu_items mi
            JOIN categories c ON mi.category_id = c.id
            WHERE c.name = ?
            ORDER BY mi.name
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categoryName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new MenuItem(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBigDecimal("price"),
                            rs.getString("category_name")
                    ));
                }
            }
        }
        return items;
    }
}