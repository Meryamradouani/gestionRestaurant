package org.example.restaurant.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.example.restaurant.user.models.User;
import org.example.restaurant.user.Session;

public class ConfirmationPopupController {

    @FXML
    private Label dateLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label guestsLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextArea messageArea;

    @FXML
    private Button confirmBookButton;

    private LocalDate selectedDate;
    private LocalTime selectedTime;
    private int selectedGuests;

    public void setReservationDetails(LocalDate date, LocalTime time, int guests) {
        this.selectedDate = date;
        this.selectedTime = time;
        this.selectedGuests = guests;

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        dateLabel.setText(date.toString());
        timeLabel.setText(time.format(timeFormatter));
        guestsLabel.setText(String.valueOf(guests));

        // Récupérer utilisateur courant
        User user = Session.getCurrentUser();
        nameLabel.setText(user.getName());
        emailLabel.setText(user.getEmail());
    }

    @FXML
    private void confirmReservation() {
        String message = messageArea.getText();
        User user = Session.getCurrentUser();

        try (Connection conn = DataBaseConnection.connect()) {

            // ✅ 1. Vérifier si ce user a déjà une réservation pour cette date et heure
            String userCheckSql = "SELECT COUNT(*) FROM reservations WHERE user_id = ? AND date = ? AND time = ?";
            PreparedStatement userCheckStmt = conn.prepareStatement(userCheckSql);
            userCheckStmt.setInt(1, user.getId());
            userCheckStmt.setDate(2, java.sql.Date.valueOf(selectedDate));
            userCheckStmt.setTime(3, java.sql.Time.valueOf(selectedTime));
            ResultSet userRs = userCheckStmt.executeQuery();

            if (userRs.next() && userRs.getInt(1) > 0) {
                showAlert("Vous avez déjà une réservation à cette date et heure.");
                return;
            }

            // ✅ 2. Vérifier s’il y a déjà 15 réservations à ce créneau
            String totalCheckSql = "SELECT COUNT(*) FROM reservations WHERE date = ? AND time = ?";
            PreparedStatement totalCheckStmt = conn.prepareStatement(totalCheckSql);
            totalCheckStmt.setDate(1, java.sql.Date.valueOf(selectedDate));
            totalCheckStmt.setTime(2, java.sql.Time.valueOf(selectedTime));
            ResultSet totalRs = totalCheckStmt.executeQuery();

            if (totalRs.next() && totalRs.getInt(1) >= 15) {
                showAlert("Toutes les tables sont réservées à cette heure. Veuillez choisir un autre créneau.");
                return;
            }

            // ✅ 3. Insertion si tout est OK
            String sql = "INSERT INTO reservations (user_id, date, time, guests, contact_message) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getId());
            stmt.setDate(2, java.sql.Date.valueOf(selectedDate));
            stmt.setTime(3, java.sql.Time.valueOf(selectedTime));
            stmt.setInt(4, selectedGuests);
            stmt.setString(5, message);
            stmt.executeUpdate();

            // ✅ 4. Afficher popup de succès
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/success-popup.fxml"));
            Parent root = fxmlLoader.load();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root));
            popupStage.setTitle("Success");
            popupStage.show();

            // ✅ 5. Fermer la popup actuelle
            Stage currentStage = (Stage) confirmBookButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
