package com.pharmacy.controller.common;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmacy.model.User;
import com.pharmacy.util.ApiService;
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
        leftPane.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.45));
        rightPane.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.55));

        applyStaticStyle();
        clearError();
        Platform.runLater(() -> btnLogin.requestFocus());
    }

    private void applyStaticStyle() {
        String inputStyle = "-fx-background-color: rgba(15, 23, 42, 0.6); " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 8; " +
                            "-fx-border-color: #334155; " +
                            "-fx-border-radius: 8; " +
                            "-fx-prompt-text-fill: #777777; " +
                            "-fx-font-size: 15px;";
        
        txtUsername.setStyle(inputStyle);
        txtPassword.setStyle(inputStyle);

        setupInputFocus(txtUsername, inputStyle);
        setupInputFocus(txtPassword, inputStyle);

        String btnStyle = "-fx-background-color: linear-gradient(to right, #00ff9d, #059669); " +
                          "-fx-text-fill: #064e3b; " +
                          "-fx-font-weight: 800; " +
                          "-fx-background-radius: 8; " +
                          "-fx-cursor: hand; " +
                          "-fx-font-size: 18px;";
        btnLogin.setStyle(btnStyle);
        
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
            SceneManager.switchScene("/com/pharmacy/views/ForgotPassword.fxml");
        } catch (IOException e) {
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
        if (username.isEmpty()) {
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

        String jsonBody = String.format("{\"sdt\": \"%s\", \"password\": \"%s\"}", username, password);

        // ĐÃ DÙNG APISERVICE
        ApiService.postPublic("/api/login", jsonBody).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode rootNode = ApiService.mapper.readTree(response.body());
                        JsonNode dataNode = rootNode.has("data") ? rootNode.get("data") : rootNode;
                        
                        String vaitro = dataNode.get("vaitro").asText();
                        String token = dataNode.get("token").asText();
                        String sdt = txtUsername.getText().trim();
                        
                        String hotenBackend = (dataNode.has("hoten") && !dataNode.get("hoten").isNull()) 
                                            ? dataNode.get("hoten").asText().trim() 
                                            : "";
                        
                        String fullName;
                        if (hotenBackend.isEmpty()) {
                            String prefix = sdt.substring(0, Math.min(sdt.length(), 5)); 
                            fullName = "user" + prefix;
                        } else {
                            fullName = hotenBackend;
                        }
                        
                        Session.setToken(token);
                        Session.setCurrentUser(new User(sdt, fullName, vaitro));
                        
                        boolean isFirstLogin = dataNode.has("firstLogin") && dataNode.get("firstLogin").asBoolean();
                        
                        if (isFirstLogin) {
                            System.out.println("Tài khoản đăng nhập lần đầu - Chuyển sang trang kích hoạt");
                            SceneManager.switchScene("/com/pharmacy/views/FirstLogin.fxml");
                        } else {
                            navigateToDashboard(vaitro);
                        }
                        
                    } catch (Exception e) {
                        showError("Lỗi đọc dữ liệu từ server!");
                        resetLoginButton();
                    }
                } else {
                    showError("Tài khoản hoặc mật khẩu không chính xác!");
                    resetLoginButton();
                }
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                showError("Lỗi kết nối máy chủ backend!");
                resetLoginButton();
            });
            return null;
        });
    }

    private void navigateToDashboard(String role) {
        String path = "";
        String roleLower = role.toLowerCase();

        switch (roleLower) {
            case "admin":
                path = "/com/pharmacy/views/admin/admin-main.fxml"; 
                break;
            case "sales_staff":
                path = "/com/pharmacy/views/sales/sales-main.fxml";
                break;
            case "warehouse_staff":
                path = "/com/pharmacy/views/warehouse/warehouse-main.fxml";
                break;
            default:
                path = "/com/pharmacy/views/login.fxml";
        }

        try {
            System.out.println("Đang chuyển đến: " + path);
            SceneManager.switchScene(path);
        } catch (Exception e) {
            System.err.println("Lỗi chuyển trang cho role: " + role);
            e.printStackTrace();
            showError("Không tìm thấy giao diện cho quyền: " + role);
            resetLoginButton();
        }
    }

    private void resetLoginButton() {
        btnLogin.setDisable(false);
        btnLogin.setText("VÀO HỆ THỐNG");
    }

    private void showError(String message) { lblError.setText(message); }
    private void clearError() { lblError.setText(""); }
}