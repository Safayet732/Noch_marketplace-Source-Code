package com.noch.noch.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PortalSelectionController {

    @FXML private VBox customerCard;
    @FXML private VBox adminCard;

    @FXML private void onCustomerHover(MouseEvent e) {
        customerCard.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-border-color: #000000;" +
            "-fx-border-width: 2;" +
            "-fx-cursor: hand;"
        );
    }

    @FXML private void onCustomerExit(MouseEvent e) {
        customerCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #000000;" +
            "-fx-border-width: 1;" +
            "-fx-cursor: hand;"
        );
    }

    @FXML private void onAdminHover(MouseEvent e) {
        adminCard.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-border-color: #000000;" +
            "-fx-border-width: 2;" +
            "-fx-cursor: hand;"
        );
    }

    @FXML private void onAdminExit(MouseEvent e) {
        adminCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #000000;" +
            "-fx-border-width: 1;" +
            "-fx-cursor: hand;"
        );
    }

    @FXML private void goToCustomerLogin(MouseEvent e) {
        navigateTo("/fxml/CustomerLogin.fxml", "NOCH – Customer Login", 480, 620);
    }

    @FXML private void goToAdminLogin(MouseEvent e) {
        navigateTo("/fxml/AdminLogin.fxml", "NOCH – Admin Login", 480, 580);
    }

    private void navigateTo(String fxmlPath, String title, int w, int h) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), w, h);
            Stage stage = (Stage) customerCard.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}