package com.pharmacy.controller.common;

import java.io.IOException;

import com.pharmacy.model.User;
import com.pharmacy.util.SceneManager;
import com.pharmacy.util.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private AnchorPane rootPane;
    @FXML private VBox leftPane;     
    @FXML private VBox rightPane; 
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;

    @FXML
    void initialize() {
        // --- 1. SỬA LỖI KHÔNG FULL MÀN HÌNH ---
        // Ràng buộc chiều rộng theo tỷ lệ cửa sổ (Left 45%, Right 55%)
        leftPane.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.45));
        rightPane.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.55));

        applyStaticStyle();
        
        clearError();
        
        Platform.runLater(() -> btnLogin.requestFocus());
    }

    private void applyStaticStyle() {
        // Style cho TextField & PasswordField (Glassmorphism)
        String inputStyle = "-fx-background-color: rgba(15, 23, 42, 0.6); " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 8; " +
                            "-fx-border-color: #334155; " +
                            "-fx-border-radius: 8; " +
                            "-fx-prompt-text-fill: #777777; " +
                            "-fx-font-size: 15px;";
        
        txtUsername.setStyle(inputStyle);
        txtPassword.setStyle(inputStyle);

        // Hiệu ứng Focus
        setupInputFocus(txtUsername, inputStyle);
        setupInputFocus(txtPassword, inputStyle);

        // Style cho Nút đăng nhập
        String btnStyle = "-fx-background-color: linear-gradient(to right, #00ff9d, #059669); " +
                          "-fx-text-fill: #064e3b; " +
                          "-fx-font-weight: 800; " +
                          "-fx-background-radius: 8; " +
                          "-fx-cursor: hand; " +
                          "-fx-font-size: 18px;";
        btnLogin.setStyle(btnStyle);
        
        // Hiệu ứng Hover nút
        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle(btnStyle + "-fx-translate-y: -2; -fx-effect: dropshadow(three-pass-box, rgba(0,255,157,0.4), 15, 0, 0, 0);"));
        btnLogin.setOnMouseExited(e -> btnLogin.setStyle(btnStyle));
    }

    private void setupInputFocus(TextInputControl field, String baseStyle) {
        field.focusedProperty().addListener((obs, old, newVal) -> {
            if (newVal) {
                field.setStyle(baseStyle + "-fx-border-color: #00ff9d; -fx-border-width: 2;");
            } else {
                field.setStyle(baseStyle);
            }
        });
    }

    @FXML
    void handleGoToForgotPass(ActionEvent event) {
        try {
            // Chỉ truyền đúng tên file FXML, không cần truyền Stage nữa
            // SỬA ĐÚNG TÊN FILE VÀO ĐÂY NHÉ:
            SceneManager.switchScene("/com/pharmacy/views/ForgotPassword.fxml");
        } catch (IOException e) {
            // In ra lỗi nếu không tìm thấy file để dễ gỡ rối
            System.err.println("Lỗi khi chuyển sang màn hình Quên mật khẩu!");
            e.printStackTrace();
        }
    }
    @FXML
    void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if(username.isEmpty() && password.isEmpty()) {
            showError("Vui lòng nhập thông tin đăng nhập!");
            return;
        }
        if (username.isEmpty() ) {
            showError("Vui lòng nhập tài khoản!");
            return;
        }
        if (password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu!");
            return;
        }

        clearError();
        btnLogin.setDisable(true);
        btnLogin.setText("ĐANG XỬ LÝ...");

        // Giả lập logic đăng nhập
        if ("1".equals(username.toLowerCase()) && "1".equals(password)) {
            navigateToDashboard("ADMIN");
        } else {
            showError("Tài khoản hoặc mật khẩu không đúng!");
            btnLogin.setDisable(false);
            btnLogin.setText("VÀO HỆ THỐNG");
        }
    }

    private void navigateToDashboard(String role) {
        String path = "";
        String roleLower = role.toLowerCase();

        // Xác định đúng file FXML tổng (Main Dashboard) của từng bộ phận
        switch (roleLower) {
            case "admin":
                // Theo ảnh thư mục của bạn: resources/com/pharmacy/views/admin/admin-main.fxml
                path = "/com/pharmacy/views/admin/admin-main.fxml"; 
                break;
            case "sales":
                path = "/com/pharmacy/views/sales/dashboard.fxml";
                break;
            case "warehouse":
                path = "/com/pharmacy/views/warehouse/dashboard.fxml";
                break;
            default:
                path = "/com/pharmacy/views/login.fxml";
        }

        try {
            System.out.println("🚀 Đang chuyển đến: " + path);
            SceneManager.switchScene(path);
        } catch (Exception e) {
            System.err.println("❌ Lỗi chuyển trang cho role: " + role);
            e.printStackTrace();
            showError("Không tìm thấy giao diện cho quyền: " + role);
        }
}

    private void showError(String message) { lblError.setText(message); }
    private void clearError() { lblError.setText(""); }
}