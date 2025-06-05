package org.example.restaurant.manager.controllers.manager;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.restaurant.manager.dao.OrderDAO;
import org.example.restaurant.manager.models.Order;
import org.example.restaurant.manager.utils.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrdersController {

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, String> customerNameColumn;
    @FXML private TableColumn<Order, String> statusColumn;

    private OrderDAO orderDAO;

    @FXML
    public void initialize() {
        // Configure les colonnes de la table
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        try {
            // Initialisation de la connexion à la base de données
            Connection connection = DatabaseConfig.getConnection();
            orderDAO = new OrderDAO(connection);

            // Récupération des commandes et ajout à la TableView
            List<Order> orderList = orderDAO.getAllOrders();
            orderTable.getItems().setAll(orderList);
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'erreur de connexion ou autre
        }
    }
}
