package com.pharmacy.util;

import com.pharmacy.model.User;

public class Session {

    private static User currentUser = null;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public static String getFullName() {
        return currentUser != null ? currentUser.getFullName() : "Unknown User";
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }

    // Xóa session khi cần reset
    public static void clear() {
        currentUser = null;
    }
}