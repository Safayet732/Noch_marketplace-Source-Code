package com.noch.noch;

import com.noch.noch.controllers.ProductDAO;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ProductDAO Tests — Product Catalogue")
public class ProductDAOTest {

    private static ProductDAO productDAO;

    @BeforeAll
    static void setup() {
        productDAO = ProductDAO.getInstance();
    }

    // ── TC-08: All products load ──────────────────────────────
    @Test
    @Order(1)
    @DisplayName("TC-08: getAllProducts returns 21 products")
    void testGetAllProducts() {
        List<String[]> products = productDAO.getAllProducts();
        assertNotNull(products, "Products list should not be null");
        assertEquals(21, products.size(), "Should return exactly 21 products");
        System.out.println("PASS TC-08: Loaded " + products.size() + " products");
    }

    // ── TC-09: Product has correct fields ─────────────────────
    @Test
    @Order(2)
    @DisplayName("TC-09: Product array has 6 fields")
    void testProductFields() {
        List<String[]> products = productDAO.getAllProducts();
        assertFalse(products.isEmpty(), "Products should not be empty");
        String[] first = products.get(0);
        assertEquals(6, first.length, "Each product should have 6 fields: id, name, category, price, stock, image");
        System.out.println("PASS TC-09: Product fields count correct — " + first[1]);
    }

    // ── TC-10: Get product by ID ───────────────────────────────
    @Test
    @Order(3)
    @DisplayName("TC-10: getProductById returns correct product")
    void testGetProductById() {
        String[] product = productDAO.getProductById("PROD-001");
        assertNotNull(product, "PROD-001 should exist");
        assertEquals("PROD-001",         product[0], "Product ID should match");
        assertEquals("Classic White Tee", product[1], "Product name should match");
        assertEquals("T-Shirts",          product[2], "Category should match");
        System.out.println("PASS TC-10: Found product — " + product[1]);
    }

    // ── TC-11: Non-existent product returns null ──────────────
    @Test
    @Order(4)
    @DisplayName("TC-11: getProductById returns null for unknown ID")
    void testGetProductByInvalidId() {
        String[] product = productDAO.getProductById("PROD-999");
        assertNull(product, "Non-existent product should return null");
        System.out.println("PASS TC-11: Non-existent product correctly returned null");
    }

    // ── TC-12: Product price is positive ──────────────────────
    @Test
    @Order(5)
    @DisplayName("TC-12: All product prices are positive")
    void testProductPrices() {
        List<String[]> products = productDAO.getAllProducts();
        for (String[] p : products) {
            double price = Double.parseDouble(p[3]);
            assertTrue(price > 0, "Price of " + p[1] + " should be positive, got " + price);
        }
        System.out.println("PASS TC-12: All " + products.size() + " products have positive prices");
    }

    // ── TC-13: Reduce stock ───────────────────────────────────
    @Test
    @Order(6)
    @DisplayName("TC-13: reduceStock decreases stock by correct amount")
    void testReduceStock() {
        // Get current stock of PROD-006
        String[] before = productDAO.getProductById("PROD-006");
        int stockBefore = Integer.parseInt(before[4]);

        // Reduce by 1
        productDAO.reduceStock("PROD-006", 1);

        // Check it decreased
        String[] after = productDAO.getProductById("PROD-006");
        int stockAfter = Integer.parseInt(after[4]);

        assertEquals(stockBefore - 1, stockAfter, "Stock should have decreased by 1");

        // Restore stock for next test run
        productDAO.reduceStock("PROD-006", -1); // add back

        System.out.println("PASS TC-13: Stock reduced from " + stockBefore + " to " + stockAfter);
    }

    // ── TC-14: Stock never goes negative ──────────────────────
    @Test
    @Order(7)
    @DisplayName("TC-14: Stock cannot go below zero")
    void testStockNeverNegative() {
        // PROD-018 has stock = 0 (Cropped Sweatshirt)
        productDAO.reduceStock("PROD-018", 999);
        String[] product = productDAO.getProductById("PROD-018");
        int stock = Integer.parseInt(product[4]);
        assertTrue(stock >= 0, "Stock should never be negative, got " + stock);
        System.out.println("PASS TC-14: Stock correctly stayed at " + stock + " (not negative)");
    }
}