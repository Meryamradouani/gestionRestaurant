package org.example.restaurant.manager.dao;

import org.example.restaurant.manager.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    // Récupère tous les staffs (role_id = 2)
    public List<User> getAllStaff() throws SQLException {
        return getUsersByRole(2); // Utilise la méthode générique
    }

    // Méthode générique pour récupérer les utilisateurs par rôle
    public List<User> getUsersByRole(int roleId) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.id, u.name, u.email, u.password, u.role_id, r.name as role_name " +
                "FROM users u JOIN roles r ON u.role_id = r.id " +
                "WHERE u.role_id = ? ORDER BY u.name";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, roleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getInt("role_id"),
                            rs.getString("role_name"),
                            rs.getString("password") // Correction ici
                    ));
                }
            }
        }
        return users;
    }

    // Met à jour un membre du staff
    public void updateStaff(User staff) throws SQLException {
        String query = "UPDATE users SET name = ?, email = ? WHERE id = ? AND role_id = 2";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, staff.getName());
            pstmt.setString(2, staff.getEmail());
            pstmt.setInt(3, staff.getId());

            pstmt.executeUpdate();
        }
    }

    // Supprime un membre du staff
    public void deleteStaff(int id) throws SQLException {
        String query = "DELETE FROM users WHERE id = ? AND role_id = 2";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // Vérifie l'existence d'un email
    public boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Ajoute un nouveau membre du staff
    public void addStaff(User staff) throws SQLException {
        String query = "INSERT INTO users (name, email, password, role_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, staff.getName());
            pstmt.setString(2, staff.getEmail());
            pstmt.setString(3, staff.getPassword());
            pstmt.setInt(4, staff.getRoleId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec de l'ajout, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    staff.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
}