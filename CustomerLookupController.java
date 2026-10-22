package com.noch.noch.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CustomerLookupController {

    @FXML private TextField                     searchField;
    @FXML private HBox                          customerInfoPanel;
    @FXML private Label                         customerNameLabel;
    @FXML private Label                         customerEmailLabel;
    @FXML private Label                         customerIdLabel;
    @FXML private Label                         orderCountLabel;
    @FXML private Label                         notFoundLabel;
    @FXML private VBox                          orderHistoryPanel;
    @FXML private Label                         orderHistorySubtitle;
    @FXML private TableView<String[]>           ordersTable;
    @FXML private TableColumn<String[], String> colOrderId;
    @FXML private TableColumn<String[], String> colDate;
    @FXML private TableColumn<String[], String> colItems;
    @FXML private TableColumn<String[], String> colTotal;
    @FXML private TableColumn<String[], String> colStatus;
    @FXML private TableColumn<String[], String> colActions;

    private final String[][] CUSTOMERS = {
        {"CUST-001","Alice Johnson","alice@noch.com"},
        {"CUST-002","Bob Smith",    "bob@noch.com"},
        {"CUST-003","Carol Williams","carol@noch.com"},
        {"CUST-004","David Brown",  "david@noch.com"},
        {"CUST-005","Emma Davis",   "emma@noch.com"},
        {"CUST-006","Fatima Khan",  "fatima@noch.com"},
        {"CUST-007","James Wilson", "james@noch.com"},
        {"CUST-008","Sophia Martinez","sophia@noch.com"},
        {"CUST-009","Liam O'Brien", "liam@noch.com"},
        {"CUST-010","Priya Patel",  "priya@noch.com"},
        {"CUST-011","Noah Thompson","noah@noch.com"},
        {"CUST-012","Isla Campbell","isla@noch.com"},
        {"CUST-013","Omar Hassan",  "omar@noch.com"}
    };

    @FXML
    public void initialize() {
        colOrderId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colDate.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue()[3]));
        colItems.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[4]));
        colTotal.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[6]));

        // Status badge — NOCH style
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

        // Actions — View Details loads into contentArea so navbar stays
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
                    "-fx-background-radius:0;-fx-padding:6 14;" +
                    "-fx-font-size:10;-fx-font-weight:bold;" +
                    "-fx-font-family:Arial;-fx-letter-spacing:1;-fx-cursor:hand;"
                );
                viewBtn.setOnAction(e -> {
                    // Store selected order so detail page can read it
                    Session.currentOrder = row;
                    loadIntoContentArea("/fxml/OrderDetail.fxml");
                });
                setGraphic(viewBtn); setText(null);
            }
        });
    }

    @FXML private void onEnterKey(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) handleSearch();
    }

    @FXML private void handleSearch() {
        String term = searchField.getText().trim().toLowerCase();

        customerInfoPanel.setVisible(false);
        customerInfoPanel.setManaged(false);
        orderHistoryPanel.setVisible(false);
        orderHistoryPanel.setManaged(false);
        notFoundLabel.setVisible(false);
        notFoundLabel.setManaged(false);

        if (term.isEmpty()) return;

        // ── Search customer in database ───────────────────────
        String[] found = CustomerDAO.getInstance().searchCustomer(term);

        if (found == null) {
            notFoundLabel.setVisible(true);
            notFoundLabel.setManaged(true);
            return;
        }

        // Show customer info
        customerNameLabel.setText(found[1]);
        customerEmailLabel.setText(found[2]);
        customerIdLabel.setText("Customer ID: " + found[0]);
        customerInfoPanel.setVisible(true);
        customerInfoPanel.setManaged(true);

        // ── Load orders from database for this customer ──────
        final String customerName = found[1];
        ObservableList<String[]> customerOrders =
            OrderDAO.getInstance().getOrdersByCustomer(customerName);

        orderCountLabel.setText(String.valueOf(customerOrders.size()));
        orderHistorySubtitle.setText("All orders for " + found[1]);
        ordersTable.setItems(customerOrders);

        orderHistoryPanel.setVisible(true);
        orderHistoryPanel.setManaged(true);
    }

    private void loadIntoContentArea(String fxmlPath) {
        try {
            StackPane contentArea = (StackPane) searchField
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
            case "Pending"    -> { border="#f97316"; fg="#7c2d12"; }
            default           -> { border="#6b7280"; fg="#111827"; }
        }
        return "-fx-background-color:transparent;-fx-border-color:"+border+
               ";-fx-border-width:1;-fx-text-fill:"+fg+
               ";-fx-font-size:10;-fx-font-weight:bold;" +
               "-fx-font-family:Arial;-fx-letter-spacing:1;-fx-padding:3 8;";
    }
}