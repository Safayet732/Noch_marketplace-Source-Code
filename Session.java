package com.noch.noch.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Session {

    // ── Logged in user: [id, name, email, role, customer_id] ──
    public static String[] loggedInUser = null;

    // ── Selected order (admin + customer detail) ──────────────
    public static String[] currentOrder = null;

    // ── All orders (persists across navigation) ───────────────
    public static ObservableList<String[]> orders = null;

    public static void initOrders() {
        if (orders != null) return;
        orders = FXCollections.observableArrayList(
            new String[]{"ORD-001","Alice Johnson",   "CUST-001","Mar 14, 2026","2 items","Classic White Tee +1 more",   "£44.98", "Processing"},
            new String[]{"ORD-002","Bob Smith",       "CUST-002","Mar 13, 2026","3 items","Slim Fit Jeans +2 more",      "£119.97","Shipped"},
            new String[]{"ORD-003","Carol Williams",  "CUST-003","Mar 12, 2026","1 item", "Floral Summer Dress",         "£54.99", "Delivered"},
            new String[]{"ORD-004","David Brown",     "CUST-004","Mar 11, 2026","2 items","Leather Jacket +1 more",      "£189.98","Cancelled"},
            new String[]{"ORD-005","Emma Davis",      "CUST-005","Mar 10, 2026","1 item", "Wool Overcoat",               "£129.99","Returned"},
            new String[]{"ORD-006","Alice Johnson",   "CUST-001","Mar 9, 2026", "2 items","Striped Polo Shirt +1 more",  "£69.98", "Delivered"},
            new String[]{"ORD-007","Bob Smith",       "CUST-002","Mar 15, 2026","1 item", "Chino Trousers",              "£49.99", "Pending"},
            new String[]{"ORD-008","Fatima Khan",     "CUST-006","Mar 8, 2026", "3 items","Hijab Set +2 more",           "£84.97", "Delivered"},
            new String[]{"ORD-009","James Wilson",    "CUST-007","Mar 7, 2026", "2 items","Oxford Button Shirt +1 more", "£89.98", "Shipped"},
            new String[]{"ORD-010","Sophia Martinez", "CUST-008","Mar 6, 2026", "1 item", "Knit Cardigan",               "£39.99", "Processing"},
            new String[]{"ORD-011","Liam O'Brien",    "CUST-009","Mar 5, 2026", "4 items","Graphic Hoodie +3 more",      "£134.96","Delivered"},
            new String[]{"ORD-012","Priya Patel",     "CUST-010","Mar 4, 2026", "2 items","Silk Blouse +1 more",         "£99.98", "Processing"},
            new String[]{"ORD-013","Noah Thompson",   "CUST-011","Mar 3, 2026", "1 item", "Tailored Blazer",             "£149.99","Shipped"},
            new String[]{"ORD-014","Isla Campbell",   "CUST-012","Mar 2, 2026", "2 items","High-Waist Joggers +1 more",  "£74.98", "Cancelled"},
            new String[]{"ORD-015","Omar Hassan",     "CUST-013","Mar 1, 2026", "3 items","Linen Shirt +2 more",         "£109.97","Delivered"}
        );
    }

    // ── Shopping cart: productId -> [id, name, category, price, stock, qty] ──
    public static Map<String, String[]> cart = new LinkedHashMap<>();

    public static void addToCart(String[] product) {
        String id = product[0];
        if (cart.containsKey(id)) {
            String[] entry = cart.get(id);
            entry[5] = String.valueOf(Integer.parseInt(entry[5]) + 1);
        } else {
            cart.put(id, new String[]{
                product[0], product[1], product[2],
                product[3], product[4], "1"
            });
        }
    }

    public static int cartTotal() {
        return cart.values().stream()
            .mapToInt(e -> Integer.parseInt(e[5])).sum();
    }

    // ── Snapshot of last confirmed order — for confirmation screen ──
    // Each entry: [name, qty, subtotal]
    public static List<String[]> lastOrderItems = new ArrayList<>();
    public static String lastOrderTotal = "£0.00";

    // Save cart snapshot BEFORE clearing
    public static void snapshotCart() {
        lastOrderItems.clear();
        double total = 0;
        for (String[] entry : cart.values()) {
            double price    = Double.parseDouble(entry[3]);
            int    qty      = Integer.parseInt(entry[5]);
            double subtotal = price * qty;
            total += subtotal;
            lastOrderItems.add(new String[]{
                entry[1],                                    // name
                String.valueOf(qty),                         // qty
                "£" + String.format("%.2f", subtotal)        // subtotal
            });
        }
        lastOrderTotal = "£" + String.format("%.2f", total);
    }

    public static void clearCart() { cart.clear(); }

    private Session() {}
}