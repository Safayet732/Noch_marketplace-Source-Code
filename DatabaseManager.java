package com.noch.noch.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;

    // Path to DB — relative to project root (where Eclipse runs from)
    private static final String DB_URL =
        "jdbc:sqlite:nochmarketplace.db";

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    // ── Get or open connection ────────────────────────────────
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
            System.out.println("DB connected: " + DB_URL);
        }
        return connection;
    }

    // ── Close connection safely ───────────────────────────────
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("DB connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing DB: " + e.getMessage());
        }
    }

    // ── Test connection — call this first to verify DB works ──
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("SELECT 1");
            stmt.close();
            System.out.println("DB connection test: SUCCESS");
            return true;
        } catch (SQLException e) {
            System.err.println("DB connection test FAILED: " + e.getMessage());
            return false;
        }
    }
}