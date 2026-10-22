package com.noch.noch;

import com.noch.noch.controllers.CustomerDAO;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("CustomerDAO Tests — Customer Lookup")
public class CustomerDAOTest {

    private static CustomerDAO customerDAO;

    @BeforeAll
    static void setup() {
        customerDAO = CustomerDAO.getInstance();
    }

    // ── TC-22: Search by name ─────────────────────────────────
    @Test
    @Order(1)
    @DisplayName("TC-22: searchCustomer finds customer by name")
    void testSearchByName() {
        String[] customer = customerDAO.searchCustomer("Alice");
        assertNotNull(customer, "Should find Alice Johnson");
        assertEquals("CUST-001",      customer[0], "Customer ID should be CUST-001");
        assertEquals("Alice Johnson", customer[1], "Name should be Alice Johnson");
        System.out.println("PASS TC-22: Found customer — " + customer[1]);
    }

    // ── TC-23: Search by customer ID ──────────────────────────
    @Test
    @Order(2)
    @DisplayName("TC-23: searchCustomer finds customer by ID")
    void testSearchById() {
        String[] customer = customerDAO.searchCustomer("CUST-002");
        assertNotNull(customer, "Should find CUST-002");
        assertEquals("Bob Smith", customer[1], "Name should be Bob Smith");
        System.out.println("PASS TC-23: Found customer by ID — " + customer[1]);
    }

    // ── TC-24: Unknown customer returns null ──────────────────
    @Test
    @Order(3)
    @DisplayName("TC-24: searchCustomer returns null for unknown customer")
    void testSearchUnknown() {
        String[] customer = customerDAO.searchCustomer("ZZZNOBODY");
        assertNull(customer, "Unknown customer should return null");
        System.out.println("PASS TC-24: Unknown customer correctly returned null");
    }

    // ── TC-25: Get all customers ──────────────────────────────
    @Test
    @Order(4)
    @DisplayName("TC-25: getAllCustomers returns 13 customers")
    void testGetAllCustomers() {
        List<String[]> customers = customerDAO.getAllCustomers();
        assertNotNull(customers, "Customers list should not be null");
        assertEquals(13, customers.size(), "Should return 13 customers");
        System.out.println("PASS TC-25: Loaded " + customers.size() + " customers");
    }

    // ── TC-26: Customer has correct fields ────────────────────
    @Test
    @Order(5)
    @DisplayName("TC-26: Customer array has 3 fields")
    void testCustomerFields() {
        List<String[]> customers = customerDAO.getAllCustomers();
        assertFalse(customers.isEmpty());
        assertEquals(3, customers.get(0).length,
            "Each customer should have 3 fields: customer_id, name, email");
        System.out.println("PASS TC-26: Customer fields correct");
    }
}