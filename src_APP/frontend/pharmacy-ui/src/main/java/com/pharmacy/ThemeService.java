package com.pharmacy;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class ThemeService {
    private static boolean isDarkMode = false;

    public static boolean isDarkMode() { return isDarkMode; }

    public static void applyTheme(Parent root) {
        String bgColor = isDarkMode ? "#121212" : "white";
        String fieldBg = isDarkMode ? "#121212" : "white";
        String textColor = isDarkMode ? "white" : "black";

        if (root instanceof AnchorPane) {
            root.setStyle("-fx-background-color: " + bgColor + ";");
        }
        
        for (Node node : root.lookupAll(".text-field")) {
            node.setStyle("-fx-background-color: " + fieldBg + "; " +
                          "-fx-text-fill: " + textColor + "; " +
                          "-fx-border-color: #d1d1d1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 1;");
        }

        for (Node node : root.lookupAll(".hyperlink")) {
            node.setStyle("-fx-text-fill: " + (isDarkMode ? "#81d4fa" : "#0056b3") + ";");
        }

        Node btnTheme = root.lookup("#btnTheme");
        if (btnTheme instanceof Button) {
            if (isDarkMode) {
                btnTheme.setStyle("-fx-background-color: #28a745, white; -fx-background-insets: 0, 3 3 3 28; -fx-background-radius: 30; -fx-cursor: hand;");
            } else {
                btnTheme.setStyle("-fx-background-color: #d1d1d1, white; -fx-background-insets: 0, 3 28 3 3; -fx-background-radius: 30; -fx-cursor: hand;");
            }
        }
    }

    public static void toggleTheme(Parent root) {
        isDarkMode = !isDarkMode;
        applyTheme(root);
    }
}