package com.noch.noch.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnRequestController {

    @FXML private Label      orderIdLabel;
    @FXML private VBox       returnItemsContainer;
    @FXML private DatePicker returnDatePicker;
    @FXML private Label      errorLabel;

    private final List<CheckBox> checkBoxes  = new ArrayList<>();
    private final List<TextArea> reasonAreas = new ArrayList<>();
    private List<String[]>       orderItems  = new ArrayList<>();

    @FXML
    public void initialize() {
        if (Session.currentOrder != null) {
            orderIdLabel.setText("Order ID: " + Session.currentOrder[0]);

            // ── Load real items from database ──────────────────
            orderItems = OrderDAO.getInstance().getOrderItems(Session.currentOrder[0]);
        } else {
            orderIdLabel.setText("Order ID: —");
        }

        returnDatePicker.setValue(LocalDate.now());
        errorLabel.setText("");

        // Build item rows from DB items
        for (String[] item : orderItems) {
            // item: [product_name, unit_price, quantity, subtotal]
            returnItemsContainer.getChildren().add(buildItemRow(item));
        }
    }

    private VBox buildItemRow(String[] item) {
        // item: [0=product_name, 1=unit_price, 2=quantity, 3=subtotal]
        VBox box = new VBox(10);
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 16;"
        );

        HBox topRow = new HBox(12);
        topRow.setStyle("-fx-alignment:CENTER_LEFT;");

        CheckBox cb = new CheckBox();
        checkBoxes.add(cb);

        VBox info = new VBox(3);
        Label nameLabel = new Label(item[0]);
        nameLabel.setStyle("-fx-font-size:13;-fx-font-weight:bold;-fx-text-fill:#000000;-fx-font-family:Arial;");
        Label qtyLabel = new Label("Quantity: " + item[2]);
        qtyLabel.setStyle("-fx-font-size:11;-fx-text-fill:#888888;-fx-font-family:Arial;");
        info.getChildren().addAll(nameLabel, qtyLabel);
        topRow.getChildren().addAll(cb, info);

        VBox reasonBox = new VBox(6);
        reasonBox.setVisible(false);
        reasonBox.setManaged(false);

        Label reasonLabel = new Label("REASON FOR RETURN *");
        reasonLabel.setStyle("-fx-font-size:11;-fx-font-weight:bold;-fx-text-fill:#333333;-fx-font-family:Arial;-fx-letter-spacing:1;");

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Describe why you want to return this item...");
        reasonArea.setPrefRowCount(3);
        reasonArea.setStyle("-fx-background-radius:0;-fx-border-color:#000000;-fx-border-width:1;-fx-font-size:12;-fx-font-family:Arial;");
        reasonAreas.add(reasonArea);
        reasonBox.getChildren().addAll(reasonLabel, reasonArea);

        cb.selectedProperty().addListener((obs, old, selected) -> {
            reasonBox.setVisible(selected);
            reasonBox.setManaged(selected);
            box.setStyle(
                "-fx-background-color:" + (selected ? "#f9f9f9" : "white") + ";" +
                "-fx-border-color:#000000;" +
                "-fx-border-width:" + (selected ? "2" : "1") + ";" +
                "-fx-padding:16;"
            );
        });

        box.getChildren().addAll(topRow, reasonBox);
        return box;
    }

    @FXML private void handleSubmit() {
        errorLabel.setText("");

        boolean anySelected = checkBoxes.stream().anyMatch(CheckBox::isSelected);
        if (!anySelected) {
            errorLabel.setText("Please select at least one item to return.");
            return;
        }

        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected() && reasonAreas.get(i).getText().trim().isEmpty()) {
                errorLabel.setText("Please provide a reason for all selected items.");
                return;
            }
        }

        // Build items summary and reason
        StringBuilder itemsSummary = new StringBuilder();
        StringBuilder reasonSummary = new StringBuilder();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                String[] item = orderItems.get(i);
                itemsSummary.append(item[0]).append(" x").append(item[2]).append(", ");
                reasonSummary.append(reasonAreas.get(i).getText().trim()).append("; ");
            }
        }
        // Remove trailing comma
        String items  = itemsSummary.toString().replaceAll(", $", "");
        String reason = reasonSummary.toString().replaceAll("; $", "");
        String date   = returnDatePicker.getValue().toString();

        // Save to database
        if (Session.currentOrder != null) {
            String returnId = ReturnDAO.getInstance().generateReturnId();
            String customerId   = Session.currentOrder[2];
            String customerName = Session.currentOrder[1];
            String orderId      = Session.currentOrder[0];

            ReturnDAO.getInstance().saveReturn(
                returnId, orderId, customerId, customerName, items, reason, date
            );
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Return Submitted");
        alert.setHeaderText("Return Request Submitted");
        alert.setContentText("Your return request has been received. We will review it shortly.");
        alert.showAndWait();

        navigateToContentArea("/fxml/CustomerOrderHistory.fxml");
    }

    @FXML private void goBack() {
        navigateToContentArea("/fxml/CustomerOrderDetail.fxml");
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