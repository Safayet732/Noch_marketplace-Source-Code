package com.noch.noch.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CustomerLoginController {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label         emailError;
    @FXML private Label         passwordError;

    @FXML private void goBack() {
        navigateTo("/fxml/AdminLogin.fxml", "NOCH – Portal", 620, 720);
    }

    @FXML private void handleLogin() {
        emailError.setText("");
        passwordError.setText("");

        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();
        boolean hasError = false;

        if (email.isEmpty()) {
            emailError.setText("Email is required");
            hasError = true;
        }
        if (password.isEmpty()) {
            passwordError.setText("Password is required");
            hasError = true;
        }
        if (hasError) return;

        navigateTo("/fxml/CustomerLayout.fxml", "NOCH – Collection", 1280, 800);
    }

    private void navigateTo(String fxmlPath, String title, int w, int h) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), w, h);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}