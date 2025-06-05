package org.example.restaurant.manager.controllers.manager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

public class DashboardManagerController {

    @FXML private StackPane contentPane;
    @FXML private TextField searchField;
    @FXML private BarChart<String, Number> salesChart;
    @FXML private PieChart categoriesChart;
    @FXML private LineChart<String, Number> trendChart;



    @FXML
    public void initialize() {
        loadDefaultView();
        setupSearchField();
        setupCharts();

    }

    private void setupSearchField() {
        searchField.setOnAction(event -> handleSearch());
    }

    private void setupCharts() {
        if (salesChart != null) {
            salesChart.setTitle("Ventes quotidiennes");
            salesChart.setLegendVisible(false);
        }

        if (categoriesChart != null) {
            categoriesChart.setTitle("Répartition par catégorie");
        }

        if (trendChart != null) {
            trendChart.setTitle("Tendance des ventes sur 30 jours");
            trendChart.setCreateSymbols(true);
        }
    }


    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        System.out.println("Recherche en cours: " + query);
    }

    @FXML
    private void showDashboard() {
        loadViewSafe("/org/example/restaurant/manager/view/manager/DashboardView.fxml");
    }

    @FXML
    private void showStaff() {
        loadViewSafe("/org/example/restaurant/manager/view/manager/StaffView.fxml");
    }

    @FXML
    private void showOrders() {
        loadViewSafe("/org/example/restaurant/manager/view/manager/OrdersView.fxml");
    }

    @FXML
    private void showMenu() {
        loadViewSafe("/org/example/restaurant/manager/view/manager/MenuView.fxml");
    }

    @FXML
    private void showCustomers() {
        loadViewSafe("/org/example/restaurant/manager/view/manager/CustomersView.fxml");
    }

    @FXML
    private void showAnalytics() {
        loadViewSafe("/org/example/restaurant/manager/view/manager/AnalyticsView.fxml");
    }

    private void loadDefaultView() {
        showDashboard();
    }

    private void loadViewSafe(String fxmlPath) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("Fichier FXML introuvable: " + fxmlPath);
            }
            Parent view = FXMLLoader.load(fxmlUrl);
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger la vue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

}
