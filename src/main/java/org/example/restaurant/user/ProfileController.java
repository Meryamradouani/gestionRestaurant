// ProfileController.java
package org.example.restaurant.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.restaurant.user.models.User;
import org.example.restaurant.user.Session;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileController {



    @FXML
    private Button bookTableButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ListView<String> reservationListView;

    @FXML
    private void initialize() {
        bookTableButton.setOnAction(e -> loadReservationPage());

        User user = Session.getCurrentUser();
        if (user != null) {
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            loadReservationHistory(user.getId());
        }

    }
    private void loadReservationPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/reservation-view.fxml"));
            Parent reservationRoot = loader.load();
            Scene scene = bookTableButton.getScene();
            scene.setRoot(reservationRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadReservationHistory(int userId) {
        try (Connection conn = DataBaseConnection.connect()) {
            String sql = "SELECT date, time, guests FROM reservations WHERE user_id = ? ORDER BY created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String date = rs.getString("date");
                String time = rs.getString("time");
                int guests = rs.getInt("guests");
                reservationListView.getItems().add(date + " - " + time + " - " + guests + " Guests");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String currentPwd = currentPasswordField.getText();
        String newPwd = newPasswordField.getText();
        String confirmPwd = confirmPasswordField.getText();

        User user = Session.getCurrentUser();
        if (user == null) return;

        try (Connection conn = DataBaseConnection.connect()) {
            // Vérifier le mot de passe actuel
            String checkSql = "SELECT password FROM users WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, user.getId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && !rs.getString("password").equals(currentPwd)) {
                showAlert("Mot de passe actuel incorrect.");
                return;
            }

            // Si les nouveaux mots de passe sont remplis mais différents
            if (!newPwd.isEmpty() && !newPwd.equals(confirmPwd)) {
                showAlert("Les nouveaux mots de passe ne correspondent pas.");
                return;
            }

            // Préparer la requête de mise à jour
            String updateSql = "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, name);
            updateStmt.setString(2, email);
            updateStmt.setString(3, newPwd.isEmpty() ? currentPwd : newPwd);
            updateStmt.setInt(4, user.getId());

            updateStmt.executeUpdate();

            // Mettre à jour l'objet en session
            user.setName(name);
            user.setEmail(email);

            showAlert("Profil mis à jour avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la mise à jour du profil.");
        }
    }


    @FXML
    private void handleSignOut() {
        Session.setCurrentUser(null);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    @FXML
    private void goToHome(javafx.scene.input.MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/accueil-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Label) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToMenu(javafx.scene.input.MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/menu-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToProfile(javafx.scene.input.MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/profil-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goToReview(javafx.scene.input.MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/avis-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
