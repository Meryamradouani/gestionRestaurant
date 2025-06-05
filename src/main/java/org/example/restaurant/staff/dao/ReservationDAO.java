package org.example.restaurant.staff.dao;

import org.example.restaurant.staff.model.Reservation;
import org.example.restaurant.staff.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationDAO {
    private Connection connection;

    public ReservationDAO() {
        try {
            this.connection = DatabaseConfig.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reservation> getReservationsForToday() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, u.name as user_name FROM reservations r " +
                "JOIN users u ON r.user_id = u.id " +
                "WHERE r.date = CURDATE() " +
                "ORDER BY r.time";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Combiner la date et l'heure en un Timestamp
                LocalDate date = rs.getDate("date").toLocalDate();
                LocalTime time = rs.getTime("time").toLocalTime();
                Timestamp timestamp = Timestamp.valueOf(date.atTime(time));

                reservations.add(new Reservation(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        timestamp,
                        rs.getInt("guests"),
                        rs.getString("status"),
                        rs.getString("contact_message")
                ));
            }
        }
        return reservations;
    }

    public void updateReservationStatus(int reservationId, String status) throws SQLException {
        String query = "UPDATE reservations SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, reservationId);
            pstmt.executeUpdate();
        }
    }
}