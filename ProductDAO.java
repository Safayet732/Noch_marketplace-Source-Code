package com.noch.noch.controllers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private static ProductDAO instance;
    private final DatabaseManager db = DatabaseManager.getInstance();

    private ProductDAO() {}

    public static ProductDAO getInstance() {
        if (instance == null) instance = new ProductDAO();
        return instance;
    }

    // ── Get all products ──────────────────────────────────────
    // Returns: List of [product_id, name, category, price, stock, image_file]
    public List<String[]> getAllProducts() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT product_id, name, category, price, stock, image_file " +
                     "FROM products ORDER BY category, name";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("product_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    String.valueOf(rs.getDouble("price")),
                    String.valueOf(rs.getInt("stock")),
                    rs.getString("image_file") != null
                        ? rs.getString("image_file") : ""
                });
            }
            System.out.println("ProductDAO: loaded " + list.size() + " products");
        } catch (SQLException e) {
            System.err.println("ProductDAO.getAllProducts error: " + e.getMessage());
        }
        return list;
    }

    // ── Reduce stock after order confirmed ────────────────────
    public void reduceStock(String productId, int quantity) {
        String sql = "UPDATE products SET stock = MAX(0, stock - ?) " +
                     "WHERE product_id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setString(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ProductDAO.reduceStock error: " + e.getMessage());
        }
    }

    // ── Get single product by ID ──────────────────────────────
    public String[] getProductById(String productId) {
        String sql = "SELECT product_id, name, category, price, stock, image_file " +
                     "FROM products WHERE product_id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                    rs.getString("product_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    String.valueOf(rs.getDouble("price")),
                    String.valueOf(rs.getInt("stock")),
                    rs.getString("image_file") != null
                        ? rs.getString("image_file") : ""
                };
            }
        } catch (SQLException e) {
            System.err.println("ProductDAO.getProductById error: " + e.getMessage());
        }
        return null;
    }
}