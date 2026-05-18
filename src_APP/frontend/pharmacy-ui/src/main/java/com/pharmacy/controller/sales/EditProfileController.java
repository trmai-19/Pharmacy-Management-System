package com.pharmacy.controller.sales;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pharmacy.util.ApiService;
import com.pharmacy.util.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.Optional;

public class EditProfileController {

    @FXML private Label lblFullNameCard;
    @FXML private TextField txtName, txtPhone, txtEmail, txtRole, txtJoinDate;
    @FXML private ComboBox<String> cbGender;
    @FXML private DatePicker dpBirthDate;

    @FXML
    public void initialize() {
        cbGender.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        
        // 1. KHÓA VÀ BÔI XÁM CÁC TRƯỜNG KHÔNG CHO SỬA
        txtPhone.setEditable(false);
        txtPhone.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #94a3b8;"); // Màu xám nhạt
        
        txtEmail.setEditable(false);
        txtEmail.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #94a3b8;");
        
        txtRole.setEditable(false);
        txtRole.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #94a3b8;");
        
        txtJoinDate.setEditable(false);
        txtJoinDate.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #94a3b8;");

        loadInitialData();
    }

    private void loadInitialData() {
        ApiService.get("/api/profile/me").thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode data = ApiService.mapper.readTree(response.body()).get("data");
                        
                        lblFullNameCard.setText(data.get("hoten").asText().toUpperCase());
                        txtName.setText(data.get("hoten").asText());
                        txtPhone.setText(data.get("sdt").asText());
                        txtRole.setText(data.get("vaitro").asText());
                        
                        if (data.has("email") && !data.get("email").isNull()) {
                            txtEmail.setText(data.get("email").asText());
                        }
                        if (data.has("gioitinh") && !data.get("gioitinh").isNull()) {
                            cbGender.setValue(data.get("gioitinh").asText());
                        }
                        if (data.has("ngaysinh") && !data.get("ngaysinh").isNull()) {
                            String dobStr = data.get("ngaysinh").asText().split("T")[0];
                            dpBirthDate.setValue(LocalDate.parse(dobStr));
                        }
                        if (data.has("ngayvaolam") && !data.get("ngayvaolam").isNull()) {
                            txtJoinDate.setText(data.get("ngayvaolam").asText().split("T")[0]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    @FXML
    void handleUpdateInfo(ActionEvent event) {
        String newName = txtName.getText().trim();
        String gender = cbGender.getValue();
        LocalDate dob = dpBirthDate.getValue();

        if (newName.isEmpty() || gender == null || dob == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đủ Họ tên, Giới tính và Ngày sinh!").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn lưu thay đổi?");
        Optional<ButtonType> result = confirm.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ObjectNode jsonBody = ApiService.mapper.createObjectNode();
                jsonBody.put("hoten", newName);
                jsonBody.put("gioitinh", gender);
                jsonBody.put("ngaysinh", dob.toString());

                ApiService.put("/api/profile/update", jsonBody.toString()).thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            // 2. CẬP NHẬT DỮ LIỆU TRONG SESSION
                            Session.getCurrentUser().setFullName(newName);
                            
                            // 3. ÉP CẬP NHẬT TÊN TRÊN HEADER (Góc trên bên phải màn hình tổng)
                            javafx.scene.Scene scene = txtName.getScene();
                            Label lblHeaderName = (Label) scene.lookup("#lblEmployeeName");
                            if (lblHeaderName != null) {
                                lblHeaderName.setText(newName);
                            }
                            
                            new Alert(Alert.AlertType.INFORMATION, "Cập nhật thành công!").showAndWait();
                            returnToView();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Lỗi server: " + response.statusCode()).show();
                        }
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void returnToView() {
        try {
            javafx.scene.Scene currentScene = txtName.getScene();
            javafx.scene.layout.AnchorPane salesContentPane = (javafx.scene.layout.AnchorPane) currentScene.lookup("#contentPane");
            if (salesContentPane != null) {
                com.pharmacy.util.SceneManager.loadContent(salesContentPane, "/com/pharmacy/views/sales/profile.fxml");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void cancelEdit(ActionEvent event) { returnToView(); }
}