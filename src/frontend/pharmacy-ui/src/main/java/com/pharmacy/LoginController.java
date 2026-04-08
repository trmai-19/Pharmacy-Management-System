package com.pharmacy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class LoginController {

    @FXML private AnchorPane rootPane;
    @FXML private Button btnTheme;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;
    
    @FXML
    void initialize() {
        btnTheme.setText("");
        ThemeService.applyTheme(rootPane);

        addHoverEffect(txtUsername);
        addHoverEffect(txtPassword);
        addButtonHoverEffect(btnLogin);
        
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

        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            lblError.setStyle("-fx-text-fill: red; -fx-font-style: italic; -fx-font-size: 13px;");
        } else {
            lblError.setText("");
            
            // Đoạn này sau Database để check tài khoản thật
            System.out.println("Tài khoản: " + user + " | Mật khẩu: " + pass);
            
            // Test
            if (user.equals("admin") && pass.equals("123456")) {
                lblError.setText("Đăng nhập thành công!");
                lblError.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
            } else {
                lblError.setText("Sai tài khoản hoặc mật khẩu rồi ông giáo ạ!");
                lblError.setStyle("-fx-text-fill: red; -fx-font-style: italic;");
            }
        }
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
                        "-fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 2; " +
                        "-fx-text-fill: " + textColor + ";");
        });
    }

    private void addButtonHoverEffect(Button btn) {
        String defaultStyle = "-fx-background-color: linear-gradient(to right, #007bff, #00e676); " +
                            "-fx-background-radius: 20; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 16px; " + 
                            "-fx-background-insets: 0;";

        String hoverStyle = "-fx-background-color: linear-gradient(to right, #3399ff, #33ff99); " +
                            "-fx-background-radius: 20; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 16px; " + 
                            "-fx-background-insets: 0; " +
                            "-fx-cursor: hand;";

        btn.setStyle(defaultStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(defaultStyle));
    }
    
}