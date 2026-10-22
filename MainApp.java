package com.noch.noch;

import com.noch.noch.controllers.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // ── Test DB connection on startup ──────────────────────
        boolean dbOk = DatabaseManager.getInstance().testConnection();
        if (dbOk) {
            System.out.println("✓ Database ready");
        } else {
            System.err.println("✗ Database not found — check nochmarketplace.db path");
        }

        // ── Load login screen ──────────────────────────────────
        URL fxmlUrl = MainApp.class.getResource("/fxml/AdminLogin.fxml");
        System.out.println("FXML URL: " + fxmlUrl);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 620, 720);
        stage.setTitle("NOCH – Minimalist Essentials");
        stage.setMinWidth(600);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        // Close DB connection when app closes
        DatabaseManager.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}