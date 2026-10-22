package com.noch.noch.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EditOrderController {

    @FXML private Label            orderIdLabel;
    @FXML private Label            customerNameLabel;
    @FXML private Label            customerEmailLabel;
    @FXML private ComboBox<String> statusCombo;
    @FXML private VBox             lineItemsContainer;
    @FXML private Label            totalLabel;

    private final String[][] PRODUCTS = {
        {"PROD-001","Classic White Tee",    "19.99"},
        {"PROD-002","Slim Fit Jeans",       "49.99"},
        {"PROD-003","Floral Summer Dress",  "54.99"},
        {"PROD-004","Leather Jacket",       "129.99"},
        {"PROD-005","Wool Overcoat",       "129.99"}
    };

    @FXML
    public void initialize() {
        orderIdLabel.setText("Order ID: ORD-001");
        customerNameLabel.setText("Alice Johnson");
        customerEmailLabel.setText("alice@demo.com");
        statusCombo.getItems().addAll("Processing","Shipped","Delivered","Pending","Returned","Cancelled");
        statusCombo.setValue("Processing");
        addPreloadedItem("Classic White Tee","19.99","2");
        addPreloadedItem("Slim Fit Jeans","49.99","1");
        recalcTotal();
    }

    private void addPreloadedItem(String productName, String price, String qty) {
        HBox row = buildRow();
        ComboBox<?> combo = (ComboBox<?>) row.getChildren().get(0);
        for (int i = 0; i < PRODUCTS.length; i++) {
            if (PRODUCTS[i][1].equals(productName)) {
                combo.getSelectionModel().select(i);
                Label priceLabel = (Label) row.getChildren().get(1);
                priceLabel.setText("£" + price);
                break;
            }
        }
        lineItemsContainer.getChildren().add(row);
    }

    @FXML private void addLineItem() { lineItemsContainer.getChildren().add(buildRow()); }

    private HBox buildRow() {
        HBox row = new HBox(12);
        row.setStyle("-fx-alignment:CENTER_LEFT;");

        ComboBox<String> productCombo = new ComboBox<>();
        productCombo.setPrefWidth(280);
        productCombo.setPromptText("Select product");
        for (String[] p : PRODUCTS) productCombo.getItems().add(p[1]);
        productCombo.setStyle("-fx-background-radius:8;-fx-border-color:#d1d5db;-fx-border-radius:8;-fx-font-size:13;");

        Label priceLabel = new Label("£0.00");
        priceLabel.setPrefWidth(100);
        priceLabel.setStyle("-fx-font-size:13;-fx-text-fill:#64748b;-fx-alignment:CENTER_RIGHT;");

        Spinner<Integer> qtySpinner = new Spinner<>(1,999,1);
        qtySpinner.setPrefWidth(100);

        Label subtotalLabel = new Label("£0.00");
        subtotalLabel.setPrefWidth(100);
        subtotalLabel.setStyle("-fx-font-size:13;-fx-font-weight:bold;-fx-text-fill:#0f172a;-fx-alignment:CENTER_RIGHT;");

        Button removeBtn = new Button("🗑");
        removeBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:#dc2626;-fx-cursor:hand;-fx-font-size:14;");
        removeBtn.setOnAction(e -> { lineItemsContainer.getChildren().remove(row); recalcTotal(); });

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
        return row;
    }

    @FXML private void handleSave() {
        if (lineItemsContainer.getChildren().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please add at least one product").showAndWait();
            return;
        }
        loadIntoContentArea("/fxml/AdminOrdersDashboard.fxml");
    }

    @FXML private void goBack() {
        loadIntoContentArea("/fxml/AdminOrdersDashboard.fxml");
    }

    private void recalcTotal() {
        double total = 0;
        for (javafx.scene.Node node : lineItemsContainer.getChildren()) {
            if (node instanceof HBox row) {
                Label sub = (Label) row.getChildren().get(3);
                try { total += Double.parseDouble(sub.getText().replace("£","")); } catch (Exception ignored) {}
            }
        }
        totalLabel.setText("£" + String.format("%.2f", total));
    }

    private void loadIntoContentArea(String fxmlPath) {
        try {
            StackPane contentArea = (StackPane) orderIdLabel
                .getScene().getRoot().lookup("#contentArea");
            if (contentArea != null) {
                Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
                contentArea.getChildren().setAll(content);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}