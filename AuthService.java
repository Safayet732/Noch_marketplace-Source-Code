package com.noch.noch.controllers;

public class AuthService {

    private static AuthService instance;
    private String currentUserType = null;

    private static final String STAFF_EMAIL       = "admin@noch.com";
    private static final String STAFF_PASSWORD    = "staff123";
    private static final String CUSTOMER_EMAIL    = "customer@gmail.com";
    private static final String CUSTOMER_PASSWORD = "customer123";

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    public String validateEmail(String email, String userType) {
        if (email == null || email.isBlank()) return "";
        String lower = email.toLowerCase().trim();
        if ("staff".equals(userType)) {
            if (!lower.contains("@noch.com"))
                return "Staff email must use a @noch.com address.";
        } else {
            int atIndex  = lower.indexOf('@');
            int dotIndex = lower.lastIndexOf('.');
            boolean valid = atIndex > 0
                    && dotIndex > atIndex + 1
                    && dotIndex < lower.length() - 1
                    && lower.endsWith(".com")
                    && !lower.contains(" ");
            if (!valid) return "Please enter a valid email address.";
        }
        return "";
    }

    public boolean login(String email, String password, String userType) {
        if (email == null || password == null) return false;
        String e = email.trim();
        String p = password.trim();
        if ("staff".equals(userType)) {
            if (e.equalsIgnoreCase(STAFF_EMAIL) && p.equals(STAFF_PASSWORD)) {
                currentUserType = "staff";
                return true;
            }
        } else {
            if (e.equalsIgnoreCase(CUSTOMER_EMAIL) && p.equals(CUSTOMER_PASSWORD)) {
                currentUserType = "customer";
                return true;
            }
        }
        return false;
    }

    public void logout()                      { currentUserType = null; }
    public String getCurrentUserType()        { return currentUserType; }
    public boolean isLoggedIn()               { return currentUserType != null; }
}