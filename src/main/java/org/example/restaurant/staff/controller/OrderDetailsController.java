package org.example.restaurant.staff.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import org.example.restaurant.staff.model.Order;
import org.example.restaurant.staff.model.OrderItem;
import org.example.restaurant.staff.dao.OrderDAO;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.net.URL;

public class OrderDetailsController {
    @FXML private Label orderTitleLabel;
    @FXML private Label clientNameLabel;
    @FXML private Label dateLabel;
    @FXML private Label statusLabel;
    @FXML private Label totalLabel;

    @FXML private TableView<OrderItem> orderItemsTable;
    @FXML private TableColumn<OrderItem, String> itemNameColumn;
    @FXML private TableColumn<OrderItem, Integer> quantityColumn;
    @FXML private TableColumn<OrderItem, String> priceColumn;
    @FXML private TableColumn<OrderItem, String> subtotalColumn;

    private final OrderDAO orderDAO = new OrderDAO();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private Order currentOrder;

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f €", cellData.getValue().getPrice())));
        subtotalColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f €", cellData.getValue().getSubtotal())));
    }

    public void setOrder(Order order) {
        this.currentOrder = order;
        updateView();
    }

    private void updateView() {
        if (currentOrder == null) return;

        // Mise à jour des informations de base
        orderTitleLabel.setText("Commande #" + currentOrder.getId());
        clientNameLabel.setText(currentOrder.getUserName());
        dateLabel.setText(currentOrder.getCreatedAt().format(dateFormatter));
        statusLabel.setText(currentOrder.getStatus());

        // Chargement des articles
        try {
            List<OrderItem> items = orderDAO.getOrderItems(currentOrder.getId());
            ObservableList<OrderItem> orderItems = FXCollections.observableArrayList(items);
            orderItemsTable.setItems(orderItems);

            // Mise à jour du total
            double total = items.stream()
                    .mapToDouble(item -> item.getSubtotal().doubleValue())
                    .sum();
            totalLabel.setText(String.format("%.2f €", total));
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les articles de la commande: " + e.getMessage());
        }
    }

    @FXML
    private void handlePrint() {
        try {
            System.out.println("Opening invoice for order ID: " + currentOrder.getId()); // Debug log

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/restaurant/staff/fxml/InvoiceView.fxml"));
            Scene scene = new Scene(loader.load());

            // Appliquer le CSS à la nouvelle scène
            URL cssUrl = getClass().getResource("/org/example/restaurant/staff/styles/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Warning: CSS file not found at /org/example/restaurant/staff/styles/styles.css");
            }

            InvoiceViewController controller = loader.getController();
            controller.setOrder(currentOrder);

            Stage stage = new Stage();
            stage.setTitle("Facture - Commande #" + currentOrder.getId());
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);

            System.out.println("Showing invoice window"); // Debug log
            stage.show();

        } catch (IOException e) {
            System.err.println("Error opening invoice: " + e.getMessage()); // Debug log
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir la facture: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) orderTitleLabel.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}