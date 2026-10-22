package com.noch.noch.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDAO {

    private static OrderDAO instance;
    private final DatabaseManager db = DatabaseManager.getInstance();

    private OrderDAO() {}

    public static OrderDAO getInstance() {
        if (instance == null) instance = new OrderDAO();
        return instance;
    }

    // ── Get ALL orders (for admin dashboard) ──────────────────
    // Returns: [orderId, customerName, customerId, date,
    //           itemCount, firstItem, total, status]
    public ObservableList<String[]> getAllOrders() {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String sql = "SELECT o.order_id, o.customer_name, o.customer_id, " +
                     "       o.order_date, o.total, o.status, " +
                     "       COUNT(oi.id) AS item_count, " +
                     "       MIN(oi.product_name) AS first_item " +
                     "FROM orders o " +
                     "LEFT JOIN order_items oi ON o.order_id = oi.order_id " +
                     "GROUP BY o.order_id " +
                     "ORDER BY o.id DESC";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int    count     = rs.getInt("item_count");
                String firstItem = rs.getString("first_item");
                String itemSummary = count + " item" + (count != 1 ? "s" : "");
                String itemDetail  = count > 1
                    ? firstItem + " +" + (count - 1) + " more"
                    : (firstItem != null ? firstItem : "—");

                list.add(new String[]{
                    rs.getString("order_id"),
                    rs.getString("customer_name"),
                    rs.getString("customer_id"),
                    rs.getString("order_date"),
                    itemSummary,
                    itemDetail,
                    "£" + String.format("%.2f", rs.getDouble("total")),
                    rs.getString("status")
                });
            }
            System.out.println("OrderDAO: loaded " + list.size() + " orders");
        } catch (SQLException e) {
            System.err.println("OrderDAO.getAllOrders error: " + e.getMessage());
        }
        return list;
    }

    // ── Get orders for one customer ───────────────────────────
    public ObservableList<String[]> getOrdersByCustomer(String customerName) {
        ObservableList<String[]> list = FXCollections.observableArrayList();
        String sql = "SELECT o.order_id, o.customer_name, o.customer_id, " +
                     "       o.order_date, o.total, o.status, " +
                     "       COUNT(oi.id) AS item_count, " +
                     "       MIN(oi.product_name) AS first_item " +
                     "FROM orders o " +
                     "LEFT JOIN order_items oi ON o.order_id = oi.order_id " +
                     "WHERE o.customer_name = ? " +
                     "GROUP BY o.order_id " +
                     "ORDER BY o.id DESC";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, customerName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int    count     = rs.getInt("item_count");
                String firstItem = rs.getString("first_item");
                String itemSummary = count + " item" + (count != 1 ? "s" : "");
                String itemDetail  = count > 1
                    ? firstItem + " +" + (count - 1) + " more"
                    : (firstItem != null ? firstItem : "—");

                list.add(new String[]{
                    rs.getString("order_id"),
                    rs.getString("customer_name"),
                    rs.getString("customer_id"),
                    rs.getString("order_date"),
                    itemSummary,
                    itemDetail,
                    "£" + String.format("%.2f", rs.getDouble("total")),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            System.err.println("OrderDAO.getOrdersByCustomer error: " + e.getMessage());
        }
        return list;
    }

    // ── Get items for one order ───────────────────────────────
    // Returns: [product_name, unit_price, quantity, subtotal]
    public List<String[]> getOrderItems(String orderId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT product_name, unit_price, quantity, subtotal " +
                     "FROM order_items WHERE order_id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("product_name"),
                    "£" + String.format("%.2f", rs.getDouble("unit_price")),
                    String.valueOf(rs.getInt("quantity")),
                    "£" + String.format("%.2f", rs.getDouble("subtotal"))
                });
            }
        } catch (SQLException e) {
            System.err.println("OrderDAO.getOrderItems error: " + e.getMessage());
        }
        return list;
    }

    // ── Save new order + items to DB ──────────────────────────
    public boolean saveOrder(String orderId, String customerId,
                             String customerName, String date,
                             double total, Map<String, String[]> cartItems) {
        String insertOrder = "INSERT INTO orders " +
            "(order_id, customer_id, customer_name, order_date, total, status) " +
            "VALUES (?,?,?,?,?,'Processing')";
        String insertItem = "INSERT INTO order_items " +
            "(order_id, product_id, product_name, quantity, unit_price, subtotal) " +
            "VALUES (?,?,?,?,?,?)";
        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);

            // Insert order
            try (PreparedStatement ps = conn.prepareStatement(insertOrder)) {
                ps.setString(1, orderId);
                ps.setString(2, customerId);
                ps.setString(3, customerName);
                ps.setString(4, date);
                ps.setDouble(5, total);
                ps.executeUpdate();
            }

            // Insert each item
            try (PreparedStatement ps = conn.prepareStatement(insertItem)) {
                for (String[] entry : cartItems.values()) {
                    // entry: [id, name, category, price, stock, qty]
                    int    qty      = Integer.parseInt(entry[5]);
                    double price    = Double.parseDouble(entry[3]);
                    double subtotal = price * qty;
                    ps.setString(1, orderId);
                    ps.setString(2, entry[0]);
                    ps.setString(3, entry[1]);
                    ps.setInt(4, qty);
                    ps.setDouble(5, price);
                    ps.setDouble(6, subtotal);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            conn.setAutoCommit(true);
            System.out.println("OrderDAO: saved order " + orderId);
            return true;

        } catch (SQLException e) {
            System.err.println("OrderDAO.saveOrder error: " + e.getMessage());
            try { db.getConnection().setAutoCommit(true); } catch (SQLException ignored) {}
            return false;
        }
    }

    // ── Update order status ───────────────────────────────────
    public boolean updateStatus(String orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, orderId);
            int rows = ps.executeUpdate();
            System.out.println("OrderDAO: updated " + orderId + " → " + newStatus);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("OrderDAO.updateStatus error: " + e.getMessage());
            return false;
        }
    }

    // ── Generate next order ID ────────────────────────────────
    public String generateOrderId() {
        String sql = "SELECT COUNT(*) AS total FROM orders";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int next = rs.getInt("total") + 1;
                return String.format("ORD-%03d", next);
            }
        } catch (SQLException e) {
            System.err.println("OrderDAO.generateOrderId error: " + e.getMessage());
        }
        return "ORD-" + System.currentTimeMillis();
    }
}