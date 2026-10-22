package com.noch.noch.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AdminLayoutController {

    @FXML private StackPane contentArea;
    @FXML private Button    dashboardBtn;
    @FXML private Button    placeOrderBtn;
    @FXML private Button    customerLookupBtn;
    @FXML private Button    returnsBtn;

    @FXML
    public void initialize() {
        loadContent("/fxml/AdminOrdersDashboard.fxml");
        setActive(dashboardBtn);
    }

    @FXML private void goToDashboard() {
        setActive(dashboardBtn);
        setInactive(placeOrderBtn);
        setInactive(customerLookupBtn);
        setInactive(returnsBtn);
        loadContent("/fxml/AdminOrdersDashboard.fxml");
    }

    @FXML private void goToPlaceOrder() {
        setInactive(dashboardBtn);
        setActive(placeOrderBtn);
        setInactive(customerLookupBtn);
        setInactive(returnsBtn);
        loadContent("/fxml/AdminPlaceOrder.fxml");
    }

    @FXML private void goToCustomerLookup() {
        setInactive(dashboardBtn);
        setInactive(placeOrderBtn);
        setActive(customerLookupBtn);
        setInactive(returnsBtn);
        loadContent("/fxml/CustomerLookup.fxml");
    }

    @FXML private void goToReturns() {
        setInactive(dashboardBtn);
        setInactive(placeOrderBtn);
        setInactive(customerLookupBtn);
        setActive(returnsBtn);
        loadContent("/fxml/ReturnManagement.fxml");
    }

    @FXML private void handleLogout() {
        try {
            Session.orders = null;
            Session.currentOrder = null;
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/AdminLogin.fxml")
            );
            Scene scene = new Scene(loader.load(), 620, 720);
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setTitle("NOCH – Minimalist Essentials");
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void loadContent(String fxmlPath) {
        try {
            Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(content);
        } catch (Exception ex) {
            System.err.println("Could not load: " + fxmlPath);
            ex.printStackTrace();
        }
    }

    private void setActive(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #000000;" +
            "-fx-font-size: 11; -fx-font-weight: bold;" +
            "-fx-font-family: Arial; -fx-letter-spacing: 2;" +
            "-fx-padding: 20 20;" +
            "-fx-border-color: transparent transparent #000000 transparent;" +
            "-fx-border-width: 0 0 2 0;" +
            "-fx-cursor: hand;"
        );
    }

    private void setInactive(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #888888;" +
            "-fx-font-size: 11;" +
            "-fx-font-family: Arial; -fx-letter-spacing: 2;" +
            "-fx-padding: 20 20;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;"
        );
    }
}