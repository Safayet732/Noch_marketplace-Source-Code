package com.noch.noch;

import com.noch.noch.controllers.ReturnDAO;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ReturnDAO Tests — Return Management")
public class ReturnDAOTest {

    private static ReturnDAO returnDAO;

    @BeforeAll
    static void setup() {
        returnDAO = ReturnDAO.getInstance();
    }

    // ── TC-27: All returns load ───────────────────────────────
    @Test
    @Order(1)
    @DisplayName("TC-27: getAllReturns returns at least 5 returns")
    void testGetAllReturns() {
        ObservableList<String[]> returns = returnDAO.getAllReturns();
        assertNotNull(returns, "Returns list should not be null");
        assertTrue(returns.size() >= 5, "Should have at least 5 seed returns");
        System.out.println("PASS TC-27: Loaded " + returns.size() + " returns");
    }

    // ── TC-28: Return has 7 fields ────────────────────────────
    @Test
    @Order(2)
    @DisplayName("TC-28: Return array has 7 fields")
    void testReturnFields() {
        ObservableList<String[]> returns = returnDAO.getAllReturns();
        assertFalse(returns.isEmpty());
        assertEquals(7, returns.get(0).length,
            "Each return should have 7 fields: returnId, orderId, customerName, customerId, items, date, status");
        System.out.println("PASS TC-28: Return fields correct");
    }

    // ── TC-29: Save new return ────────────────────────────────
    @Test
    @Order(3)
    @DisplayName("TC-29: saveReturn inserts a new return request")
    void testSaveReturn() {
        String testReturnId = "RET-TEST-001";
        String date = LocalDate.now().toString();

        boolean saved = returnDAO.saveReturn(
            testReturnId, "ORD-003", "CUST-003",
            "Carol Williams", "Floral Summer Dress x1",
            "Wrong size", date
        );
        assertTrue(saved, "saveReturn should return true");

        // Verify it appears in getAllReturns
        ObservableList<String[]> returns = returnDAO.getAllReturns();
        boolean found = returns.stream().anyMatch(r -> r[0].equals(testReturnId));
        assertTrue(found, "New return should appear in getAllReturns");

        System.out.println("PASS TC-29: Return request saved — " + testReturnId);

        // Cleanup
        try {
            com.noch.noch.controllers.DatabaseManager.getInstance()
                .getConnection()
                .createStatement()
                .execute("DELETE FROM returns WHERE return_id = '" + testReturnId + "'");
        } catch (Exception ignored) {}
    }

    // ── TC-30: Update return status ───────────────────────────
    @Test
    @Order(4)
    @DisplayName("TC-30: updateStatus changes return status")
    void testUpdateStatus() {
        // RET-001 starts as Pending — change to Approved
        boolean result = returnDAO.updateStatus("RET-001", "Approved");
        assertTrue(result, "updateStatus should return true");

        // Verify
        ObservableList<String[]> returns = returnDAO.getAllReturns();
        String[] ret001 = returns.stream()
            .filter(r -> r[0].equals("RET-001"))
            .findFirst()
            .orElse(null);
        assertNotNull(ret001);
        assertEquals("Approved", ret001[6], "Status should be Approved");

        // Restore
        returnDAO.updateStatus("RET-001", "Pending");
        System.out.println("PASS TC-30: Return status updated successfully");
    }

    // ── TC-31: Generate return ID ─────────────────────────────
    @Test
    @Order(5)
    @DisplayName("TC-31: generateReturnId returns valid format RET-XXX")
    void testGenerateReturnId() {
        String id = returnDAO.generateReturnId();
        assertNotNull(id, "Generated ID should not be null");
        assertTrue(id.startsWith("RET-"), "ID should start with RET-");
        System.out.println("PASS TC-31: Generated ID — " + id);
    }
}