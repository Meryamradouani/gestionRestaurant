package org.example.restaurant.manager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Mettez votre mot de passe ici

    public static void init() {
        try {
            // Charger le pilote JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir la connexion
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connexion à la base de données établie avec succès");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur: Pilote JDBC non trouvé");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        // Vérifier si la connexion est toujours valide
        try {
            if (connection == null || connection.isClosed()) {
                init(); // Reconnecter si nécessaire
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}