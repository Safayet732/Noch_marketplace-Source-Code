package com.noch.noch.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AdminPlaceOrderController {

    @FXML private ComboBox<String> customerCombo;
    @FXML private Label            customerError;
    @FXML private VBox             lineItemsContainer;
    @FXML private Label            emptyLabel;
    @FXML private Label            totalLabel;
    @FXML private Label            stockErrorLabel;

    private final String[] CUSTOMERS = {
        "Alice Johnson (CUST-001)",
        "Bob Smith (CUST-002)",
        "Carol White (CUST-003)"
    };

    private final String[][] PRODUCTS = {
        {"PROD-001","Classic White Tee",       "19.99","120"},
        {"PROD-002","Slim Fit Jeans",           "49.99","85"},
        {"PROD-003","Floral Summer Dress",      "54.99","40"},
        {"PROD-004","Leather Jacket",           "129.99","20"},
        {"PROD-005","Wool Overcoat",            "129.99","15"}
    };

    @FXML
    public void initialize() {
        customerCombo.getItems().addAll(CUSTOMERS);
        stockErrorLabel.setVisible(false);
        stockErrorLabel.setManaged(false);
    }

    @FXML private void addLineItem() {
        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);

        HBox row = new HBox(12);
        row.setStyle("-fx-alignment:CENTER_LEFT;");

        ComboBox<String> productCombo = new ComboBox<>();
        productCombo.setPrefWidth(280);
        productCombo.setPromptText("Select product");
        for (String[] p : PRODUCTS) productCombo.getItems().add(p[1] + " (Stock: " + p[3] + ")");
        productCombo.setStyle("-fx-background-radius:8;-fx-border-color:#d1d5db;-fx-border-radius:8;-fx-font-size:13;-fx-font-family:Arial;");

        Label priceLabel = new Label("£0.00");
        priceLabel.setPrefWidth(100);
        priceLabel.setStyle("-fx-font-size:13;-fx-text-fill:#64748b;-fx-font-family:Arial;-fx-alignment:CENTER_RIGHT;");

        Spinner<Integer> qtySpinner = new Spinner<>(1,999,1);
        qtySpinner.setPrefWidth(100);
        qtySpinner.setStyle("-fx-font-size:13;-fx-font-family:Arial;");

        Label subtotalLabel = new Label("£0.00");
        subtotalLabel.setPrefWidth(100);
        subtotalLabel.setStyle("-fx-font-size:13;-fx-font-weight:bold;-fx-text-fill:#0f172a;-fx-font-family:Arial;-fx-alignment:CENTER_RIGHT;");

        Button removeBtn = new Button("🗑");
        removeBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:#dc2626;-fx-cursor:hand;-fx-font-size:14;");
        removeBtn.setOnAction(e -> {
            lineItemsContainer.getChildren().remove(row);
            if (lineItemsContainer.getChildren().size() <= 1) {
                emptyLabel.setVisible(true);
                emptyLabel.setManaged(true);
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

        qtySpinner.valueProperty().addListener((obs,o,nv) -> {
            int idx = productCombo.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                double price = Double.parseDouble(PRODUCTS[idx][2]);
                subtotalLabel.setText("£" + String.format("%.2f", price * nv));
            }
            recalcTotal();
        });

        row.getChildren().addAll(productCombo, priceLabel, qtySpinner, subtotalLabel, removeBtn);
        lineItemsContainer.getChildren().add(row);
    }

    @FXML private void handleSubmit() {
        stockErrorLabel.setVisible(false);
        stockErrorLabel.setManaged(false);
        customerError.setText("");

        if (customerCombo.getValue() == null) {
            customerError.setText("Please select a customer");
            return;
        }

        int itemCount = lineItemsContainer.getChildren().size() - 1;
        if (itemCount <= 0 || emptyLabel.isVisible()) {
            stockErrorLabel.setText("Please add at least one product");
            stockErrorLabel.setVisible(true);
            stockErrorLabel.setManaged(true);
            return;
        }

        StringBuilder errors = new StringBuilder();
        for (int i = 1; i < lineItemsContainer.getChildren().size(); i++) {
            HBox row = (HBox) lineItemsContainer.getChildren().get(i);
            ComboBox<?> combo   = (ComboBox<?>) row.getChildren().get(0);
            Spinner<?>  spinner = (Spinner<?>)  row.getChildren().get(2);
            int idx = combo.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                int stock = Integer.parseInt(PRODUCTS[idx][3]);
                int qty   = (Integer) spinner.getValue();
                if (qty > stock) errors.append("Insufficient stock for ").append(PRODUCTS[idx][1]).append("\n");
            }
        }

        if (errors.length() > 0) {
            stockErrorLabel.setText("⚠ " + errors);
            stockErrorLabel.setVisible(true);
            stockErrorLabel.setManaged(true);
            return;
        }

        loadIntoContentArea("/fxml/AdminOrdersDashboard.fxml");
    }

    @FXML private void handleCancel() {
        loadIntoContentArea("/fxml/AdminOrdersDashboard.fxml");
    }

    private void recalcTotal() {
        double total = 0;
        for (int i = 1; i < lineItemsContainer.getChildren().size(); i++) {
            HBox row = (HBox) lineItemsContainer.getChildren().get(i);
            Label sub = (Label) row.getChildren().get(3);
            try { total += Double.parseDouble(sub.getText().replace("£","")); } catch (Exception ignored) {}
        }
        totalLabel.setText("£" + String.format("%.2f", total));
    }

    private void loadIntoContentArea(String fxmlPath) {
        try {
            StackPane contentArea = (StackPane) lineItemsContainer
                .getScene().getRoot().lookup("#contentArea");
            if (contentArea != null) {
                Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
                contentArea.getChildren().setAll(content);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}