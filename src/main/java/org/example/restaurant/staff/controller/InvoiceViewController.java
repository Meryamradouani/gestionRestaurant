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

public class InvoiceViewController {
    @FXML private Label invoiceNumberLabel;
    @FXML private Label dateLabel;
    @FXML private Label clientNameLabel;
    @FXML private Label orderDateLabel;
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
        invoiceNumberLabel.setText("Facture #" + currentOrder.getId());
        dateLabel.setText("Date: " + currentOrder.getCreatedAt().format(dateFormatter));
        clientNameLabel.setText(currentOrder.getUserName());
        orderDateLabel.setText(currentOrder.getCreatedAt().format(dateFormatter));

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
        // TODO: Implémenter l'impression réelle
        showInfo("Information", "Fonctionnalité d'impression à venir");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) invoiceNumberLabel.getScene().getWindow();
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