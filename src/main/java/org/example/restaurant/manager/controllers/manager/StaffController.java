package org.example.restaurant.manager.controllers.manager;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import org.example.restaurant.manager.dao.UserDAO;
import org.example.restaurant.manager.models.User;
import org.example.restaurant.manager.utils.DatabaseConfig;
import org.example.restaurant.manager.utils.PasswordUtils;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class StaffController {

    @FXML private TableView<User> staffTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Void> actionColumn;

    private UserDAO userDAO;
    private ObservableList<User> staffList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            Connection connection = DatabaseConfig.getConnection();
            userDAO = new UserDAO(connection);
            setupTableColumns();
            loadStaffData();
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de se connecter à la base de données", Alert.AlertType.ERROR);
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        setupActionButtons();
    }

    private void loadStaffData() {
        try {
            staffList.setAll(userDAO.getUsersByRole(2));
            staffTable.setItems(staffList);
        } catch (SQLException e) {
            showAlert("Erreur", "Échec du chargement du personnel", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddStaff() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un membre du staff");

        // Configuration des boutons
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Création du formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        grid.addRow(0, new Label("Nom:"), nameField);
        grid.addRow(1, new Label("Email:"), emailField);
        grid.addRow(2, new Label("Mot de passe:"), passwordField);
        grid.addRow(3, new Label("Confirmation:"), confirmPasswordField);

        // Validation
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        ChangeListener<String> validator = (obs, oldVal, newVal) -> {
            boolean valid = !nameField.getText().isEmpty()
                    && !emailField.getText().isEmpty()
                    && passwordField.getText().length() >= 6
                    && passwordField.getText().equals(confirmPasswordField.getText());
            saveButton.setDisable(!valid);
        };

        nameField.textProperty().addListener(validator);
        emailField.textProperty().addListener(validator);
        passwordField.textProperty().addListener(validator);
        confirmPasswordField.textProperty().addListener(validator);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                return new User(0, nameField.getText(), emailField.getText(), 2, "Staff", passwordField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newStaff -> {
            try {
                if (userDAO.emailExists(newStaff.getEmail())) {
                    showAlert("Erreur", "Cet email existe déjà", Alert.AlertType.ERROR);
                    return;
                }

                newStaff.setPassword(PasswordUtils.hashPassword(newStaff.getPassword()));
                userDAO.addStaff(newStaff);
                refreshStaffList();
                showAlert("Succès", "Staff ajouté avec succès", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void setupActionButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttons = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("edit-button");
                deleteBtn.getStyleClass().add("delete-button");

                editBtn.setOnAction(event -> editStaff(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> deleteStaff(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
    }

    private void editStaff(User staff) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Modifier le staff");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(staff.getName());
        TextField emailField = new TextField(staff.getEmail());

        grid.addRow(0, new Label("Nom:"), nameField);
        grid.addRow(1, new Label("Email:"), emailField);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                staff.setName(nameField.getText());
                staff.setEmail(emailField.getText());
                return staff;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedStaff -> {
            try {
                userDAO.updateStaff(updatedStaff);
                refreshStaffList();
                showAlert("Succès", "Staff modifié avec succès", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la modification", Alert.AlertType.ERROR);
            }
        });
    }

    private void deleteStaff(User staff) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer " + staff.getName() + "?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userDAO.deleteStaff(staff.getId());
                    refreshStaffList();
                    showAlert("Succès", "Staff supprimé avec succès", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors de la suppression", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void refreshStaffList() {
        try {
            staffList.setAll(userDAO.getUsersByRole(2));
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}