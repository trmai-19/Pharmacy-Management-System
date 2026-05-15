package com.pharmacy.model;

public class User {

    private String username;
    private String fullName;
    private String role;   // ADMIN, SALES, WAREHOUSE

    public User(String username, String fullName, String role) {
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName != null && !fullName.isEmpty() ? fullName : username;
    }

    public String getRole() {
        return role;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}