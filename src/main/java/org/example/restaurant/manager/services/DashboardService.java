package org.example.restaurant.manager.services;



import org.example.restaurant.manager.database.DBConnection;

import org.example.restaurant.manager.models.Reservation;
import org.example.restaurant.manager.models.TopDish;
import org.example.restaurant.manager.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DashboardService {

    public int getTotalReservations() {
        String sql = "SELECT COUNT(*) FROM reservations";
        return getCountFromQuery(sql);
    }

    public int getTodayReservations() {
        String sql = "SELECT COUNT(*) FROM reservations WHERE DATE(date) = CURDATE()";
        return getCountFromQuery(sql);
    }

    public int getTotalCommandes() {
        String sql = "SELECT COUNT(*) FROM orders";
        return getCountFromQuery(sql);
    }

    public int getTodayCommandes() {
        String sql = "SELECT COUNT(*) FROM orders WHERE DATE(created_at) = CURDATE()";
        return getCountFromQuery(sql);
    }

    public double getTotalRevenus() {
        String sql = "SELECT SUM(total) FROM orders";
        return getDoubleFromQuery(sql);
    }

    public double getMonthlyRevenus() {
        String sql = "SELECT SUM(total) FROM orders WHERE MONTH(created_at) = MONTH(CURRENT_DATE()) AND YEAR(created_at) = YEAR(CURRENT_DATE())";
        return getDoubleFromQuery(sql);
    }


    public List<Reservation> getRecentReservations(int limit) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, u.name as user_name FROM reservations r " +
                "JOIN users u ON r.user_id = u.id " +
                "ORDER BY r.date DESC, r.time DESC LIMIT ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reservation res = new Reservation();
                res.setId(rs.getInt("id"));
                res.setUserId(rs.getInt("user_id"));
                res.setUserName(rs.getString("user_name"));
                res.setDate(rs.getDate("date").toLocalDate());
                res.setTime(rs.getString("time"));
                res.setGuests(rs.getInt("guests"));
                res.setStatus(rs.getString("status"));
                reservations.add(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public Map<String, Integer> getOrdersByMonth(int monthsBack) {
        Map<String, Integer> result = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH);

        for (int i = monthsBack; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            String monthStr = month.format(formatter);
            result.put(monthStr, 0);
        }

        String sql = "SELECT DATE_FORMAT(created_at, '%b %Y') as month, COUNT(*) as count " +
                "FROM orders " +
                "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL ? MONTH) " +
                "GROUP BY month " +
                "ORDER BY MIN(created_at)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, monthsBack);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String month = rs.getString("month");
                int count = rs.getInt("count");
                result.put(month, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<TopDish> getTopDishes(int limit) {
        List<TopDish> list = new ArrayList<>();
        String sql = "SELECT m.name, COUNT(oi.id) AS order_count " +
                "FROM order_items oi " +
                "JOIN menu_items m ON oi.menu_item_id = m.id " +
                "GROUP BY m.name ORDER BY order_count DESC LIMIT ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new TopDish(rs.getString("name"), rs.getInt("order_count")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<Integer, Long> getReviewsDistribution() {
        Map<Integer, Long> distribution = new TreeMap<>();
        String sql = "SELECT rating, COUNT(*) as count FROM reviews GROUP BY rating ORDER BY rating";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                distribution.put(rs.getInt("rating"), rs.getLong("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // S'assurer que toutes les notes de 1 à 5 sont présentes
        for (int i = 1; i <= 5; i++) {
            distribution.putIfAbsent(i, 0L);
        }

        return distribution;
    }

    private int getCountFromQuery(String sql) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double getDoubleFromQuery(String sql) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}