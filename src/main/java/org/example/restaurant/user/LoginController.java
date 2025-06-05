package org.example.restaurant.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.restaurant.user.models.User;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;

public class LoginController {

    @FXML
    private javafx.scene.control.Label errorLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    protected void handleRegisterRedirect() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/register-view.fxml"));
            Parent registerRoot = fxmlLoader.load();
            Scene registerScene = new Scene(registerRoot);

            //  Récupérer la fenêtre actuelle depuis emailField
            Stage currentStage = (Stage) emailField.getScene().getWindow();

            //  Sauvegarder taille + position
            double width = currentStage.getWidth();
            double height = currentStage.getHeight();
            double x = currentStage.getX();
            double y = currentStage.getY();

            //  Appliquer la nouvelle scène
            currentStage.setScene(registerScene);

            //  Réappliquer la taille et la position
            currentStage.setWidth(width);
            currentStage.setHeight(height);
            currentStage.setX(x);
            currentStage.setY(y);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loginUser() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try (Connection conn = DataBaseConnection.connect()) {
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            var stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            var rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String userName = rs.getString("name");
                String userEmail = rs.getString("email");

                User loggedInUser = new User(userId, userName, userEmail);
                Session.setCurrentUser(loggedInUser);
                int roleId = rs.getInt("role_id");

                Stage stage = (Stage) emailField.getScene().getWindow();

                double width = stage.getWidth();
                double height = stage.getHeight();
                double x = stage.getX();
                double y = stage.getY();

                Scene scene;
                if (roleId == 1) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/user/accueil-view.fxml"));
                    Parent root = loader.load();
                    scene = new Scene(root);
                } else if (roleId == 2) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/staff/fxml/StaffDashboard.fxml"));
                    Parent root = loader.load();

                    // Passer l'ID au contrôleur
                    org.example.restaurant.staff.controller.StaffDashboardController controller = loader.getController();
                    controller.setStaffId(userId);

                    scene = new Scene(root);


                    stage.setTitle("Tableau de bord Staff");
                } else if (roleId == 3) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/manager/view/manager/DashboardManager.fxml"));
                    Parent root = loader.load();
                    scene = new Scene(root);
                } else {
                    System.out.println("Rôle inconnu");
                    return;
                }

                stage.setScene(scene);
                stage.setWidth(width);
                stage.setHeight(height);
                stage.setX(x);
                stage.setY(y);

            } else {
                errorLabel.setText("Email ou mot de passe incorrect.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
