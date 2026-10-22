package com.noch.noch.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderBuilderController {

    @FXML private VBox   cartItemsContainer;
    @FXML private Label  emptyCartLabel;
    @FXML private Label  totalLabel;
    @FXML private VBox   stockErrorPanel;
    @FXML private Label  stockErrorDetails;
    @FXML private Button confirmBtn;

    @FXML
    public void initialize() {
        stockErrorPanel.setVisible(false);
        stockErrorPanel.setManaged(false);
        renderCart();
    }

    private void renderCart() {
        cartItemsContainer.getChildren().clear();

        if (Session.cart.isEmpty()) {
            emptyCartLabel.setVisible(true);
            emptyCartLabel.setManaged(true);
            confirmBtn.setDisable(true);
            totalLabel.setText("£0.00");
            return;
        }

        emptyCartLabel.setVisible(false);
        emptyCartLabel.setManaged(false);
        confirmBtn.setDisable(false);

        for (String[] entry : Session.cart.values())
            cartItemsContainer.getChildren().add(buildCartRow(entry));

        recalcTotal();
    }

    private HBox buildCartRow(String[] entry) {
        // entry: [0=id, 1=name, 2=category, 3=price, 4=stock, 5=qty]
        HBox row = new HBox(16);
        row.setStyle(
            "-fx-alignment:CENTER_LEFT;" +
            "-fx-padding:16 0;" +
            "-fx-border-color:#f0f0f0;" +
            "-fx-border-width:0 0 1 0;"
        );

        VBox productInfo = new VBox(4);
        productInfo.setPrefWidth(260);
        Label nameLabel = new Label(entry[1]);
        nameLabel.setStyle("-fx-font-size:14;-fx-font-weight:bold;-fx-text-fill:#000000;-fx-font-family:Arial;");
        Label catLabel = new Label(entry[2].toUpperCase());
        catLabel.setStyle("-fx-font-size:10;-fx-text-fill:#888888;-fx-font-family:Arial;-fx-letter-spacing:1;");
        Label stockLabel = new Label("Stock available: " + entry[4]);
        stockLabel.setStyle("-fx-font-size:11;-fx-text-fill:#888888;-fx-font-family:Arial;");
        productInfo.getChildren().addAll(nameLabel, catLabel, stockLabel);

        Label priceLabel = new Label("£" + entry[3]);
        priceLabel.setPrefWidth(90);
        priceLabel.setStyle("-fx-font-size:14;-fx-text-fill:#666666;-fx-font-family:Georgia;-fx-alignment:CENTER_RIGHT;");

        HBox qtyBox = new HBox(6);
        qtyBox.setPrefWidth(150);
        qtyBox.setStyle("-fx-alignment:CENTER;");
        Button minusBtn = new Button("−");
        minusBtn.setStyle("-fx-background-color:white;-fx-border-color:#000000;-fx-border-width:1;-fx-border-radius:0;-fx-background-radius:0;-fx-cursor:hand;-fx-padding:5 12;-fx-font-size:14;");
        TextField qtyField = new TextField(entry[5]);
        qtyField.setPrefWidth(44);
        qtyField.setStyle("-fx-background-radius:0;-fx-border-color:#e0e0e0;-fx-border-width:1;-fx-font-size:13;-fx-font-family:Arial;-fx-alignment:CENTER;");
        Button plusBtn = new Button("+");
        plusBtn.setStyle("-fx-background-color:#000000;-fx-text-fill:white;-fx-border-radius:0;-fx-background-radius:0;-fx-cursor:hand;-fx-padding:5 12;-fx-font-size:14;");
        qtyBox.getChildren().addAll(minusBtn, qtyField, plusBtn);

        double sub = Double.parseDouble(entry[3]) * Integer.parseInt(entry[5]);
        Label subtotalLabel = new Label("£" + String.format("%.2f", sub));
        subtotalLabel.setPrefWidth(90);
        subtotalLabel.setStyle("-fx-font-size:16;-fx-font-weight:bold;-fx-text-fill:#000000;-fx-font-family:Georgia;-fx-alignment:CENTER_RIGHT;");

        Button removeBtn = new Button("✕");
        removeBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:#aaaaaa;-fx-cursor:hand;-fx-font-size:16;-fx-border-width:0;");
        removeBtn.setOnAction(e -> { Session.cart.remove(entry[0]); renderCart(); });

        minusBtn.setOnAction(e -> {
            int qty = Math.max(1, Integer.parseInt(entry[5]) - 1);
            entry[5] = String.valueOf(qty);
            qtyField.setText(entry[5]);
            subtotalLabel.setText("£" + String.format("%.2f", Double.parseDouble(entry[3]) * qty));
            recalcTotal();
        });
        plusBtn.setOnAction(e -> {
            int qty = Integer.parseInt(entry[5]) + 1;
            entry[5] = String.valueOf(qty);
            qtyField.setText(entry[5]);
            subtotalLabel.setText("£" + String.format("%.2f", Double.parseDouble(entry[3]) * qty));
            recalcTotal();
        });

        row.getChildren().addAll(productInfo, priceLabel, qtyBox, subtotalLabel, removeBtn);
        return row;
    }

    private void recalcTotal() {
        double total = Session.cart.values().stream()
            .mapToDouble(e -> Double.parseDouble(e[3]) * Integer.parseInt(e[5]))
            .sum();
        totalLabel.setText("£" + String.format("%.2f", total));
    }

    @FXML private void handleConfirmOrder() {
        // Check stock
        StringBuilder errors = new StringBuilder();
        for (String[] entry : Session.cart.values()) {
            int qty   = Integer.parseInt(entry[5]);
            int stock = Integer.parseInt(entry[4]);
            if (qty > stock)
                errors.append(entry[1]).append(": requested ").append(qty)
                      .append(", only ").append(stock).append(" available\n");
        }
        if (errors.length() > 0) {
            stockErrorDetails.setText(errors.toString());
            stockErrorPanel.setVisible(true);
            stockErrorPanel.setManaged(true);
            return;
        }
        saveOrderToSession();
        Session.snapshotCart();
        Session.clearCart();
        navigateTo("/fxml/OrderConfirmation.fxml");
    }

    @FXML private void handleRejectOrder() {
        stockErrorPanel.setVisible(false);
        stockErrorPanel.setManaged(false);
    }

    @FXML private void handleMarkPending() {
        stockErrorPanel.setVisible(false);
        stockErrorPanel.setManaged(false);
        saveOrderToSession();
        Session.snapshotCart();
        Session.clearCart();
        navigateTo("/fxml/OrderConfirmation.fxml");
    }

    @FXML private void continueShopping() {
        navigateTo("/fxml/ProductCatalogue.fxml");
    }

    // ── Build a new order row, save to DB and Session ────────────────────────
    private void saveOrderToSession() {
        Session.initOrders();

        // Generate order ID from DB
        String orderId = OrderDAO.getInstance().generateOrderId();

        // Build items summary
        int itemCount = Session.cart.values().stream()
            .mapToInt(e -> Integer.parseInt(e[5])).sum();
        String firstItem = Session.cart.values().iterator().next()[1];
        String itemsSummary = itemCount + " item" + (itemCount != 1 ? "s" : "");
        String itemsDetail  = itemCount > 1 ? firstItem + " +" + (Session.cart.size() - 1) + " more"
                                             : firstItem;

        // Calculate total
        double total = Session.cart.values().stream()
            .mapToDouble(e -> Double.parseDouble(e[3]) * Integer.parseInt(e[5]))
            .sum();

        // Date
        String date = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("MMM d, yyyy"));

        // New order row — same format as Session.orders
        // [orderId, customerName, customerId, date, itemCount, firstItem, total, status]
        String[] newOrder = {
            orderId,
            "Alice Johnson",
            "CUST-001",
            date,
            itemsSummary,
            itemsDetail,
            "£" + String.format("%.2f", total),
            "Processing"
        };

        // Save to database
        String customerId = (Session.loggedInUser != null && Session.loggedInUser[4] != null)
            ? Session.loggedInUser[4] : "CUST-001";
        String customerName = (Session.loggedInUser != null)
            ? Session.loggedInUser[1] : "Alice Johnson";

        OrderDAO.getInstance().saveOrder(
            orderId, customerId, customerName, date, total, Session.cart
        );

        // Update stock in DB for each item
        for (String[] entry : Session.cart.values()) {
            ProductDAO.getInstance().reduceStock(entry[0], Integer.parseInt(entry[5]));
        }

        // Add to top of Session orders so it shows immediately
        Session.orders.add(0, newOrder);

        // Store as currentOrder so confirmation page can display it
        Session.currentOrder = newOrder;
    }

    private void navigateTo(String fxmlPath) {
        try {
            StackPane contentArea = (StackPane) cartItemsContainer
                .getScene().getRoot().lookup("#contentArea");
            if (contentArea != null) {
                Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
                contentArea.getChildren().setAll(content);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}