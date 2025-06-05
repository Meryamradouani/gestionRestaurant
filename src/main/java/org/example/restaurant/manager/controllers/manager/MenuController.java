package org.example.restaurant.manager.controllers.manager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.restaurant.manager.dao.MenuItemDAO;
import org.example.restaurant.manager.models.MenuItem;
import org.example.restaurant.manager.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class MenuController {

    @FXML private TableView<MenuItem> menuTable;
    @FXML private TableColumn<MenuItem, Integer> idColumn;
    @FXML private TableColumn<MenuItem, String> nameColumn;
    @FXML private TableColumn<MenuItem, Double> priceColumn;
    @FXML private TableColumn<MenuItem, String> descriptionColumn;
    @FXML private TableColumn<MenuItem, Void> actionColumn;
    @FXML private AnchorPane rootPane;
    private MenuItemDAO menuItemDAO;

    @FXML
    public void initialize() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            menuItemDAO = new MenuItemDAO(conn);

            // Configuration des colonnes
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

            // Configuration de la colonne d'actions
            actionColumn.setCellFactory(param -> new TableCell<>() {
                private final Button editButton = new Button("Modifier");
                private final Button deleteButton = new Button("Supprimer");
                private final HBox container = new HBox(10, editButton, deleteButton);

                {
                    container.setAlignment(Pos.CENTER);
                    editButton.getStyleClass().add("edit-btn");
                    deleteButton.getStyleClass().add("delete-btn");

                    editButton.setOnAction(event -> {
                        MenuItem item = getTableView().getItems().get(getIndex());
                        handleEditMenuItem(item);
                    });

                    deleteButton.setOnAction(event -> {
                        MenuItem item = getTableView().getItems().get(getIndex());
                        handleDeleteMenuItem(item);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : container);
                }
            });

            refreshMenuTable();

        } catch (SQLException e) {
            showAlert("Erreur", "Connexion à la base de données échouée: " + e.getMessage());
        }
    }

    @FXML
    private void refreshMenuTable() {
        try {
            menuTable.setItems(FXCollections.observableArrayList(menuItemDAO.getAllMenuItems()));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger le menu: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddMenuItem() {
        Dialog<MenuItem> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un nouvel élément");

        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextArea descriptionField = new TextArea();

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Nom:"), nameField,
                new Label("Prix:"), priceField,
                new Label("Description:"), descriptionField
        ));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    return new MenuItem(
                            0,
                            nameField.getText(),
                            descriptionField.getText(),
                            Double.parseDouble(priceField.getText()),
                            1 // Catégorie par défaut
                    );
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Veuillez entrer un prix valide");
                    return null;
                }
            }
            return null;
        });

        Optional<MenuItem> result = dialog.showAndWait();
        result.ifPresent(menuItem -> {
            try {
                menuItemDAO.addMenuItem(menuItem);
                refreshMenuTable();
                showAlert("Succès", "Élément ajouté avec succès");
            } catch (SQLException e) {
                showAlert("Erreur", "Échec de l'ajout: " + e.getMessage());
            }
        });
    }

    private void handleEditMenuItem(MenuItem item) {
        Dialog<MenuItem> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'élément");

        TextField nameField = new TextField(item.getName());
        TextField priceField = new TextField(String.valueOf(item.getPrice()));
        TextArea descriptionField = new TextArea(item.getDescription());

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Nom:"), nameField,
                new Label("Prix:"), priceField,
                new Label("Description:"), descriptionField
        ));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    item.setName(nameField.getText());
                    item.setDescription(descriptionField.getText());
                    item.setPrice(Double.parseDouble(priceField.getText()));
                    return item;
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Veuillez entrer un prix valide");
                    return null;
                }
            }
            return null;
        });

        Optional<MenuItem> result = dialog.showAndWait();
        result.ifPresent(updatedItem -> {
            try {
                menuItemDAO.updateMenuItem(updatedItem);
                refreshMenuTable();
                showAlert("Succès", "Élément modifié avec succès");
            } catch (SQLException e) {
                showAlert("Erreur", "Échec de la modification: " + e.getMessage());
            }
        });
    }

    private void handleDeleteMenuItem(MenuItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer " + item.getName() + "?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                menuItemDAO.deleteMenuItem(item.getId());
                refreshMenuTable();
                showAlert("Succès", "Élément supprimé avec succès");
            } catch (SQLException e) {
                showAlert("Erreur", "Échec de la suppression: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}