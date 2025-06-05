package org.example.restaurant.user;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class SuccessPopupController {

    @FXML
    private Label userInfoLabel;

    @FXML
    private Label reservationInfoLabel;

    public void setSuccessDetails(String name, String email, String date, String time, int guests) {
        userInfoLabel.setText(name + " | " + email);
        reservationInfoLabel.setText(date + " at " + time + " | " + guests + " Guests");
    }

    @FXML
    private void handleDone(ActionEvent event) {
        // Fermer la fenêtre popup
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
