package com.noch.noch;

import com.noch.noch.controllers.OrderDAO;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("OrderDAO Tests — Order Management")
public class OrderDAOTest {

    private static OrderDAO orderDAO;

    @BeforeAll
    static void setup() {
        orderDAO = OrderDAO.getInstance();
    }

    // ── TC-15: All orders load ────────────────────────────────
    @Test
    @Order(1)
    @DisplayName("TC-15: getAllOrders returns at least 15 orders")
    void testGetAllOrders() {
        ObservableList<String[]> orders = orderDAO.getAllOrders();
        assertNotNull(orders, "Orders list should not be null");
        assertTrue(orders.size() >= 15, "Should have at least 15 seed orders, got " + orders.size());
        System.out.println("PASS TC-15: Loaded " + orders.size() + " orders");
    }

    // ── TC-16: Order has 8 fields ─────────────────────────────
    @Test
    @Order(2)
    @DisplayName("TC-16: Order array has 8 fields")
    void testOrderFields() {
        ObservableList<String[]> orders = orderDAO.getAllOrders();
        assertFalse(orders.isEmpty(), "Orders list should not be empty");
        String[] order = orders.get(0);
        assertEquals(8, order.length,
            "Each order should have 8 fields: orderId, name, custId, date, itemCount, firstItem, total, status");
        System.out.println("PASS TC-16: Order fields correct — " + order[0]);
    }

    // ── TC-17: Get orders by customer ─────────────────────────
    @Test
    @Order(3)
    @DisplayName("TC-17: getOrdersByCustomer returns only that customer's orders")
    void testGetOrdersByCustomer() {
        ObservableList<String[]> orders = orderDAO.getOrdersByCustomer("Alice Johnson");
        assertNotNull(orders, "Orders list should not be null");
        assertTrue(orders.size() >= 2, "Alice should have at least 2 orders");
        for (String[] order : orders) {
            assertEquals("Alice Johnson", order[1], "All orders should belong to Alice Johnson");
        }
        System.out.println("PASS TC-17: Found " + orders.size() + " orders for Alice Johnson");
    }

    // ── TC-18: Get order items ────────────────────────────────
    @Test
    @Order(4)
    @DisplayName("TC-18: getOrderItems returns items for ORD-001")
    void testGetOrderItems() {
        List<String[]> items = orderDAO.getOrderItems("ORD-001");
        assertNotNull(items, "Items list should not be null");
        assertFalse(items.isEmpty(), "ORD-001 should have items");
        // Each item has 4 fields: name, unit_price, quantity, subtotal
        assertEquals(4, items.get(0).length, "Each item should have 4 fields");
        System.out.println("PASS TC-18: ORD-001 has " + items.size() + " item(s)");
    }

    // ── TC-19: Update order status ────────────────────────────
    @Test
    @Order(5)
    @DisplayName("TC-19: updateStatus changes order status in database")
    void testUpdateStatus() {
        // Change ORD-007 (Pending) to Shipped
        boolean result = orderDAO.updateStatus("ORD-007", "Shipped");
        assertTrue(result, "updateStatus should return true on success");

        // Verify it changed
        ObservableList<String[]> orders = orderDAO.getAllOrders();
        String[] ord007 = orders.stream()
            .filter(o -> o[0].equals("ORD-007"))
            .findFirst()
            .orElse(null);
        assertNotNull(ord007, "ORD-007 should exist");
        assertEquals("Shipped", ord007[7], "Status should now be Shipped");

        // Restore
        orderDAO.updateStatus("ORD-007", "Pending");
        System.out.println("PASS TC-19: Status update successful");
    }

    // ── TC-20: Save new order ─────────────────────────────────
    @Test
    @Order(6)
    @DisplayName("TC-20: saveOrder inserts new order and items into database")
    void testSaveOrder() {
        String testOrderId = "ORD-TEST-001";
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy"));

        // Build a test cart
        Map<String, String[]> cart = new LinkedHashMap<>();
        cart.put("PROD-001", new String[]{"PROD-001","Classic White Tee","T-Shirts","19.99","120","2"});

        boolean saved = orderDAO.saveOrder(
            testOrderId, "CUST-001", "Alice Johnson", date, 39.98, cart
        );
        assertTrue(saved, "saveOrder should return true");

        // Verify it exists
        List<String[]> items = orderDAO.getOrderItems(testOrderId);
        assertFalse(items.isEmpty(), "New order should have items");
        assertEquals("Classic White Tee", items.get(0)[0], "Item name should match");

        System.out.println("PASS TC-20: New order saved — " + testOrderId);

        // Cleanup — delete test order from DB
        try {
            com.noch.noch.controllers.DatabaseManager.getInstance()
                .getConnection()
                .createStatement()
                .execute("DELETE FROM orders WHERE order_id = '" + testOrderId + "'");
            com.noch.noch.controllers.DatabaseManager.getInstance()
                .getConnection()
                .createStatement()
                .execute("DELETE FROM order_items WHERE order_id = '" + testOrderId + "'");
        } catch (Exception ignored) {}
    }

    // ── TC-21: Generate order ID ──────────────────────────────
    @Test
    @Order(7)
    @DisplayName("TC-21: generateOrderId returns valid format ORD-XXX")
    void testGenerateOrderId() {
        String id = orderDAO.generateOrderId();
        assertNotNull(id, "Generated ID should not be null");
        assertTrue(id.startsWith("ORD-"), "ID should start with ORD-");
        System.out.println("PASS TC-21: Generated ID — " + id);
    }
}