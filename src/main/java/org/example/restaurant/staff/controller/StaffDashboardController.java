package org.example.restaurant.staff.controller;

import org.example.restaurant.staff.dao.OrderDAO;
import org.example.restaurant.staff.dao.ReservationDAO;
import org.example.restaurant.staff.model.Order;
import org.example.restaurant.staff.model.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class StaffDashboardController {
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> timeColumn;
    @FXML private TableColumn<Reservation, String> nameColumn;
    @FXML private TableColumn<Reservation, Integer> guestsColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private TableColumn<Reservation, String> messageColumn;

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, String> orderTimeColumn;
    @FXML private TableColumn<Order, String> orderClientColumn;
    @FXML private TableColumn<Order, String> orderTotalColumn;
    @FXML private TableColumn<Order, String> orderStatusColumn;

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private int currentStaffId;

    @FXML
    public void initialize() {
        setupReservationsTable();
        setupOrdersTable();
        refreshData();
    }

    private void setupReservationsTable() {
        timeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTime().toLocalDateTime().format(timeFormatter)));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        guestsColumn.setCellValueFactory(new PropertyValueFactory<>("guests"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("contactMessage"));
    }

    private void setupOrdersTable() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCreatedAt().format(timeFormatter)));
        orderClientColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        orderTotalColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTotal().toString()));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    public void setStaffId(int staffId) {
        this.currentStaffId = staffId;
        refreshData();
    }

    @FXML
    private void handleRefresh() {
        refreshData();
    }

    private void refreshData() {
        try {
            ObservableList<Reservation> reservations = FXCollections.observableArrayList(
                    reservationDAO.getReservationsForToday()
            );
            reservationsTable.setItems(reservations);

            ObservableList<Order> orders = FXCollections.observableArrayList(
                    orderDAO.getOrdersForToday()
            );
            ordersTable.setItems(orders);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des données", e.getMessage());
        }
    }

    @FXML
    private void handleCreateOrder() {
        Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showError("Erreur", "Veuillez sélectionner une réservation");
            return;
        }

        if (currentStaffId <= 0) {
            showError("Erreur", "ID du staff invalide. Veuillez vous reconnecter.");
            return;
        }

        try {
            System.out.println("Opening order dialog..."); // Debug log

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/restaurant/staff/fxml/OrderDialog.fxml"));
            DialogPane dialogPane = loader.load();

            // Appliquer le CSS au DialogPane
            URL cssUrl = getClass().getResource("/org/example/restaurant/staff/styles/styles.css");
            if (cssUrl != null) {
                dialogPane.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Warning: CSS file not found at /org/example/restaurant/staff/styles/styles.css");
            }

            OrderDialogController controller = loader.getController();
            controller.setStaffId(currentStaffId);
            controller.setReservation(selectedReservation);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Nouvelle commande");

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    System.out.println("Order dialog closed with OK"); // Debug log
                    refreshData(); // Rafraîchir les données après la création de la commande
                }
            });
        } catch (IOException e) {
            System.err.println("Error opening order dialog: " + e.getMessage()); // Debug log
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir la fenêtre de commande: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewOrderDetails() {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showError("Erreur", "Veuillez sélectionner une commande");
            return;
        }

        try {
            System.out.println("Opening order details for order ID: " + selectedOrder.getId()); // Debug log

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/staff/fxml/OrderDetailsView.fxml"));
            Scene scene = new Scene(loader.load());

            // Appliquer le CSS à la nouvelle scène
            URL cssUrl = getClass().getResource("/org/example/restaurant/staff/styles/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Warning: CSS file not found at /org/example/restaurant/staff/styles/styles.css");
            }

            OrderDetailsController controller = loader.getController();
            controller.setOrder(selectedOrder);

            Stage stage = new Stage();
            stage.setTitle("Détails de la commande #" + selectedOrder.getId());
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);

            System.out.println("Showing order details window"); // Debug log
            stage.show();

        } catch (IOException e) {
            System.err.println("Error opening order details: " + e.getMessage()); // Debug log
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir les détails de la commande: " + e.getMessage());
        }
    }

    @FXML
    private void handlePrintInvoice() {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showError("Erreur", "Veuillez sélectionner une commande");
            return;
        }

        try {
            System.out.println("Opening invoice for order ID: " + selectedOrder.getId()); // Debug log

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/org/example/restaurant/staff/fxml/InvoiceView.fxml"));
            Scene scene = new Scene(loader.load());

            // Appliquer le CSS à la nouvelle scène
            URL cssUrl = getClass().getResource("/org/example/restaurant/staff/styles/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Warning: CSS file not found at /org/example/restaurant/staff/styles/styles.css");
            }

            InvoiceViewController controller = loader.getController();
            controller.setOrder(selectedOrder);

            Stage stage = new Stage();
            stage.setTitle("Facture - Commande #" + selectedOrder.getId());
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);

            System.out.println("Showing invoice window..."); // Debug log
            stage.show();
            System.out.println("Invoice window shown"); // Debug log

        } catch (IOException e) {
            System.err.println("Error opening invoice: " + e.getMessage()); // Debug log
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir la facture: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        Stage stage = (Stage) reservationsTable.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}