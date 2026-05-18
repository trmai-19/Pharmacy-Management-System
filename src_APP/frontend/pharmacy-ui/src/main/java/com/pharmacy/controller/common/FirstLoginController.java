package com.pharmacy.controller.common;

import com.pharmacy.util.ApiService;
import com.pharmacy.util.SceneManager;
import com.pharmacy.util.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

public class FirstLoginController {

    @FXML private PasswordField txtNewPass;
    @FXML private Button btnSubmit;

    @FXML
    public void initialize() {
        // Tự động focus vào ô nhập mật khẩu khi mở màn hình
        Platform.runLater(() -> txtNewPass.requestFocus());
    }

    @FXML
    void handleConfirm(ActionEvent event) {
        String newPass = txtNewPass.getText().trim();

        if (newPass.isEmpty() || newPass.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập mật khẩu mới ít nhất 6 ký tự!");
            return;
        }

        btnSubmit.setDisable(true);
        btnSubmit.setText("ĐANG XỬ LÝ...");

        // Cấu trúc JSON phải khớp với trường "newPassword" của FirstLoginChangePasswordRequest
        String jsonBody = String.format("{\"newPassword\": \"%s\"}", newPass);

        // Gọi API với Token đã có sẵn trong Session từ lúc đăng nhập thành công
        ApiService.post("/api/password/first-login-change", jsonBody).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    showAlert(Alert.AlertType.INFORMATION, "Kích hoạt thành công", "Tài khoản của bạn đã được thiết lập mật khẩu an toàn!");
                    // Kích hoạt xong thì tự động đẩy vào Dashboard dựa theo Role
                    navigateToDashboard(Session.getRole());
                } else {
                    btnSubmit.setDisable(false);
                    btnSubmit.setText("XÁC NHẬN VÀ KÍCH HOẠT");
                    showAlert(Alert.AlertType.ERROR, "Lỗi kích hoạt", "Đổi mật khẩu thất bại. Mã lỗi: " + response.statusCode());
                }
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                btnSubmit.setDisable(false);
                btnSubmit.setText("XÁC NHẬN VÀ KÍCH HOẠT");
                showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể kết nối đến máy chủ Backend!");
            });
            return null;
        });
    }

    private void navigateToDashboard(String role) {
        if (role == null) {
            System.err.println("❌ Role bị null, không thể chuyển trang!");
            return;
        }
        
        String path = "";
        switch (role.toLowerCase()) {
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
            SceneManager.switchScene(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}