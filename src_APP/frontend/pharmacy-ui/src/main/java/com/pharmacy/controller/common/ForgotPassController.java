package com.pharmacy.controller.common;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmacy.util.ApiService;
import com.pharmacy.util.SceneManager;

public class ForgotPassController {

    @FXML private TextField txtSdt;
    @FXML private TextField txtEmail;
    @FXML private Button btnSubmit;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (txtSdt != null && txtSdt.getScene() != null) {
                javafx.scene.Parent root = txtSdt.getScene().getRoot();
                root.setFocusTraversable(true);
                root.requestFocus();
                txtSdt.getScene().setOnMouseClicked(event -> root.requestFocus());
            }
        });
    }

    @FXML
    void handleRequestAdmin(ActionEvent event) {
        String sdt = txtSdt.getText() != null ? txtSdt.getText().trim() : "";
        String email = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
        
        if (sdt.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ Số điện thoại và Email!");
            return;
        }
        
        btnSubmit.setDisable(true); 
        btnSubmit.setText("ĐANG XỬ LÝ...");

        String jsonBody = String.format("{\"sdt\": \"%s\", \"email\": \"%s\"}", sdt, email);

        // ĐÃ DÙNG APISERVICE
        ApiService.postPublic("/api/password/forgot", jsonBody).thenAccept(response -> {
            Platform.runLater(() -> {
                resetSubmitButton();

                try {
                    JsonNode rootNode = ApiService.mapper.readTree(response.body());
                    String message = rootNode.has("message") ? rootNode.get("message").asText() : "Đã gửi yêu cầu!";

                    if (response.statusCode() == 200) {
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", message);
                        handleGoToLogin(null);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi xác thực", message);
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi xử lý", "Không thể đọc dữ liệu từ máy chủ!");
                }
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                resetSubmitButton();
                showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể kết nối đến máy chủ Backend!");
            });
            return null;
        });
    }

    @FXML
    void handleGoToLogin(ActionEvent event) {
        try {
            SceneManager.switchScene("/com/pharmacy/views/login.fxml"); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetSubmitButton() {
        btnSubmit.setDisable(false);
        btnSubmit.setText("Gửi yêu cầu tới Admin");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}