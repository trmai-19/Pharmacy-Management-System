package com.pharmacy.controller.admin;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pharmacy.util.ApiService;
import javafx.scene.control.Label;
import com.pharmacy.util.SceneManager;

public class AddEmployeeController {

    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cbRole;

    @FXML
    public void initialize() {
        System.out.println("✅ Đã load form Cấp Tài Khoản Nhanh");
        cbRole.setItems(FXCollections.observableArrayList(
            "Quản trị viên (Admin)",
            "Nhân viên bán hàng",
            "Nhân viên kho"
        ));
    }

   @FXML
    void handleIssueAccount(ActionEvent event) {
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String role = cbRole.getValue();

        if (phone.isEmpty() || email.isEmpty() || role == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Thiếu thông tin");
            alert.setContentText("Vui lòng điền đủ Số điện thoại, Email và chọn Phân quyền!");
            alert.showAndWait();
            return;
        }

        String backendRole = mapRoleToBackendValue(role);
        ObjectNode request = ApiService.mapper.createObjectNode()
            .put("sdt", phone)
            .put("email", email)
            .put("vaitro", backendRole);

        System.out.println("⏳ Đang tạo tài khoản cho: " + email + " - Quyền: " + backendRole);

        try {
            String requestBody = ApiService.mapper.writeValueAsString(request);
            ApiService.post("/api/admin/accounts/create", requestBody)
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        javafx.application.Platform.runLater(() -> {
                            showCustomSuccessAlert(role, email);
                            handleCancel(event);
                        });
                    } else {
                        String errorMessage = String.format("Tạo tài khoản thất bại. Máy chủ trả về mã %d", response.statusCode());
                        javafx.application.Platform.runLater(() -> showErrorAlert(errorMessage));
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    javafx.application.Platform.runLater(() -> showErrorAlert("Không thể kết nối tới máy chủ. Vui lòng thử lại."));
                    return null;
                });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            showErrorAlert("Lỗi định dạng dữ liệu yêu cầu. Vui lòng thử lại.");
        }
    }

    private String mapRoleToBackendValue(String role) {
        return switch (role) {
            case "Quản trị viên (Admin)" -> "ADMIN";
            case "Nhân viên bán hàng" -> "SALES_STAFF";
            case "Nhân viên kho" -> "WAREHOUSE_STAFF";
            default -> "STAFF";
        };
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Lỗi khi tạo tài khoản");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showCustomSuccessAlert(String role, String email) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT); 

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 20; " +
                     "-fx-padding: 30 40; " +
                     "-fx-border-color: #10b981; " +
                     "-fx-border-width: 3; " +
                     "-fx-border-radius: 18; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 20, 0, 0, 10);");
        box.setPrefWidth(400);

        Label icon = new Label("✔");
        icon.setStyle("-fx-font-size: 60px; -fx-text-fill: #10b981; -fx-font-weight: bold;");

        Label lblTitle = new Label("Cấp Tài Khoản Thành Công!");
        lblTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #1e293b;");

        Label lblMessage = new Label("Tài khoản với quyền [" + role + "] đã được tạo.\nThông tin đăng nhập mặc định đã được gửi về email:\n" + email);
        lblMessage.setWrapText(true);
        lblMessage.setTextAlignment(TextAlignment.CENTER);
        lblMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-line-spacing: 4px;");

        Button btnOk = new Button("Tuyệt vời");
        btnOk.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 12 40; -fx-cursor: hand;");
        
        btnOk.setOnMouseEntered(e -> btnOk.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 12 40; -fx-cursor: hand;"));
        btnOk.setOnMouseExited(e -> btnOk.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 12 40; -fx-cursor: hand;"));

        btnOk.setOnAction(e -> dialogStage.close());

        box.getChildren().addAll(icon, lblTitle, lblMessage, btnOk);

        Scene scene = new Scene(box);
        scene.setFill(Color.TRANSPARENT);
        dialogStage.setScene(scene);

        dialogStage.showAndWait();
    }

    @FXML
    void handleCancel(ActionEvent event) {
        try {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            javafx.scene.layout.AnchorPane adminContentPane = 
                (javafx.scene.layout.AnchorPane) source.getScene().lookup("#contentPane");

            if (adminContentPane != null) {
                SceneManager.loadContent(adminContentPane, "/com/pharmacy/views/admin/employee-management.fxml");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi quay lại trang Danh sách nhân sự!");
            e.printStackTrace();
        }
    }
}