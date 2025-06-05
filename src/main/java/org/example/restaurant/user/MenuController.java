package org.example.restaurant.user;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MenuController {


    @FXML
    private Button bookTableButton;

    @FXML
    private VBox menuContainer;
    public void initialize() {
        bookTableButton.setOnAction(e -> loadReservationPage());

        try (Connection conn = DataBaseConnection.connect()) {
            String categoryQuery = "SELECT * FROM categories";
            Statement stmt = conn.createStatement();
            ResultSet categoryRs = stmt.executeQuery(categoryQuery);

            while (categoryRs.next()) {
                int categoryId = categoryRs.getInt("id");
                String categoryName = categoryRs.getString("name").toLowerCase(); // pour comparaison

                VBox categoryBox = new VBox(10);
                categoryBox.setStyle(
                        "-fx-background-color: #F4F0F0;" +
                                "-fx-padding: 40px;" +
                                "-fx-border-color: #B0964F;" +
                                "-fx-border-radius: 15px;" +
                                "-fx-background-radius: 15px;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 4);"
                );
                categoryBox.setMaxWidth(800);

                // Préparation de l'image (non ronde)
                javafx.scene.image.ImageView imageView = null;
                if (categoryName.equals("starter") || categoryName.equals("main course") || categoryName.equals("dessert") || categoryName.equals("boisson")) {
                    String imagePath = switch (categoryName) {
                        case "starter" -> "/org/example/restaurant/user/images/starter.png";
                        case "main course" -> "/org/example/restaurant/user/images/main.png";
                        case "dessert" -> "/org/example/restaurant/user/images/dessert.png";
                        case "boisson" -> "/org/example/restaurant/user/images/boisson.png";

                        default -> null;
                    };

                    if (imagePath != null) {
                        imageView = new javafx.scene.image.ImageView(
                                new javafx.scene.image.Image(getClass().getResourceAsStream(imagePath))
                        );
                        imageView.setFitWidth(130);
                        imageView.setFitHeight(130);
                        imageView.setSmooth(true);
                        imageView.setCache(true);
                        // plus de clip rond ici
                    }
                }

                // Titre avec image à droite
                Label categoryTitle = new Label(categoryName.toUpperCase());
                categoryTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #B0964F;");

                HBox titleBox = new HBox(10);
                titleBox.setMaxWidth(Double.MAX_VALUE);
                titleBox.setStyle("-fx-alignment: center-left;");

                Pane spacer = new Pane();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                if (imageView != null) {
                    titleBox.getChildren().addAll(categoryTitle, spacer, imageView);
                } else {
                    titleBox.getChildren().add(categoryTitle);
                }

                categoryBox.getChildren().add(titleBox);

                // Items de la catégorie
                String itemQuery = "SELECT * FROM menu_items WHERE category_id = ?";
                PreparedStatement itemStmt = conn.prepareStatement(itemQuery);
                itemStmt.setInt(1, categoryId);
                ResultSet itemRs = itemStmt.executeQuery();

                while (itemRs.next()) {
                    String itemName = itemRs.getString("name");
                    String itemDesc = itemRs.getString("description");
                    double itemPrice = itemRs.getDouble("price");

                    HBox itemLine = new HBox(10);
                    Label name = new Label(itemName);
                    name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1B4E31;");
                    Label price = new Label(String.format("%.2f DH", itemPrice));
                    price.setStyle("-fx-font-size: 16px; -fx-text-fill: #B0964F;");
                    Pane lineSpacer = new Pane();
                    HBox.setHgrow(lineSpacer, Priority.ALWAYS);
                    itemLine.getChildren().addAll(name, lineSpacer, price);

                    Label description = new Label(itemDesc);
                    description.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
                    description.setWrapText(true);

                    VBox itemBlock = new VBox(3);
                    itemBlock.getChildren().addAll(itemLine, description);

                    categoryBox.getChildren().add(itemBlock);
                }

                menuContainer.getChildren().add(categoryBox);
            }

        } catch (SQLException e) {
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
