package com.noch.noch;

import com.noch.noch.controllers.UserDAO;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UserDAO Tests — Login Authentication")
public class UserDAOTest {

    private static UserDAO userDAO;

    @BeforeAll
    static void setup() {
        userDAO = UserDAO.getInstance();
    }

    // ── TC-01: Valid staff login ──────────────────────────────
    @Test
    @Order(1)
    @DisplayName("TC-01: Valid staff login returns user data")
    void testValidStaffLogin() {
        String[] result = userDAO.login("admin@noch.com", "staff123", "staff");
        assertNotNull(result, "Login should return a user array, not null");
        assertEquals("Admin User", result[1], "Name should be Admin User");
        assertEquals("staff", result[3], "Role should be staff");
        System.out.println("PASS TC-01: Staff login successful — " + result[1]);
    }

    // ── TC-02: Valid customer login ───────────────────────────
    @Test
    @Order(2)
    @DisplayName("TC-02: Valid customer login returns user data")
    void testValidCustomerLogin() {
        String[] result = userDAO.login("customer@gmail.com", "customer123", "customer");
        assertNotNull(result, "Login should return a user array, not null");
        assertEquals("Alice Johnson", result[1], "Name should be Alice Johnson");
        assertEquals("customer", result[3], "Role should be customer");
        assertEquals("CUST-001", result[4], "Customer ID should be CUST-001");
        System.out.println("PASS TC-02: Customer login successful — " + result[1]);
    }

    // ── TC-03: Wrong password returns null ────────────────────
    @Test
    @Order(3)
    @DisplayName("TC-03: Wrong password returns null")
    void testWrongPassword() {
        String[] result = userDAO.login("admin@noch.com", "wrongpassword", "staff");
        assertNull(result, "Wrong password should return null");
        System.out.println("PASS TC-03: Wrong password correctly rejected");
    }

    // ── TC-04: Wrong email returns null ───────────────────────
    @Test
    @Order(4)
    @DisplayName("TC-04: Wrong email returns null")
    void testWrongEmail() {
        String[] result = userDAO.login("nobody@noch.com", "staff123", "staff");
        assertNull(result, "Wrong email should return null");
        System.out.println("PASS TC-04: Wrong email correctly rejected");
    }

    // ── TC-05: Wrong role returns null ────────────────────────
    @Test
    @Order(5)
    @DisplayName("TC-05: Correct credentials but wrong role returns null")
    void testWrongRole() {
        // staff credentials used with customer role
        String[] result = userDAO.login("admin@noch.com", "staff123", "customer");
        assertNull(result, "Wrong role should return null");
        System.out.println("PASS TC-05: Wrong role correctly rejected");
    }

    // ── TC-06: Empty email returns null ──────────────────────
    @Test
    @Order(6)
    @DisplayName("TC-06: Empty email returns null")
    void testEmptyEmail() {
        String[] result = userDAO.login("", "staff123", "staff");
        assertNull(result, "Empty email should return null");
        System.out.println("PASS TC-06: Empty email correctly rejected");
    }

    // ── TC-07: Case insensitive email ─────────────────────────
    @Test
    @Order(7)
    @DisplayName("TC-07: Email is case insensitive")
    void testCaseInsensitiveEmail() {
        String[] result = userDAO.login("ADMIN@NOCH.COM", "staff123", "staff");
        assertNotNull(result, "Login should work regardless of email case");
        System.out.println("PASS TC-07: Case insensitive login works");
    }
}