package com.noch.noch.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrderHistoryController {

    @FXML private TableView<String[]>           ordersTable;
    @FXML private TableColumn<String[], String> colOrderId;
    @FXML private TableColumn<String[], String> colDate;
    @FXML private TableColumn<String[], String> colItems;
    @FXML private TableColumn<String[], String> colTotal;
    @FXML private TableColumn<String[], String> colStatus;
    @FXML private TableColumn<String[], String> colActions;
    @FXML private ComboBox<String>              statusFilter;
    @FXML private Label                         orderCountLabel;

    private ObservableList<String[]> myOrders;

    @FXML
    public void initialize() {
        // ── Load orders from Session, filtered for current customer ──
        Session.initOrders();
        myOrders = getMyOrders("Alice Johnson");

        colOrderId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colDate.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue()[3]));
        colTotal.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[6]));

        // Items column — show summary
        colItems.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));
        colItems.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) { setGraphic(null); return; }
                String[] row = getTableView().getItems().get(getIndex());
                javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(2);
                Label count = new Label(row[4]);
                count.setStyle("-fx-font-size:13;-fx-text-fill:#000000;-fx-font-family:Arial;");
                Label detail = new Label(row[5]);
                detail.setStyle("-fx-font-size:11;-fx-text-fill:#888888;-fx-font-family:Arial;");
                box.getChildren().addAll(count, detail);
                setGraphic(box); setText(null);
            }
        });

        // Status badge
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[7]));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setGraphic(null); return; }
                Label badge = new Label(status.toUpperCase());
                badge.setStyle(statusStyle(status));
                setGraphic(badge); setText(null);
            }
        });

        // Actions
        colActions.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null); return;
                }
                String[] row = getTableView().getItems().get(getIndex());
                Button viewBtn = new Button("VIEW DETAILS");
                viewBtn.setStyle(
                    "-fx-background-color:#000000;-fx-text-fill:white;" +
                    "-fx-background-radius:0;-fx-padding:7 14;" +
                    "-fx-font-size:10;-fx-font-weight:bold;" +
                    "-fx-font-family:Arial;-fx-letter-spacing:1;-fx-cursor:hand;"
                );
                viewBtn.setOnAction(e -> {
                    Session.currentOrder = row;
                    navigateToContentArea("/fxml/CustomerOrderDetail.fxml");
                });
                setGraphic(viewBtn); setText(null);
            }
        });

        statusFilter.getItems().addAll(
            "All Statuses","Processing","Shipped","Delivered","Returned","Cancelled"
        );
        statusFilter.setValue("All Statuses");

        ordersTable.setItems(myOrders);
        updateCount(myOrders.size());
    }

    @FXML private void applyFilter() {
        String selected = statusFilter.getValue();
        ObservableList<String[]> filtered;
        if (selected == null || selected.equals("All Statuses")) {
            filtered = getMyOrders("Alice Johnson");
        } else {
            List<String[]> result = new ArrayList<>();
            for (String[] row : getMyOrders("Alice Johnson")) {
                if (row[7].equals(selected)) result.add(row);
            }
            filtered = FXCollections.observableArrayList(result);
        }
        ordersTable.setItems(filtered);
        updateCount(filtered.size());
    }

    // ── Returns orders for the logged-in customer from DB ────
    private ObservableList<String[]> getMyOrders(String customerName) {
        return OrderDAO.getInstance().getOrdersByCustomer(customerName);
    }

    private void updateCount(int c) {
        orderCountLabel.setText(c + " order" + (c != 1 ? "s" : "") + " found");
    }

    private void navigateToContentArea(String fxmlPath) {
        try {
            StackPane contentArea = (StackPane) ordersTable
                .getScene().getRoot().lookup("#contentArea");
            if (contentArea != null) {
                Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
                contentArea.getChildren().setAll(content);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private String statusStyle(String s) {
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
               ";-fx-font-size:10;-fx-font-weight:bold;" +
               "-fx-font-family:Arial;-fx-letter-spacing:1;-fx-padding:3 8;";
    }
}