package com.noch.noch.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PlaceNewOrderController {

    @FXML private TextField customerNameField;
    @FXML private TextField customerEmailField;
    @FXML private Label     nameError;
    @FXML private Label     emailError;
    @FXML private VBox      lineItemsContainer;
    @FXML private Label     emptyItemsLabel;
    @FXML private Label     totalLabel;

    private final String[][] PRODUCTS = {
        {"PROD-001", "Classic White Tee",    "19.99"},
        {"PROD-002", "Slim Fit Jeans",       "49.99"},
        {"PROD-003", "Floral Summer Dress",  "54.99"},
        {"PROD-004", "Leather Jacket",       "129.99"},
        {"PROD-005", "Wool Overcoat",       "129.99"}
    };

    @FXML
    public void initialize() {
        emptyItemsLabel.setVisible(true);
        emptyItemsLabel.setManaged(true);
    }

    @FXML
    private void addLineItem() {
        emptyItemsLabel.setVisible(false);
        emptyItemsLabel.setManaged(false);

        HBox row = new HBox(12);
        row.setStyle("-fx-alignment: CENTER_LEFT;");

        ComboBox<String> productCombo = new ComboBox<>();
        productCombo.setPrefWidth(280);
        productCombo.setPromptText("Select product");
        for (String[] p : PRODUCTS) productCombo.getItems().add(p[1]);
        productCombo.setStyle("-fx-background-radius: 8; -fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-font-size: 13; -fx-font-family: Arial;");

        Label priceLabel = new Label("£0.00");
        priceLabel.setPrefWidth(100);
        priceLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b; -fx-font-family: Arial; -fx-alignment: CENTER_RIGHT;");

        Spinner<Integer> qtySpinner = new Spinner<>(1, 999, 1);
        qtySpinner.setPrefWidth(100);

        Label subtotalLabel = new Label("£0.00");
        subtotalLabel.setPrefWidth(100);
        subtotalLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-font-family: Arial; -fx-alignment: CENTER_RIGHT;");

        Button removeBtn = new Button("🗑");
        removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc2626; -fx-cursor: hand; -fx-font-size: 14;");
        removeBtn.setOnAction(e -> {
            lineItemsContainer.getChildren().remove(row);
            if (lineItemsContainer.getChildren().size() <= 1) {
                emptyItemsLabel.setVisible(true);
                emptyItemsLabel.setManaged(true);
            }
            recalcTotal();
        });

        productCombo.setOnAction(e -> {
            int idx = productCombo.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                double price = Double.parseDouble(PRODUCTS[idx][2]);
                priceLabel.setText("£" + String.format("%.2f", price));
                subtotalLabel.setText("£" + String.format("%.2f", price * qtySpinner.getValue()));
            }
            recalcTotal();
        });

        qtySpinner.valueProperty().addListener((obs, o, newVal) -> {
            int idx = productCombo.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                double price = Double.parseDouble(PRODUCTS[idx][2]);
                subtotalLabel.setText("£" + String.format("%.2f", price * newVal));
            }
            recalcTotal();
        });

        row.getChildren().addAll(productCombo, priceLabel, qtySpinner, subtotalLabel, removeBtn);
        lineItemsContainer.getChildren().add(row);
    }

    @FXML
    private void handleSubmit() {
        nameError.setText("");
        emailError.setText("");

        String name  = customerNameField.getText().trim();
        String email = customerEmailField.getText().trim();
        boolean hasError = false;

        if (name.isEmpty()) { nameError.setText("Customer name is required"); hasError = true; }
        if (email.isEmpty()) { emailError.setText("Customer email is required"); hasError = true; }
        if (hasError) return;

        int items = lineItemsContainer.getChildren().size() - 1;
        if (items <= 0 || emptyItemsLabel.isVisible()) {
            nameError.setText("Please add at least one product");
            return;
        }

        String orderId = "ORD-" + (int)(Math.random() * 9000 + 1000);
        System.out.println("Order placed: " + orderId);
        navigateTo("/fxml/CustomerOrderHistory.fxml");
    }

    @FXML
    private void handleCancel() {
        navigateTo("/fxml/ProductCatalogue.fxml");
    }

    private void recalcTotal() {
        double total = 0;
        for (int i = 1; i < lineItemsContainer.getChildren().size(); i++) {
            HBox row = (HBox) lineItemsContainer.getChildren().get(i);
            Label sub = (Label) row.getChildren().get(3);
            try { total += Double.parseDouble(sub.getText().replace("£", "")); } catch (Exception ignored) {}
        }
        totalLabel.setText("£" + String.format("%.2f", total));
    }

    private void navigateTo(String fxmlPath) {
        try {
            Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
            lineItemsContainer.getScene().setRoot(content);
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}