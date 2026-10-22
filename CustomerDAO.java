package com.noch.noch.controllers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private static CustomerDAO instance;
    private final DatabaseManager db = DatabaseManager.getInstance();

    private CustomerDAO() {}

    public static CustomerDAO getInstance() {
        if (instance == null) instance = new CustomerDAO();
        return instance;
    }

    // ── Search customer by name or ID ─────────────────────────
    // Returns: [customer_id, name, email] or null
    public String[] searchCustomer(String term) {
        String sql = "SELECT customer_id, name, email FROM users " +
                     "WHERE role = 'customer' AND " +
                     "(LOWER(name) LIKE LOWER(?) OR LOWER(customer_id) LIKE LOWER(?))";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + term + "%");
            ps.setString(2, "%" + term + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("email")
                };
            }
        } catch (SQLException e) {
            System.err.println("CustomerDAO.searchCustomer error: " + e.getMessage());
        }
        return null;
    }

    // ── Get all customers ─────────────────────────────────────
    // Returns: List of [customer_id, name, email]
    public List<String[]> getAllCustomers() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT customer_id, name, email FROM users " +
                     "WHERE role = 'customer' ORDER BY name";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("email")
                });
            }
        } catch (SQLException e) {
            System.err.println("CustomerDAO.getAllCustomers error: " + e.getMessage());
        }
        return list;
    }
}