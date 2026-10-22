package com.noch.noch.controllers;

import javafx.beans.property.SimpleStringProperty;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class AdminOrdersDashboardController {

    @FXML private TableView<String[]>           ordersTable;
    @FXML private TableColumn<String[], String> colOrderId;
    @FXML private TableColumn<String[], String> colCustomer;
    @FXML private TableColumn<String[], String> colDate;
    @FXML private TableColumn<String[], String> colItems;
    @FXML private TableColumn<String[], String> colTotal;
    @FXML private TableColumn<String[], String> colStatus;
    @FXML private TableColumn<String[], String> colActions;
    @FXML private TextField                     searchField;
    @FXML private ComboBox<String>              customerFilter;
    @FXML private ComboBox<String>              statusFilter;
    @FXML private Label                         orderCountLabel;

    private ObservableList<String[]> filteredOrders;

    @FXML
    public void initialize() {
        // ── Load orders from database ─────────────────────────
        Session.orders = OrderDAO.getInstance().getAllOrders();
        filteredOrders = FXCollections.observableArrayList(Session.orders);

        colOrderId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colDate.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue()[3]));
        colTotal.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[6]));

        // Customer
        colCustomer.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        colCustomer.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) { setGraphic(null); return; }
                String[] row = getTableView().getItems().get(getIndex());
                VBox box = new VBox(2);
                Label name = new Label(row[1]);
                name.setStyle("-fx-font-size:13;-fx-font-weight:bold;-fx-text-fill:#000000;-fx-font-family:Arial;");
                Label id = new Label(row[2]);
                id.setStyle("-fx-font-size:11;-fx-text-fill:#888888;-fx-font-family:Arial;");
                box.getChildren().addAll(name, id);
                setGraphic(box); setText(null);
            }
        });

        // Items
        colItems.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));
        colItems.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) { setGraphic(null); return; }
                String[] row = getTableView().getItems().get(getIndex());
                VBox box = new VBox(2);
                Label count = new Label(row[4]);
                count.setStyle("-fx-font-size:13;-fx-text-fill:#000000;-fx-font-family:Arial;");
                Label first = new Label(row[5]);
                first.setStyle("-fx-font-size:11;-fx-text-fill:#888888;-fx-font-family:Arial;");
                box.getChildren().addAll(count, first);
                setGraphic(box); setText(null);
            }
        });

        // Status — NOCH minimalist style
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
                if (empty || getIndex() >= getTableView().getItems().size()) { setGraphic(null); return; }
                String[] row    = getTableView().getItems().get(getIndex());
                String   status = row[7];

                Button viewBtn = new Button("VIEW");
                viewBtn.setStyle(btnStyle(false, false));
                viewBtn.setOnAction(e -> {
                    Session.currentOrder = row;
                    loadIntoContentArea("/fxml/OrderDetail.fxml");
                });

                boolean editOff = status.equals("Cancelled") || status.equals("Delivered");
                Button editBtn = new Button("EDIT");
                editBtn.setStyle(btnStyle(true, editOff));
                editBtn.setDisable(editOff);
                editBtn.setOnAction(e -> {
                    Session.currentOrder = row;
                    loadIntoContentArea("/fxml/EditOrder.fxml");
                });

                boolean cancelOff = status.equals("Cancelled") || status.equals("Delivered") || status.equals("Returned");
                Button cancelBtn = new Button("CANCEL");
                cancelBtn.setStyle(cancelBtnStyle(cancelOff));
                cancelBtn.setDisable(cancelOff);
                cancelBtn.setOnAction(e -> confirmCancel(row));

                HBox box = new HBox(6, viewBtn, editBtn, cancelBtn);
                setGraphic(box); setText(null);
            }
        });

        customerFilter.getItems().add("All Customers");
        customerFilter.getItems().addAll(
            "Alice Johnson","Bob Smith","Carol Williams","David Brown",
            "Emma Davis","Fatima Khan","James Wilson","Sophia Martinez",
            "Liam O'Brien","Priya Patel","Noah Thompson","Isla Campbell","Omar Hassan"
        );
        customerFilter.setValue("All Customers");

        statusFilter.getItems().addAll(
            "All Statuses","Processing","Shipped",
            "Delivered","Pending","Returned","Cancelled"
        );
        statusFilter.setValue("All Statuses");

        ordersTable.setItems(filteredOrders);
        updateCount();
    }

    @FXML private void applyFilters() {
        String search   = searchField.getText().toLowerCase().trim();
        String customer = customerFilter.getValue();
        String status   = statusFilter.getValue();

        List<String[]> result = new ArrayList<>();
        for (String[] r : Session.orders) {
            boolean ms = search.isEmpty() || r[0].toLowerCase().contains(search)
                         || r[1].toLowerCase().contains(search)
                         || r[5].toLowerCase().contains(search);
            boolean mc = customer == null || customer.equals("All Customers") || r[1].equals(customer);
            boolean mv = status   == null || status.equals("All Statuses")   || r[7].equals(status);
            if (ms && mc && mv) result.add(r);
        }
        filteredOrders.setAll(result);
        updateCount();
    }

    @FXML private void clearFilters() {
        searchField.clear();
        customerFilter.setValue("All Customers");
        statusFilter.setValue("All Statuses");
        filteredOrders.setAll(Session.orders);
        updateCount();
    }

    @FXML private void goToPlaceOrder() {
        loadIntoContentArea("/fxml/AdminPlaceOrder.fxml");
    }

    private void confirmCancel(String[] row) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Cancel Order");
        a.setHeaderText("Cancel Order " + row[0] + "?");
        a.setContentText("This action cannot be undone.");
        ButtonType yes = new ButtonType("Yes, Cancel", ButtonBar.ButtonData.OK_DONE);
        ButtonType no  = new ButtonType("Keep Order",  ButtonBar.ButtonData.CANCEL_CLOSE);
        a.getButtonTypes().setAll(yes, no);
        a.showAndWait().ifPresent(b -> {
            if (b == yes) { row[7] = "Cancelled"; ordersTable.refresh(); }
        });
    }

    void loadIntoContentArea(String fxmlPath) {
        try {
            StackPane contentArea = (StackPane) ordersTable
                .getScene().getRoot().lookup("#contentArea");
            if (contentArea != null) {
                Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
                contentArea.getChildren().setAll(content);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void updateCount() {
        int c = filteredOrders.size();
        orderCountLabel.setText(c + " order" + (c != 1 ? "s" : "") + " found");
    }

    // NOCH minimalist status badges — simple bordered labels
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
        return "-fx-background-color:transparent;" +
               "-fx-border-color:"+border+";" +
               "-fx-border-width:1;" +
               "-fx-text-fill:"+fg+";" +
               "-fx-font-size:10;-fx-font-weight:bold;" +
               "-fx-font-family:Arial;-fx-letter-spacing:1;" +
               "-fx-padding:3 8;";
    }

    private String btnStyle(boolean isSecondary, boolean disabled) {
        if (disabled) return "-fx-background-color:#f5f5f5;-fx-text-fill:#cccccc;" +
            "-fx-border-color:#e0e0e0;-fx-border-width:1;-fx-border-radius:0;" +
            "-fx-background-radius:0;-fx-padding:5 12;-fx-font-size:10;" +
            "-fx-font-family:Arial;-fx-letter-spacing:1;";
        if (isSecondary) return "-fx-background-color:white;-fx-text-fill:#000000;" +
            "-fx-border-color:#000000;-fx-border-width:1;-fx-border-radius:0;" +
            "-fx-background-radius:0;-fx-padding:5 12;-fx-font-size:10;" +
            "-fx-font-family:Arial;-fx-letter-spacing:1;-fx-cursor:hand;";
        return "-fx-background-color:#000000;-fx-text-fill:white;" +
            "-fx-border-radius:0;-fx-background-radius:0;" +
            "-fx-padding:5 12;-fx-font-size:10;" +
            "-fx-font-family:Arial;-fx-letter-spacing:1;-fx-cursor:hand;";
    }

    private String cancelBtnStyle(boolean disabled) {
        if (disabled) return "-fx-background-color:#f5f5f5;-fx-text-fill:#cccccc;" +
            "-fx-border-color:#e0e0e0;-fx-border-width:1;-fx-border-radius:0;" +
            "-fx-background-radius:0;-fx-padding:5 12;-fx-font-size:10;" +
            "-fx-font-family:Arial;-fx-letter-spacing:1;";
        return "-fx-background-color:white;-fx-text-fill:#cc0000;" +
            "-fx-border-color:#cc0000;-fx-border-width:1;-fx-border-radius:0;" +
            "-fx-background-radius:0;-fx-padding:5 12;-fx-font-size:10;" +
            "-fx-font-family:Arial;-fx-letter-spacing:1;-fx-cursor:hand;";
    }
}