package com.noch.noch.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderConfirmationController {

    @FXML private Label orderIdLabel;
    @FXML private Label statusBadge;
    @FXML private Label orderDateLabel;
    @FXML private Label totalLabel;
    @FXML private VBox  itemsList;

    @FXML
    public void initialize() {
        // ── Read order info from Session.currentOrder ──────────
        if (Session.currentOrder != null) {
            orderIdLabel.setText("Order #" + Session.currentOrder[0]);
        } else {
            orderIdLabel.setText("Order #ORD-" + (int)(Math.random() * 9000 + 1000));
        }

        statusBadge.setText("PROCESSING");
        statusBadge.setStyle(
            "-fx-border-color:#f59e0b;-fx-border-width:1;" +
            "-fx-text-fill:#92400e;-fx-padding:4 12;" +
            "-fx-font-size:10;-fx-font-weight:bold;" +
            "-fx-font-family:Arial;-fx-letter-spacing:1;"
        );

        orderDateLabel.setText(
            LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
        );

        // ── Read total from Session ────────────────────────────
        totalLabel.setText(Session.lastOrderTotal.isEmpty()
            ? "£0.00" : Session.lastOrderTotal);

        // ── Read items from Session.lastOrderItems ─────────────
        itemsList.getChildren().clear();

        if (Session.lastOrderItems.isEmpty()) {
            Label empty = new Label("No items found.");
            empty.setStyle("-fx-font-size:13;-fx-text-fill:#888888;-fx-font-family:Arial;");
            itemsList.getChildren().add(empty);
        } else {
            for (String[] item : Session.lastOrderItems) {
                // item: [0=name, 1=qty, 2=subtotal]
                HBox row = new HBox();
                row.setStyle(
                    "-fx-padding:12 0;" +
                    "-fx-border-color:#f0f0f0;" +
                    "-fx-border-width:0 0 1 0;" +
                    "-fx-alignment:CENTER_LEFT;"
                );

                VBox info = new VBox(3);
                HBox.setHgrow(info, Priority.ALWAYS);

                Label nameLabel = new Label(item[0]);
                nameLabel.setStyle(
                    "-fx-font-size:14;-fx-font-weight:bold;" +
                    "-fx-text-fill:#000000;-fx-font-family:Arial;"
                );
                Label qtyLabel = new Label("Quantity: " + item[1]);
                qtyLabel.setStyle(
                    "-fx-font-size:12;-fx-text-fill:#888888;-fx-font-family:Arial;"
                );
                info.getChildren().addAll(nameLabel, qtyLabel);

                Label priceLabel = new Label(item[2]);
                priceLabel.setStyle(
                    "-fx-font-size:16;-fx-font-weight:bold;" +
                    "-fx-text-fill:#000000;-fx-font-family:Georgia;"
                );

                row.getChildren().addAll(info, priceLabel);
                itemsList.getChildren().add(row);
            }
        }
    }

    @FXML private void browsMore() {
        navigateToContentArea("/fxml/ProductCatalogue.fxml");
    }

    @FXML private void viewHistory() {
        navigateToContentArea("/fxml/CustomerOrderHistory.fxml");
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
}