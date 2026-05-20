package com.pharmacy.controller.admin;

import com.pharmacy.util.ApiService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

public class ChangePassController {

    @FXML private PasswordField txtOldPass;
    @FXML private PasswordField txtNewPass;
    @FXML private PasswordField txtConfirmPass;

    @FXML
    public void initialize() {
        System.out.println("✅ Đã load thành công trang Đổi Mật Khẩu (Có API)!");
    }

    @FXML
    void handleUpdatePassword(ActionEvent event) {
        String oldPass = txtOldPass.getText();
        String newPass = txtNewPass.getText();
        String confirmPass = txtConfirmPass.getText();

        // 1. Kiểm tra Validate ở Frontend
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ các trường mật khẩu!");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi xác nhận", "Mật khẩu mới và phần xác nhận không khớp nhau!");
            return;
        }

        // 2. Gọi API xuống Backend
        // LƯU Ý: Đổi "oldPassword" và "newPassword" cho đúng với DTO bên Spring Boot của ông nhé
        String jsonBody = String.format("{\"oldPassword\": \"%s\", \"newPassword\": \"%s\"}", oldPass, newPass);

        ApiService.post("/api/password/setting-change", jsonBody).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Mật khẩu của bạn đã được cập nhật an toàn!");
                    // Xóa trắng form sau khi đổi pass thành công
                    txtOldPass.clear();
                    txtNewPass.clear();
                    txtConfirmPass.clear();
                } else {
                    // Xử lý khi Backend báo lỗi (VD: Sai pass cũ, mã lỗi 400/401)
                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Đổi mật khẩu thất bại. Vui lòng kiểm tra lại mật khẩu cũ!");
                }
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Lỗi mạng", "Không thể kết nối đến máy chủ Backend!");
                e.printStackTrace();
            });
            return null;
        });
    }

    // Hàm phụ trợ để gọi Alert cho gọn code
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}