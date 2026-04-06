package com.pharmacy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class LoginController {

    @FXML private AnchorPane rootPane;
    @FXML private Button btnTheme;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;

    @FXML
    void initialize() {
        btnTheme.setText("");
        ThemeService.applyTheme(rootPane);

        addHoverEffect(txtUsername);
        addHoverEffect(txtPassword);

        javafx.application.Platform.runLater(() -> rootPane.requestFocus());
    }

    @FXML
    void switchTheme(ActionEvent event) {
        ThemeService.toggleTheme(rootPane);
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();
        System.out.println("Tài khoản: " + user + " | Mật khẩu: " + pass);
    }

    private void addHoverEffect(TextField field)
    {
        field.setOnMouseEntered(e -> {

            String bgColor = ThemeService.isDarkMode() ? "#121212" : "white";
            String textColor = ThemeService.isDarkMode() ? "white" : "black";
            
            field.setStyle("-fx-background-color: " + bgColor + "; " +
                        "-fx-border-color: #28a745; " +
                        "-fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 2; " +
                        "-fx-text-fill: " + textColor + ";");
        });

        field.setOnMouseExited(e -> {
            String bgColor = ThemeService.isDarkMode() ? "#121212" : "white";
            String textColor = ThemeService.isDarkMode() ? "white" : "black";
            
            field.setStyle("-fx-background-color: " + bgColor + "; " +
                        "-fx-border-color: #d1d1d1; " +
                        "-fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 1; " +
                        "-fx-text-fill: " + textColor + ";");
        });
    }
    
}