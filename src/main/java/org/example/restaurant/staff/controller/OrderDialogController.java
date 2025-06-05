package org.example.restaurant.staff.controller;

import org.example.restaurant.staff.dao.MenuItemDAO;
import org.example.restaurant.staff.dao.OrderDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.beans.property.SimpleStringProperty;
import org.example.restaurant.staff.model.MenuItem;
import org.example.restaurant.staff.model.Order;
import org.example.restaurant.staff.model.OrderItem;
import org.example.restaurant.staff.model.Reservation;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;

public class OrderDialogController {
    @FXML private Label dialogTitle;
    @FXML private Label clientLabel;
    @FXML private Label timeLabel;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TableView<MenuItem> menuItemsTable;
    @FXML private TableColumn<MenuItem, String> nameColumn;
    @FXML private TableColumn<MenuItem, String> priceColumn;
    @FXML private TableColumn<MenuItem, String> categoryColumn;
    @FXML private TableView<OrderItem> orderItemsTable;
    @FXML private TableColumn<OrderItem, String> orderItemNameColumn;
    @FXML private TableColumn<OrderItem, Integer> orderItemQuantityColumn;
    @FXML private TableColumn<OrderItem, String> orderItemPriceColumn;
    @FXML private TableColumn<OrderItem, String> orderItemTotalColumn;
    @FXML private Label totalLabel;
    @FXML private DialogPane dialogPane;
    @FXML private Button addButton;
    @FXML private Button removeButton;
    @FXML private Button saveButton;

    private final MenuItemDAO menuItemDAO = new MenuItemDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private Order currentOrder;
    private Reservation currentReservation;
    private int staffId;
    private final ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private List<OrderItem> orderItemsList = new ArrayList<>();

    @FXML
    public void initialize() {
        setupTables();
        loadCategories();
        updateTotal();

        // Désactiver le bouton Enregistrer initialement
        if (saveButton != null) {
            saveButton.setDisable(true);
        }
    }

    private void setupTables() {
        // Configuration de la table des éléments du menu
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPrice().toString() + " €"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Configuration du Spinner pour la quantité dans le menu
        TableColumn<MenuItem, Integer> quantityColumn = new TableColumn<>("Quantité");
        quantityColumn.setPrefWidth(100);
        quantityColumn.setCellFactory(col -> new TableCell<>() {
            private final Spinner<Integer> spinner = new Spinner<>(1, 99, 1);

            {
                spinner.setEditable(true);
                spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    MenuItem item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        item.setQuantity(newVal);
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    MenuItem menuItem = getTableView().getItems().get(getIndex());
                    spinner.getValueFactory().setValue(menuItem.getQuantity());
                    setGraphic(spinner);
                }
            }
        });
        menuItemsTable.getColumns().add(quantityColumn);

        // Configuration de la table des éléments de la commande
        orderItemNameColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemName"));
        orderItemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        orderItemPriceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPrice().toString() + " €"));
        orderItemTotalColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSubtotal().toString() + " €"));

        orderItemsTable.setItems(orderItems);
    }

    private void loadCategories() {
        try {
            categoryComboBox.setItems(FXCollections.observableArrayList(menuItemDAO.getCategories()));
            categoryComboBox.getSelectionModel().selectFirst();
            categoryComboBox.setOnAction(e -> loadMenuItems());
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les catégories");
        }
    }

    private void loadMenuItems() {
        try {
            String category = categoryComboBox.getValue();
            if (category != null) {
                List<MenuItem> items = menuItemDAO.getMenuItemsByCategory(category);
                menuItemsTable.setItems(FXCollections.observableArrayList(items));
            }
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les éléments du menu");
        }
    }

    @FXML
    private void handleAddItem() {
        MenuItem selectedItem = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showError("Erreur", "Veuillez sélectionner un élément du menu");
            return;
        }

        // Vérification de la réservation
        if (currentReservation == null) {
            showError("Erreur", "Aucune réservation sélectionnée");
            return;
        }

        // Vérification de l'ID du staff
        if (staffId <= 0) {
            showError("Erreur", "ID du staff invalide");
            return;
        }

        // Si currentOrder est null, on crée une nouvelle commande
        if (currentOrder == null) {
            try {
                System.out.println("Création d'une nouvelle commande avec :");
                System.out.println("Staff ID: " + staffId);
                System.out.println("Reservation ID: " + currentReservation.getId());

                int orderId = orderDAO.createOrder(staffId, currentReservation.getId());
                currentOrder = new Order(orderId, staffId, currentReservation.getId(),
                        currentReservation.getUserName(), null, BigDecimal.ZERO, "EN_ATTENTE");

                System.out.println("Commande créée avec l'ID: " + orderId);
            } catch (SQLException e) {
                e.printStackTrace(); // Pour voir l'erreur complète dans la console
                showError("Erreur", "Impossible de créer la commande: " + e.getMessage());
                return;
            }
        }

        // Vérifier si l'article existe déjà dans la commande
        for (OrderItem existingItem : orderItems) {
            if (existingItem.getMenuItemId() == selectedItem.getId()) {
                // Mettre à jour la quantité de l'article existant
                existingItem.setQuantity(existingItem.getQuantity() + selectedItem.getQuantity());
                try {
                    orderDAO.updateOrderItemQuantity(existingItem.getId(), existingItem.getQuantity());
                    orderItemsTable.refresh();
                    updateTotal();
                    return;
                } catch (SQLException e) {
                    e.printStackTrace();
                    showError("Erreur", "Impossible de mettre à jour la quantité: " + e.getMessage());
                    return;
                }
            }
        }

        // Ajouter un nouvel article
        OrderItem orderItem = new OrderItem(
                0, // ID temporaire
                currentOrder.getId(),
                selectedItem.getId(),
                selectedItem.getQuantity(),
                selectedItem.getPrice()
        );
        orderItem.setMenuItemName(selectedItem.getName());

        try {
            orderDAO.addOrderItem(orderItem);
            orderItems.add(orderItem);
            updateTotal();
            // Réinitialiser la quantité du Spinner
            selectedItem.setQuantity(1);
            menuItemsTable.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ajouter l'élément à la commande: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoveItem() {
        OrderItem selectedItem = orderItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showError("Erreur", "Veuillez sélectionner un élément à supprimer");
            return;
        }

        try {
            // Si l'élément a un ID valide, le supprimer de la base de données
            if (selectedItem.getId() > 0) {
                orderDAO.removeOrderItem(selectedItem.getId());
            }

            // Supprimer l'élément de la liste et mettre à jour l'affichage
            orderItems.remove(selectedItem);
            orderItemsTable.refresh();
            updateTotal();

            // Si c'était le dernier élément, mettre à jour le statut de la commande
            if (orderItems.isEmpty() && currentOrder != null) {
                try {
                    orderDAO.updateOrderStatus(currentOrder.getId(), "ANNULEE");
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la mise à jour du statut: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            showError("Erreur", "Impossible de supprimer l'élément: " + e.getMessage());
        }
    }

    private void redirectToStaffDashboard() {
        // Fermer simplement la fenêtre actuelle
        Stage currentStage = (Stage) saveButton.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void handleCancel() {
        // Fermer simplement la fenêtre actuelle
        Stage currentStage = (Stage) saveButton.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void handleSave() {
        // Vérification si la commande est vide ou si le total est 0
        if (orderItems == null || orderItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'enregistrer une commande vide. Veuillez ajouter au moins un article.");
            alert.showAndWait();
            return;
        }

        // Calculer le total
        BigDecimal total = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Vérifier si le total est supérieur à 0
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Le total de la commande doit être supérieur à 0.");
            alert.showAndWait();
            return;
        }

        try {
            System.out.println("Saving order..."); // Debug log

            // Sauvegarder la commande
            int orderId = orderDAO.createOrder(staffId, currentReservation.getId());
            System.out.println("Order created with ID: " + orderId); // Debug log

            // Sauvegarder les articles
            for (OrderItem item : orderItems) {
                System.out.println("Adding item: " + item.getMenuItemName() + " x" + item.getQuantity()); // Debug log
                item.setOrderId(orderId);
                orderDAO.addOrderItem(item);
            }

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("La commande a été enregistrée avec succès.");
            alert.showAndWait();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) saveButton.getScene().getWindow();
            currentStage.close();
        } catch (SQLException e) {
            System.err.println("Error saving order: " + e.getMessage()); // Debug log
            e.printStackTrace();
            showError("Erreur", "Impossible d'enregistrer la commande: " + e.getMessage());
        }
    }

    public void setReservation(Reservation reservation) {
        this.currentReservation = reservation;
        dialogTitle.setText("Nouvelle commande - " + reservation.getUserName());
        updateReservationInfo();
        try {
            int orderId = orderDAO.createOrder(staffId, reservation.getId());
            currentOrder = new Order(orderId, staffId, reservation.getId(), reservation.getUserName(), null, BigDecimal.ZERO, "EN_ATTENTE");
        } catch (SQLException e) {
            showError("Erreur", "Impossible de créer la commande");
        }
    }

    public void setOrder(Order order) {
        System.out.println("Setting order details for order ID: " + order.getId()); // Debug log
        this.currentOrder = order;
        dialogTitle.setText("Détails de la commande #" + order.getId());

        // Charger les articles de la commande
        try {
            List<OrderItem> items = orderDAO.getOrderItems(order.getId());
            System.out.println("Found " + items.size() + " items for order"); // Debug log
            orderItems.setAll(items);
            updateTotal();

            // Désactiver les contrôles en mode visualisation
            categoryComboBox.setDisable(true);
            menuItemsTable.setDisable(true);
            orderItemsTable.setDisable(true);

            // Désactiver le bouton de sauvegarde
            if (dialogPane != null) {
                Button saveButton = (Button) dialogPane.lookupButton(ButtonType.OK);
                if (saveButton != null) {
                    saveButton.setDisable(true);
                }
            }

            System.out.println("Order details loaded successfully"); // Debug log
        } catch (SQLException e) {
            System.err.println("Error loading order items: " + e.getMessage()); // Debug log
            e.printStackTrace();
            showError("Erreur", "Impossible de charger les détails de la commande: " + e.getMessage());
        }
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    private void updateReservationInfo() {
        if (currentReservation != null) {
            clientLabel.setText("Client: " + currentReservation.getUserName());
            timeLabel.setText("Heure: " + currentReservation.getTime().toLocalDateTime().format(timeFormatter));
        }
    }

    private void updateTotal() {
        BigDecimal total = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalLabel.setText(total.toString() + " €");

        // Activer/désactiver le bouton Enregistrer en fonction du total
        if (saveButton != null) {
            saveButton.setDisable(total.compareTo(BigDecimal.ZERO) <= 0);
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 