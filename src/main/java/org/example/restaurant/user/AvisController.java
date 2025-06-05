package org.example.restaurant.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.restaurant.user.models.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AvisController {


    @FXML
    private TextField commentField;

    @FXML
    private TextField ratingField;

    @FXML
    private Button submitReviewButton;
    @FXML
    private Button bookTableButton;

    @FXML
    private void initialize() {
        bookTableButton.setOnAction(e -> loadReservationPage());
    }



    @FXML
    private void handleSubmitReview() {
        String comment = commentField.getText().trim();
        String ratingText = ratingField.getText().trim();

        // Vérification de l'utilisateur connecté
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            showAlert("Erreur", "Vous devez être connecté pour soumettre un avis.");
            return;
        }

        // Vérification des champs
        if (comment.isEmpty() || ratingText.isEmpty()) {
            showAlert("Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        int rating;
        try {
            rating = Integer.parseInt(ratingText);
            if (rating < 1 || rating > 5) {
                showAlert("Note invalide", "La note doit être comprise entre 1 et 5.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de format", "La note doit être un nombre entre 1 et 5.");
            return;
        }

        try (Connection conn = DataBaseConnection.connect()) {
            String sql = "INSERT INTO reviews (user_id, comment, rating, created_at) VALUES (?, ?, ?, NOW())";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUser.getId());
            stmt.setString(2, comment);
            stmt.setInt(3, rating);

            stmt.executeUpdate();

            showAlert("Merci !", "Votre avis a bien été envoyé.");
            commentField.clear();
            ratingField.clear();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'enregistrement de votre avis.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void loadReservationPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/reservation-view.fxml"));
            Parent reservationRoot = loader.load();
            Scene scene = bookTableButton.getScene(); // or reservationButton.getScene()
            scene.setRoot(reservationRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
