package com.noch.noch.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.List;

public class CustomerOrderDetailController {

    @FXML private Label                         orderIdLabel;
    @FXML private Label                         statusBadge;
    @FXML private Label                         customerNameLabel;
    @FXML private Label                         customerEmailLabel;
    @FXML private Label                         orderDateLabel;
    @FXML private Label                         totalLabel;
    @FXML private TableView<String[]>           itemsTable;
    @FXML private TableColumn<String[], String> colProduct;
    @FXML private TableColumn<String[], String> colUnitPrice;
    @FXML private TableColumn<String[], String> colQty;
    @FXML private TableColumn<String[], String> colSubtotal;
    @FXML private HBox                          statusLifecycle;
    @FXML private HBox                          returnBtnBox;

    private String currentStatus = "Processing";

    @FXML
    public void initialize() {
        if (Session.currentOrder != null) {
            String[] row = Session.currentOrder;
            currentStatus = row[7];
            orderIdLabel.setText("Order ID: " + row[0]);
            customerNameLabel.setText(row[1]);
            customerEmailLabel.setText(row[1].toLowerCase().replace(" ","") + "@noch.com");
            orderDateLabel.setText(row[3]);
            totalLabel.setText(row[6]);
        } else {
            orderIdLabel.setText("Order ID: —");
            customerNameLabel.setText("—");
            customerEmailLabel.setText("—");
            orderDateLabel.setText("—");
            totalLabel.setText("£0.00");
        }

        statusBadge.setText(currentStatus.toUpperCase());
        statusBadge.setStyle(getStatusStyle(currentStatus));

        // ── Load items from DATABASE ──────────────────────────
        colProduct.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[0]));
        colUnitPrice.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        colQty.setCellValueFactory(d       -> new SimpleStringProperty(d.getValue()[2]));
        colSubtotal.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[3]));

        if (Session.currentOrder != null) {
            List<String[]> items = OrderDAO.getInstance()
                .getOrderItems(Session.currentOrder[0]);
            itemsTable.setItems(FXCollections.observableArrayList(items));
        }

        buildLifecycle();

        // Show return button only if Delivered
        if (returnBtnBox != null) {
            returnBtnBox.setVisible(currentStatus.equals("Delivered"));
            returnBtnBox.setManaged(currentStatus.equals("Delivered"));
        }
    }

    @FXML private void goBack() {
        navigateToContentArea("/fxml/CustomerOrderHistory.fxml");
    }

    @FXML private void requestReturn() {
        navigateToContentArea("/fxml/ReturnRequest.fxml");
    }

    private void buildLifecycle() {
        if (statusLifecycle == null) return;
        statusLifecycle.getChildren().clear();
        String[] steps = {"Processing","Shipped","Delivered"};
        java.util.List<String> order = java.util.List.of(steps);
        int currentIdx = order.indexOf(currentStatus);

        for (int i = 0; i < steps.length; i++) {
            boolean active = i <= currentIdx;
            VBox node = new VBox(6);
            node.setStyle("-fx-alignment:CENTER;-fx-min-width:100;");

            StackPane circle = new StackPane();
            circle.setStyle(
                "-fx-min-width:28;-fx-min-height:28;-fx-max-width:28;-fx-max-height:28;" +
                "-fx-background-radius:50;-fx-background-color:" +
                (active ? "#000000" : "#e0e0e0") + ";"
            );
            Label dot = new Label(active ? "✓" : "");
            dot.setStyle("-fx-text-fill:white;-fx-font-size:12;-fx-font-weight:bold;");
            circle.getChildren().add(dot);

            Label lbl = new Label(steps[i].toUpperCase());
            lbl.setStyle(
                "-fx-font-size:10;-fx-font-family:Arial;-fx-letter-spacing:1;" +
                "-fx-text-fill:" + (active ? "#000000" : "#aaaaaa") + ";" +
                "-fx-font-weight:" + (active ? "bold" : "normal") + ";"
            );
            node.getChildren().addAll(circle, lbl);
            statusLifecycle.getChildren().add(node);

            if (i < steps.length - 1) {
                Label line = new Label("─────");
                line.setStyle("-fx-text-fill:#e0e0e0;-fx-padding:0 4;");
                statusLifecycle.getChildren().add(line);
            }
        }
    }

    private void navigateToContentArea(String fxmlPath) {
        try {
            StackPane contentArea = (StackPane) orderIdLabel
                .getScene().getRoot().lookup("#contentArea");
            if (contentArea != null) {
                Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
                contentArea.getChildren().setAll(content);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private String getStatusStyle(String s) {
        String border, fg;
        switch (s) {
            case "Processing" -> { border="#f59e0b"; fg="#92400e"; }
            case "Shipped"    -> { border="#3b82f6"; fg="#1e3a8a"; }
            case "Delivered"  -> { border="#22c55e"; fg="#14532d"; }
            case "Returned"   -> { border="#a855f7"; fg="#581c87"; }
            case "Cancelled"  -> { border="#ef4444"; fg="#7f1d1d"; }
            default           -> { border="#6b7280"; fg="#111827"; }
        }
        return "-fx-background-color:transparent;-fx-border-color:"+border+
               ";-fx-border-width:1;-fx-text-fill:"+fg+
               ";-fx-font-size:11;-fx-font-weight:bold;" +
               "-fx-font-family:Arial;-fx-padding:4 12;";
    }
}