package com.pharmacy;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ThemeService {

    private static boolean isDarkMode = true;  // Mặc định bắt đầu bằng Dark Mode

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    public static void applyTheme(Parent root) {
        if (root == null) return;

        String bgColor = isDarkMode ? "#0f0f0f" : "#f8f9fa";           // Nền chính
        String cardBg = isDarkMode ? "#16161e" : "#ffffff";            // Nền form đăng nhập
        String textColor = isDarkMode ? "#e0e0e0" : "#2c3e50";         // Text chính
        String labelColor = isDarkMode ? "#aaaaaa" : "#555555";        // Label phụ
        String accentColor = "#00ff9d";                                // Màu xanh ngọc nổi bật

        // Áp dụng cho root (AnchorPane chính)
        if (root instanceof AnchorPane) {
            root.setStyle("-fx-background-color: " + bgColor + ";");
        }

        // Áp dụng cho Left Branding Panel
        Node leftPane = root.lookup("#leftPane");
        if (leftPane instanceof VBox) {
            leftPane.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, " + bgColor + ");");
        }

        // Áp dụng cho Right Login Form
        for (Node node : root.lookupAll("VBox")) {
            VBox vbox = (VBox) node;
            if (vbox.getStyleClass().contains("login-form") || vbox.getParent() instanceof AnchorPane) {
                vbox.setStyle("-fx-background-color: " + cardBg + "; -fx-background-radius: 20 0 0 20;");
            }
        }

        // TextField & PasswordField
        for (Node node : root.lookupAll(".text-field, .password-field")) {
            if (node instanceof TextField || node instanceof PasswordField) {
                node.setStyle("-fx-background-color: " + (isDarkMode ? "#1f1f2e" : "#ffffff") + ";" +
                              "-fx-text-fill: " + textColor + ";" +
                              "-fx-prompt-text-fill: " + (isDarkMode ? "#777777" : "#999999") + ";" +
                              "-fx-background-radius: 12;" +
                              "-fx-border-radius: 12;" +
                              "-fx-border-color: " + (isDarkMode ? "#333344" : "#d1d1d1") + ";" +
                              "-fx-font-size: 16px;");
            }
        }

        // Labels
        for (Node node : root.lookupAll("Label")) {
            Label label = (Label) node;
            if (label.getText().equals("ĐĂNG NHẬP") || label.getStyle().contains("bold")) {
                label.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
            } else if (!label.getText().isEmpty()) {
                label.setStyle("-fx-text-fill: " + labelColor + ";");
            }
        }

        // Slogan Label (làm nổi bật)
        for (Node node : root.lookupAll("Label")) {
            Label lbl = (Label) node;
            if (lbl.getText().contains("HIỆN ĐẠI") || lbl.getText().contains("HIỆU QUẢ")) {
                lbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-letter-spacing: 2px;" +
                             "-fx-text-fill: " + accentColor + ";" +
                             "-fx-background-color: " + (isDarkMode ? "rgba(0,255,157,0.15)" : "rgba(0,200,120,0.1)") + ";" +
                             "-fx-padding: 8 25; -fx-background-radius: 25;");
            }
        }

        // Login Button
        Button btnLogin = (Button) root.lookup("#btnLogin");
        if (btnLogin != null) {
            btnLogin.setStyle("-fx-background-color: linear-gradient(to right, #00ff9d, #00cc7a);" +
                              "-fx-text-fill: #0f0f0f; -fx-font-size: 18px; -fx-font-weight: bold;" +
                              "-fx-background-radius: 12; -fx-cursor: hand;");
        }

        // Theme Button
        Button btnTheme = (Button) root.lookup("#btnTheme");
        if (btnTheme != null) {
            btnTheme.setText(isDarkMode ? "☀️" : "🌙");
            btnTheme.setStyle("-fx-background-color: transparent; -fx-text-fill: " + 
                             (isDarkMode ? "#aaaaaa" : "#555555") + "; -fx-font-size: 24px;");
        }

        // Error Label
        Label lblError = (Label) root.lookup("#lblError");
        if (lblError != null) {
            lblError.setStyle("-fx-text-fill: #ff5252; -fx-font-size: 14px; -fx-font-style: italic;");
        }
    }

    public static void toggleTheme(Parent root) {
        isDarkMode = !isDarkMode;
        applyTheme(root);
    }
}