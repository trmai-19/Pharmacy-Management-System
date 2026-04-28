package com.pharmacy.controller.common;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;

import com.pharmacy.util.SceneManager;

public class ForgotPassController {

    @FXML private TextField txtEmailOrUser;

   @FXML
    public void initialize() {
        // Dùng Platform.runLater để đợi giao diện vẽ lên màn hình xong xuôi
        Platform.runLater(() -> {
            if (txtEmailOrUser.getScene() != null) {
                javafx.scene.Parent root = txtEmailOrUser.getScene().getRoot();
                
                // 🔑 CHÌA KHÓA LÀ ĐÂY: Ép cái khung nền phải có khả năng nhận focus
                root.setFocusTraversable(true);
                
                // Sau đó mới tự tin giật focus về nó
                root.requestFocus();

                // Bonus: Khi click chuột ra vùng nền trắng, con trỏ sẽ biến mất khỏi ô nhập
                txtEmailOrUser.getScene().setOnMouseClicked(event -> {
                    root.requestFocus();
                });
            }
        });
    }

    @FXML
    void handleRequestAdmin(ActionEvent event) {
        // Lấy thông tin user nhập vào
        String input = txtEmailOrUser.getText();
        
        // Cảnh báo nếu người dùng chưa nhập gì mà đã bấm nút
        if (input == null || input.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Thiếu thông tin");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng nhập Tài khoản hoặc Email trước khi gửi yêu cầu!");
            alert.showAndWait();
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Yêu cầu cấp lại mật khẩu");
        alert.setHeaderText(null);
        alert.setContentText("Đã gửi yêu cầu cấp lại mật khẩu cho tài khoản: " + input + " tới Admin!");
        alert.showAndWait();
    }

    @FXML
    void handleGoToLogin(ActionEvent event) {
        try {
            // Gọi thẳng SceneManager với 1 tham số duy nhất là đường dẫn
            SceneManager.switchScene("/com/pharmacy/views/login.fxml"); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}