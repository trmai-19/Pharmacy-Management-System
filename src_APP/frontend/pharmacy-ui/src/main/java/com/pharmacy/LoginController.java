package com.pharmacy;

import com.pharmacy.model.User;
import com.pharmacy.util.SceneManager;
import com.pharmacy.util.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginController {

    @FXML private AnchorPane rootPane;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;
    @FXML private Button btnTheme;

    @FXML
    void initialize() {
        ThemeService.applyTheme(rootPane);
        setupHoverEffects();
        clearError();
        Platform.runLater(() -> txtUsername.requestFocus());
    }

    private void setupHoverEffects() {
        addFieldHoverEffect(txtUsername);
        addFieldHoverEffect(txtPassword);

        String defaultBtn = "-fx-background-color: linear-gradient(to right, #0575E6, #00F260); " +
                            "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; " +
                            "-fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 4);";

        String hoverBtn = "-fx-background-color: linear-gradient(to right, #0463c7, #00d65c); " +
                          "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; " +
                          "-fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 15, 0, 0, 6);";

        btnLogin.setStyle(defaultBtn);
        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle(hoverBtn));
        btnLogin.setOnMouseExited(e -> btnLogin.setStyle(defaultBtn));
    }

    private void addFieldHoverEffect(TextInputControl field) {
        field.focusedProperty().addListener((obs, old, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-background-color: white; -fx-border-color: #0575E6; -fx-border-width: 2.5;");
            } else {
                field.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dcdcdc;");
            }
        });
    }

    @FXML
    void switchTheme(ActionEvent event) {
        ThemeService.toggleTheme(rootPane);
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập và mật khẩu!");
            return;
        }

        clearError();
        btnLogin.setDisable(true);
        btnLogin.setText("Đang đăng nhập...");

        // === HARD CODE ĐỂ TEST FRONTEND (không cần Backend) ===
        User user = authenticateHardcode(username, password);

        Platform.runLater(() -> {
            btnLogin.setDisable(false);
            btnLogin.setText("ĐĂNG NHẬP");

            if (user != null) {
                Session.setCurrentUser(user);
                navigateToDashboard(user.getRole());
            } else {
                showError("Tên đăng nhập hoặc mật khẩu không đúng!");
            }
        });
    }

    // 3 tài khoản test (Hardcode)
    private User authenticateHardcode(String username, String password) {
        String userLower = username.toLowerCase();

        if ("admin".equals(userLower) && "admin".equals(password)) {
            return new User("admin", "Nguyễn Văn A", "ADMIN");
        }
        if ("sales".equals(userLower) && "sales".equals(password)) {
            return new User("sales", "Trần Thị B", "SALES");
        }
        if ("warehouse".equals(userLower) && "warehouse".equals(password)) {
            return new User("warehouse", "Lê Văn K", "WAREHOUSE");
        }
        return null;
    }

    // ==================== SỬA LỖI SWITCH Ở ĐÂY ====================
   private void navigateToDashboard(String role) {
    String dashboardPath;

    switch (role) {
        case "ADMIN":
            dashboardPath = "/com/pharmacy/views/admin/dashboard.fxml";
            break;
        case "SALES":
            dashboardPath = "/com/pharmacy/views/sales/dashboard.fxml";
            break;
        case "WAREHOUSE":
            dashboardPath = "/com/pharmacy/views/warehouse/dashboard.fxml";
            break;
        default:
            dashboardPath = "/com/pharmacy/views/sales/dashboard.fxml";
            break;
    }

    System.out.println("🔄 Đang chuyển đến role: " + role);
    System.out.println("📁 Đường dẫn FXML: " + dashboardPath);

    try {
        SceneManager.switchScene(dashboardPath);
        System.out.println("✅ Chuyển scene thành công cho role: " + role);
    } catch (Exception e) {
        System.err.println("❌ LỖI chuyển scene cho role: " + role);
        System.err.println("Đường dẫn bị lỗi: " + dashboardPath);
        e.printStackTrace();
        showError("Không thể chuyển đến trang chính của " + role + "!");
    }
}
    // Phần này giữ nguyên để sau này dễ chuyển sang dùng API
    private void handleSuccessfulLogin(String responseBody) {
        try {
            String role = extractRole(responseBody);
            String fullName = extractFullName(responseBody, txtUsername.getText());

            User user = new User(txtUsername.getText(), fullName, role);
            Session.setCurrentUser(user);

            String dashboardPath = getDashboardPath(role);

            SceneManager.switchScene(dashboardPath);

        } catch (Exception e) {
            showError("Đăng nhập thành công nhưng không chuyển được trang!");
            e.printStackTrace();
        }
    }

    private String getDashboardPath(String role) {
        if ("ADMIN".equals(role)) {
            return "/com/pharmacy/views/admin/dashboard.fxml";
        } else if ("WAREHOUSE".equals(role)) {
            return "/com/pharmacy/views/warehouse/dashboard.fxml";
        } else {
            return "/com/pharmacy/views/sales/dashboard.fxml";
        }
    }

    private String extractRole(String json) {
        if (json == null) return "SALES";
        String upper = json.toUpperCase();
        if (upper.contains("ADMIN")) return "ADMIN";
        if (upper.contains("WAREHOUSE")) return "WAREHOUSE";
        return "SALES";
    }

    private String extractFullName(String json, String fallback) {
        return fallback;
    }

    private void showError(String message) {
        lblError.setText(message);
    }

    private void clearError() {
        lblError.setText("");
    }
}