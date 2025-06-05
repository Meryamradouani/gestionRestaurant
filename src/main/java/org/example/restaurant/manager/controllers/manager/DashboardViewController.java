package org.example.restaurant.manager.controllers.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import org.example.restaurant.manager.models.Reservation;
import org.example.restaurant.manager.models.TopDish;
import org.example.restaurant.manager.services.DashboardService;

import java.util.List;
import java.util.Map;

public class DashboardViewController {

    @FXML private Label lblReservations, lblCommandes, lblRevenue;
    @FXML private Label lblReservationsToday, lblCommandesToday, lblRevenueMonth;
    @FXML private BarChart<String, Number> ordersChart;
    @FXML private PieChart reviewsChart;
    @FXML private TableView<Reservation> recentReservationsTable;
    @FXML private TableColumn<Reservation, String> colClient, colDate, colTime, colStatus;
    @FXML private TableColumn<Reservation, Integer> colPersons;
    @FXML private TableView<TopDish> topDishesTable;
    @FXML private TableColumn<TopDish, String> colDishName;
    @FXML private TableColumn<TopDish, Integer> colDishOrders;
    @FXML private Button refreshBtn;

    private final DashboardService dashboardService = new DashboardService();

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
        setupCharts();
        setupRefreshButton();
    }

    private void setupColumns() {
        // Colonnes pour les réservations
        colClient.setCellValueFactory(data -> data.getValue().userNameProperty());
        colDate.setCellValueFactory(data -> data.getValue().dateProperty().asString());
        colTime.setCellValueFactory(data -> data.getValue().timeProperty());
        colPersons.setCellValueFactory(data -> data.getValue().guestsProperty().asObject());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());

        // Colonnes pour les plats populaires
        colDishName.setCellValueFactory(data -> data.getValue().nameProperty());
        colDishOrders.setCellValueFactory(data -> data.getValue().ordersProperty().asObject());
    }

    private void loadData() {
        // Chargement des KPI
        lblReservations.setText(String.valueOf(dashboardService.getTotalReservations()));
        lblCommandes.setText(String.valueOf(dashboardService.getTotalCommandes()));
        lblRevenue.setText(String.format("%,.2f dh", dashboardService.getTotalRevenus()));

        lblReservationsToday.setText(String.valueOf(dashboardService.getTodayReservations()));
        lblCommandesToday.setText(String.valueOf(dashboardService.getTodayCommandes()));
        lblRevenueMonth.setText(String.format("%,.2f dh", dashboardService.getMonthlyRevenus()));

        // Chargement des réservations récentes
        List<Reservation> recentReservations = dashboardService.getRecentReservations(5);
        ObservableList<Reservation> reservations = FXCollections.observableArrayList(recentReservations);
        recentReservationsTable.setItems(reservations);

        // Chargement des plats populaires
        List<TopDish> topDishes = dashboardService.getTopDishes(5);
        ObservableList<TopDish> dishes = FXCollections.observableArrayList(topDishes);
        topDishesTable.setItems(dishes);
    }

    private void setupCharts() {
        // Graphique des commandes par mois
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Commandes");
        Map<String, Integer> monthlyOrders = dashboardService.getOrdersByMonth(6);

        monthlyOrders.forEach((month, count) -> {
            series.getData().add(new XYChart.Data<>(month, count));
        });

        ordersChart.getData().clear();
        ordersChart.getData().add(series);
        ordersChart.setCategoryGap(20);
        ordersChart.setLegendVisible(false);

        // Graphique des avis clients
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        Map<Integer, Long> reviewsDistribution = dashboardService.getReviewsDistribution();

        reviewsDistribution.forEach((rating, count) -> {
            pieData.add(new PieChart.Data(
                    rating + " étoiles (" + count + ")",
                    count
            ));
        });

        reviewsChart.setData(pieData);
        reviewsChart.setLabelsVisible(true);
    }

    private void setupRefreshButton() {
        refreshBtn.setOnAction(event -> {
            loadData();
            setupCharts();
        });
    }
}