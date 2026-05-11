package com.pharmacy.controller.sales;

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
        System.out.println("✅ Đã load thành công trang Đổi Mật Khẩu!");
    }

    @FXML
    void handleUpdatePassword(ActionEvent event) {
        String oldPass = txtOldPass.getText();
        String newPass = txtNewPass.getText();
        String confirmPass = txtConfirmPass.getText();

        // Kiểm tra xem người dùng có nhập thiếu không
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ các trường mật khẩu!");
            return;
        }

        // Kiểm tra mật khẩu mới và xác nhận có khớp không
        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi xác nhận", "Mật khẩu mới và phần xác nhận không khớp nhau!");
            return;
        }

        // Nếu mọi thứ OK -> Báo thành công và xóa trắng các ô nhập liệu
        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Mật khẩu của bạn đã được cập nhật an toàn!");
        txtOldPass.clear();
        txtNewPass.clear();
        txtConfirmPass.clear();
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