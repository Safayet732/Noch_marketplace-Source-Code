package com.noch.noch.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminLoginController implements Initializable {

    @FXML private Label         subtitleLabel;
    @FXML private Button        customerBtn;
    @FXML private Button        staffBtn;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label         emailErrorLabel;
    @FXML private Label         passwordErrorLabel;
    @FXML private Label         footerLabel;
    @FXML private Button        autofillBtn;

    private String currentUserType = "customer";

    // ── Use UserDAO instead of hardcoded credentials ──────────
    private final UserDAO userDAO = UserDAO.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setUserType("customer");
        emailField.textProperty().addListener((obs, old, text) -> {
            clearEmailError();
            if (!text.isBlank()) validateEmail(text);
        });
        passwordField.textProperty().addListener((obs, old, text) -> {
            if (!text.isBlank()) clearPasswordError();
        });
    }

    @FXML private void onCustomerBtn() { setUserType("customer"); }
    @FXML private void onStaffBtn()    { setUserType("staff"); }

    @FXML
    private void onAutofill() {
        if ("staff".equals(currentUserType)) {
            emailField.setText("admin@noch.com");
            passwordField.setText("staff123");
        } else {
            emailField.setText("customer@gmail.com");
            passwordField.setText("customer123");
        }
        clearEmailError();
        clearPasswordError();
    }

    @FXML
    private void onLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();
        clearEmailError();
        clearPasswordError();

        boolean hasError = false;
        if (email.isBlank()) {
            showEmailError("Email address is required.");
            hasError = true;
        } else {
            String err = validateEmailString(email, currentUserType);
            if (!err.isEmpty()) { showEmailError(err); hasError = true; }
        }
        if (password.isBlank()) {
            showPasswordError("Password is required.");
            hasError = true;
        }
        if (hasError) return;

        // ── Check credentials against database ────────────────
        String[] user = userDAO.login(email, password, currentUserType);

        if (user != null) {
            // user: [id, name, email, role, customer_id]
            Session.loggedInUser = user;
            try {
                Stage stage = (Stage) emailField.getScene().getWindow();
                if ("staff".equals(currentUserType)) {
                    Scene scene = new Scene(
                        FXMLLoader.load(getClass().getResource("/fxml/AdminLayout.fxml")),
                        1280, 800
                    );
                    stage.setTitle("NOCH – Admin");
                    stage.setScene(scene);
                } else {
                    Scene scene = new Scene(
                        FXMLLoader.load(getClass().getResource("/fxml/CustomerLayout.fxml")),
                        1280, 800
                    );
                    stage.setTitle("NOCH – Collection");
                    stage.setScene(scene);
                }
                stage.show();
            } catch (Exception e) {
                showEmailError("Could not load screen: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Wrong credentials
            showPasswordError("Incorrect email or password.");
        }
    }

    private void setUserType(String type) {
        currentUserType = type;
        clearEmailError();
        clearPasswordError();
        emailField.clear();
        passwordField.clear();
        if ("staff".equals(type)) {
            staffBtn.getStyleClass().setAll("toggle-btn","toggle-btn-active");
            customerBtn.getStyleClass().setAll("toggle-btn","toggle-btn-inactive");
            subtitleLabel.setText("STAFF ACCESS");
            footerLabel.setText("Authorized staff only");
            emailField.setPromptText("admin@noch.com");
            autofillBtn.setText("Auto-fill staff credentials");
        } else {
            customerBtn.getStyleClass().setAll("toggle-btn","toggle-btn-active");
            staffBtn.getStyleClass().setAll("toggle-btn","toggle-btn-inactive");
            subtitleLabel.setText("CUSTOMER ACCESS");
            footerLabel.setText("Welcome to Noch");
            emailField.setPromptText("customer@email.com");
            autofillBtn.setText("Auto-fill customer credentials");
        }
    }

    private String validateEmailString(String email, String type) {
        String lower = email.toLowerCase().trim();
        if ("staff".equals(type)) {
            if (!lower.contains("@noch.com"))
                return "Staff email must use a @noch.com address.";
        } else {
            int at  = lower.indexOf('@');
            int dot = lower.lastIndexOf('.');
            boolean valid = at > 0 && dot > at + 1
                    && dot < lower.length() - 1
                    && lower.endsWith(".com")
                    && !lower.contains(" ");
            if (!valid) return "Please enter a valid email address.";
        }
        return "";
    }

    private void validateEmail(String email) {
        String error = validateEmailString(email, currentUserType);
        if (!error.isEmpty()) showEmailError(error);
        else clearEmailError();
    }

    private void showEmailError(String msg) {
        emailErrorLabel.setText(msg);
        emailErrorLabel.setVisible(true);
        emailErrorLabel.setManaged(true);
        emailField.getStyleClass().remove("field-error");
        emailField.getStyleClass().add("field-error");
    }

    private void clearEmailError() {
        emailErrorLabel.setText("");
        emailErrorLabel.setVisible(false);
        emailErrorLabel.setManaged(false);
        emailField.getStyleClass().remove("field-error");
    }

    private void showPasswordError(String msg) {
        passwordErrorLabel.setText(msg);
        passwordErrorLabel.setVisible(true);
        passwordErrorLabel.setManaged(true);
        passwordField.getStyleClass().remove("field-error");
        passwordField.getStyleClass().add("field-error");
    }

    private void clearPasswordError() {
        passwordErrorLabel.setText("");
        passwordErrorLabel.setVisible(false);
        passwordErrorLabel.setManaged(false);
        passwordField.getStyleClass().remove("field-error");
    }
}