package com.pharmacy.util;

import com.pharmacy.model.User;

public class Session {
    private static User currentUser = null;
    private static String token = null;

    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }

    public static void setToken(String jwtToken) { token = jwtToken; }
    public static String getToken() { return token; } 

    public static String getRole() { return currentUser != null ? currentUser.getRole() : null; }
    public static String getFullName() { return currentUser != null ? currentUser.getFullName() : "Unknown User"; }
    
    public static boolean isLoggedIn() { return currentUser != null && token != null; }

    public static void logout() {
        currentUser = null;
        token = null;
    }

    public static void clear() {
        currentUser = null;
        token = null;
    }
}