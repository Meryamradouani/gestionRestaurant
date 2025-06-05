package org.example.restaurant.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.restaurant.user.models.User;
import org.example.restaurant.user.Session;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ReservationController {

    @FXML
    private ComboBox<String> personComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private Button bookNowButton;

    @FXML
    private Button bookTableButton;


    @FXML
    private void initialize() {
        bookTableButton.setOnAction(e -> loadReservationPage());

        personComboBox.getItems().addAll("1", "2", "3", "4", "5", "6");
        timeComboBox.getItems().addAll("10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM",
                "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM");

    }



    @FXML
    private void handleBookNow() {
        String personValue = personComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String timeString = timeComboBox.getValue();

        // Vérifie que tous les champs sont remplis
        if (personValue == null || date == null || timeString == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs avant de réserver.");
            alert.showAndWait();
            return;
        }

        try {
            Integer guests = Integer.valueOf(personValue);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
            LocalTime time = LocalTime.parse(timeString, formatter);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/confirmation-popup.fxml"));
            Parent root = loader.load();

            ConfirmationPopupController controller = loader.getController();
            controller.setReservationDetails(date, time, guests);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Confirmation de réservation");
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Une erreur est survenue");
            errorAlert.setContentText("Impossible de traiter la réservation.");
            errorAlert.showAndWait();
        }
    }


    public void goToProfile(ActionEvent event) {
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
