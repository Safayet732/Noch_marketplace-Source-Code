package com.noch.noch.controllers;

import java.sql.*;

public class UserDAO {

    private static UserDAO instance;
    private final DatabaseManager db = DatabaseManager.getInstance();

    private UserDAO() {}

    public static UserDAO getInstance() {
        if (instance == null) instance = new UserDAO();
        return instance;
    }

    // ── Login — returns user row or null if not found ─────────
    // Returns: [id, name, email, role, customer_id]
    public String[] login(String email, String password, String role) {
        String sql = "SELECT id, name, email, role, customer_id " +
                     "FROM users " +
                     "WHERE LOWER(email) = LOWER(?) " +
                     "AND password = ? " +
                     "AND role = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, password.trim());
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getString("customer_id") != null
                        ? rs.getString("customer_id") : ""
                };
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.login error: " + e.getMessage());
        }
        return null; // login failed
    }

    // ── Get user by email ─────────────────────────────────────
    public String[] getUserByEmail(String email) {
        String sql = "SELECT id, name, email, role, customer_id " +
                     "FROM users WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getString("customer_id") != null
                        ? rs.getString("customer_id") : ""
                };
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.getUserByEmail error: " + e.getMessage());
        }
        return null;
    }
}