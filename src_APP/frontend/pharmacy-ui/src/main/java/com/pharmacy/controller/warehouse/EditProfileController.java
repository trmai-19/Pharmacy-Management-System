package com.pharmacy.controller.warehouse;

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
    @FXML private TextField txtName;
    @FXML private ComboBox<String> cbGender;
    @FXML private DatePicker dpBirthDate;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtRole;
    @FXML private TextField txtJoinDate;

    @FXML
    public void initialize() {
        System.out.println("✅ Đã vào chế độ Chỉnh Sửa Thông Tin!");
        cbGender.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));

        txtPhone.setEditable(false);
        txtPhone.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #94a3b8;");
        
        txtEmail.setEditable(false);
        txtEmail.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #94a3b8;");
        
        txtRole.setEditable(false);
        txtRole.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #94a3b8;");
        
        txtJoinDate.setEditable(false);
        txtJoinDate.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #94a3b8;");

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
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đủ Họ tên, Giới tính và Ngày sinh!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận cập nhật");
        alert.setHeaderText("Lưu thông tin cá nhân");
        alert.setContentText("Bạn có chắc chắn muốn thay đổi thông tin cá nhân của mình?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ObjectNode jsonBody = ApiService.mapper.createObjectNode();
                jsonBody.put("hoten", newName);
                jsonBody.put("gioitinh", gender);
                jsonBody.put("ngaysinh", dob.toString());

                ApiService.put("/api/profile/update", jsonBody.toString()).thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            Session.getCurrentUser().setFullName(newName);
                            
                            // Cập nhật tên ở Header của Warehouse
                            javafx.scene.Scene scene = txtName.getScene();
                            Label lblHeaderName = (Label) scene.lookup("#lblWarehouseName");
                            if (lblHeaderName != null) {
                                lblHeaderName.setText(newName);
                            }

                            Alert success = new Alert(Alert.AlertType.INFORMATION);
                            success.setTitle("Thành công");
                            success.setHeaderText(null);
                            success.setContentText("Dữ liệu của bạn đã được cập nhật an toàn vào hệ thống.");
                            success.showAndWait();
                            
                            returnToView(event);
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật thông tin. Mã lỗi: " + response.statusCode());
                        }
                    });
                }).exceptionally(e -> {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi mạng", "Không thể kết nối đến máy chủ!"));
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void cancelEdit(ActionEvent event) {
        returnToView(event);
    }

    private void returnToView(ActionEvent event) {
        try {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            javafx.scene.layout.AnchorPane adminContentPane = 
                (javafx.scene.layout.AnchorPane) source.getScene().lookup("#contentPane");

            if (adminContentPane != null) {
                com.pharmacy.util.SceneManager.loadContent(adminContentPane, "/com/pharmacy/views/warehouse/profile.fxml");
            } else {
                System.err.println("❌ Lỗi: Không tìm thấy khung #contentPane để quay lại.");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi cấu trúc khi quay lại trang Profile.");
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