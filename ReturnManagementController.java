package com.noch.noch.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class ReturnManagementController {

    @FXML private TableView<String[]>           returnsTable;
    @FXML private TableColumn<String[], String> colReturnId;
    @FXML private TableColumn<String[], String> colOrderId;
    @FXML private TableColumn<String[], String> colCustomer;
    @FXML private TableColumn<String[], String> colItems;
    @FXML private TableColumn<String[], String> colDate;
    @FXML private TableColumn<String[], String> colStatus;
    @FXML private TableColumn<String[], String> colActions;
    @FXML private Label                         countLabel;

    // [returnId, orderId, customerName, customerId, items, date, status]
    private final ObservableList<String[]> allReturns = FXCollections.observableArrayList(
        new String[]{"RET-001","ORD-005","Emma Davis",    "CUST-005","Wool Overcoat x1",        "Mar 12, 2026","Pending"},
        new String[]{"RET-002","ORD-003","Carol Williams","CUST-003","Floral Summer Dress x1",  "Mar 14, 2026","Approved"},
        new String[]{"RET-003","ORD-004","David Brown",   "CUST-004","Leather Jacket x1",       "Mar 13, 2026","Rejected"},
        new String[]{"RET-004","ORD-006","Alice Johnson", "CUST-001","Striped Polo Shirt x2",   "Mar 11, 2026","Pending"},
        new String[]{"RET-005","ORD-011","Liam O'Brien",  "CUST-009","Graphic Hoodie x1",       "Mar 7, 2026", "Pending"}
    );

    @FXML
    public void initialize() {
        // ── Load returns from database ────────────────────────
        allReturns.setAll(ReturnDAO.getInstance().getAllReturns());

        colReturnId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colOrderId.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[1]));
        colCustomer.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        colItems.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue()[4]));
        colDate.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue()[5]));

        // Status badge
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[6]));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setGraphic(null); return; }
                Label badge = new Label(status);
                badge.setStyle(getStatusStyle(status));
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

                if (row[6].equals("Pending")) {
                    Button approveBtn = new Button("✓ Approve");
                    approveBtn.setStyle(
                        "-fx-background-color:#dcfce7;-fx-text-fill:#166534;" +
                        "-fx-background-radius:6;-fx-padding:5 12;" +
                        "-fx-font-size:12;-fx-cursor:hand;"
                    );
                    approveBtn.setOnAction(e -> handleAction(row, "Approved"));

                    Button rejectBtn = new Button("✕ Reject");
                    rejectBtn.setStyle(
                        "-fx-background-color:#fee2e2;-fx-text-fill:#991b1b;" +
                        "-fx-background-radius:6;-fx-padding:5 12;" +
                        "-fx-font-size:12;-fx-cursor:hand;"
                    );
                    rejectBtn.setOnAction(e -> handleAction(row, "Rejected"));

                    setGraphic(new HBox(8, approveBtn, rejectBtn));
                } else {
                    Label noAction = new Label("No actions available");
                    noAction.setStyle("-fx-font-size:12;-fx-text-fill:#94a3b8;");
                    setGraphic(noAction);
                }
                setText(null);
            }
        });

        returnsTable.setItems(allReturns);
        updateCount();
    }

    private void handleAction(String[] row, String newStatus) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(newStatus.equals("Approved") ? "Approve Return" : "Reject Return");
        confirm.setHeaderText(
            newStatus.equals("Approved")
                ? "Approve return request " + row[0] + "?"
                : "Reject return request " + row[0] + "?"
        );
        confirm.setContentText(
            newStatus.equals("Approved")
                ? "The order status will be updated to Returned."
                : "The customer will be notified of the rejection."
        );

        ButtonType yesBtn = new ButtonType(
            newStatus.equals("Approved") ? "Approve Return" : "Reject Return",
            ButtonBar.ButtonData.OK_DONE
        );
        ButtonType noBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(yesBtn, noBtn);

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == yesBtn) {
                row[6] = newStatus;
                // If approved, also update the order status in Session
                if (newStatus.equals("Approved") && Session.orders != null) {
                    for (String[] order : Session.orders) {
                        if (order[0].equals(row[1])) {
                            order[7] = "Returned";
                            break;
                        }
                    }
                }
                returnsTable.refresh();
                updateCount();
            }
        });
    }

    private void updateCount() {
        int c = allReturns.size();
        countLabel.setText(c + " return request" + (c != 1 ? "s" : "") + " in the system");
    }

    private String getStatusStyle(String status) {
        return switch (status) {
            case "Approved" -> "-fx-background-color:#dcfce7;-fx-text-fill:#166534;-fx-background-radius:12;-fx-padding:3 10;-fx-font-size:12;-fx-font-weight:bold;";
            case "Rejected" -> "-fx-background-color:#fee2e2;-fx-text-fill:#991b1b;-fx-background-radius:12;-fx-padding:3 10;-fx-font-size:12;-fx-font-weight:bold;";
            default         -> "-fx-background-color:#fef9c3;-fx-text-fill:#854d0e;-fx-background-radius:12;-fx-padding:3 10;-fx-font-size:12;-fx-font-weight:bold;";
        };
    }
}