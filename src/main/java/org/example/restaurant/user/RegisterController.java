package org.example.restaurant.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.example.restaurant.user.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void handleLoginRedirect() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/login-view.fxml"));
            Parent loginRoot = fxmlLoader.load();
            Scene loginScene = new Scene(loginRoot);

            // Récupérer la fenêtre à partir du champ déjà visible
            Stage currentStage = (Stage) emailField.getScene().getWindow();

            // Copier taille et position
            double width = currentStage.getWidth();
            double height = currentStage.getHeight();
            double x = currentStage.getX();
            double y = currentStage.getY();

            // Appliquer la scène
            currentStage.setScene(loginScene);
            currentStage.setWidth(width);
            currentStage.setHeight(height);
            currentStage.setX(x);
            currentStage.setY(y);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void registerUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        try (Connection conn = DataBaseConnection.connect()) {
            // Vérifier si l'email existe déjà
            String checkQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                showAlert("An account with this email already exists.");
                return;
            }

            // Insérer le nouvel utilisateur
            String insertQuery = "INSERT INTO users (name, email, password, role_id) VALUES (?, ?, ?, 1)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, name);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password);
            insertStmt.executeUpdate();

            // Récupérer l'ID du nouvel utilisateur
            String getUserQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement getUserStmt = conn.prepareStatement(getUserQuery);
            getUserStmt.setString(1, email);
            ResultSet userRs = getUserStmt.executeQuery();

            if (userRs.next()) {
                int userId = userRs.getInt("id");
                String userName = userRs.getString("name");
                String userEmail = userRs.getString("email");

                User loggedInUser = new User(userId, userName, userEmail);
                Session.setCurrentUser(loggedInUser);
            }

// Redirection vers accueil
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/accueil-view.fxml"));
            Parent accueilRoot = fxmlLoader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(accueilRoot));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Something went wrong. Please try again.");
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




}
