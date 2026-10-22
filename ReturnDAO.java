package com.noch.noch.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class ReturnDAO {

    private static ReturnDAO instance;
    private final DatabaseManager db = DatabaseManager.getInstance();

    private ReturnDAO() {}

    public static ReturnDAO getInstance() {
        if (instance == null) instance = new ReturnDAO();
        return instance;
    }

    // ── Get all returns ───────────────────────────────────────
    // Returns: [returnId, orderId, customerName, customerId,
    //           items, returnDate, status]
    public ObservableList<String[]> getAllReturns() {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String sql = "SELECT return_id, order_id, customer_name, customer_id, " +
                     "       items, return_date, status " +
                     "FROM returns ORDER BY id DESC";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("return_id"),
                    rs.getString("order_id"),
                    rs.getString("customer_name"),
                    rs.getString("customer_id"),
                    rs.getString("items"),
                    rs.getString("return_date"),
                    rs.getString("status")
                });
            }
            System.out.println("ReturnDAO: loaded " + list.size() + " returns");
        } catch (SQLException e) {
            System.err.println("ReturnDAO.getAllReturns error: " + e.getMessage());
        }
        return list;
    }

    // ── Save new return request ───────────────────────────────
    public boolean saveReturn(String returnId, String orderId,
                              String customerId, String customerName,
                              String items, String reason, String date) {
        String sql = "INSERT INTO returns " +
            "(return_id, order_id, customer_id, customer_name, " +
            " items, reason, return_date, status) " +
            "VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, returnId);
            ps.setString(2, orderId);
            ps.setString(3, customerId);
            ps.setString(4, customerName);
            ps.setString(5, items);
            ps.setString(6, reason);
            ps.setString(7, date);
            ps.setString(8, "Pending");
            ps.executeUpdate();
            System.out.println("ReturnDAO: saved return " + returnId);
            return true;
        } catch (SQLException e) {
            System.err.println("ReturnDAO.saveReturn error: " + e.getMessage());
            return false;
        }
    }

    // ── Update return status ──────────────────────────────────
    public boolean updateStatus(String returnId, String newStatus) {
        String sql = "UPDATE returns SET status = ? WHERE return_id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, returnId);
            int rows = ps.executeUpdate();
            System.out.println("ReturnDAO: " + returnId + " → " + newStatus);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("ReturnDAO.updateStatus error: " + e.getMessage());
            return false;
        }
    }

    // ── Generate next return ID ───────────────────────────────
    public String generateReturnId() {
        String sql = "SELECT COUNT(*) AS total FROM returns";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int next = rs.getInt("total") + 1;
                return String.format("RET-%03d", next);
            }
        } catch (SQLException e) {
            System.err.println("ReturnDAO.generateReturnId error: " + e.getMessage());
        }
        return "RET-" + System.currentTimeMillis();
    }
}