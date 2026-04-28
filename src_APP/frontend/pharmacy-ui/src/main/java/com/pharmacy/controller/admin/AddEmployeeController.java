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

import javafx.scene.control.Label;// Nhớ import class SceneManager của bạn
import com.pharmacy.util.SceneManager;

public class AddEmployeeController {

    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cbRole;

    @FXML
    public void initialize() {
        System.out.println("✅ Đã load form Cấp Tài Khoản Nhanh");
        // Nạp danh sách các quyền hạn trong nhà thuốc
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

        // 1. Validate: Yêu cầu điền đủ 3 thông tin
        if (phone.isEmpty() || email.isEmpty() || role == null) {
            // (Chỗ báo lỗi thiếu thông tin này bạn cũng có thể tự chế 1 hàm showCustomErrorAlert tương tự nhé)
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Thiếu thông tin");
            alert.setContentText("Vui lòng điền đủ Số điện thoại, Email và chọn Phân quyền!");
            alert.showAndWait();
            return;
        }

        System.out.println("⏳ Đang tạo tài khoản cho: " + email + " - Quyền: " + role);

        // 2. HIỂN THỊ THÔNG BÁO XỊN XÒ VỪA TẠO
        showCustomSuccessAlert(role, email);

        // 3. Cửa sổ đóng xong thì tự động quay lại trang Danh Sách Nhân Sự
        handleCancel(event); 
    }

    private void showCustomSuccessAlert(String role, String email) {
        // Tạo một cửa sổ mới hoàn toàn trong suốt (Bỏ viền Windows)
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Khóa màn hình nền
        dialogStage.initStyle(StageStyle.TRANSPARENT); // Xóa thanh tiêu đề

        // Khung chính của thông báo
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        // Thiết kế UI: Nền trắng, bo góc to, viền xanh lá, đổ bóng xịn xò
        box.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 20; " +
                     "-fx-padding: 30 40; " +
                     "-fx-border-color: #10b981; " +
                     "-fx-border-width: 3; " +
                     "-fx-border-radius: 18; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 20, 0, 0, 10);");
        box.setPrefWidth(400);

        // 1. Icon Checkmark (Dấu tick xanh)
        Label icon = new Label("✔");
        icon.setStyle("-fx-font-size: 60px; -fx-text-fill: #10b981; -fx-font-weight: bold;");

        // 2. Tiêu đề
        Label lblTitle = new Label("Cấp Tài Khoản Thành Công!");
        lblTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #1e293b;");

        // 3. Nội dung chi tiết
        Label lblMessage = new Label("Tài khoản với quyền [" + role + "] đã được tạo.\nThông tin đăng nhập mặc định đã được gửi về email:\n" + email);
        lblMessage.setWrapText(true);
        lblMessage.setTextAlignment(TextAlignment.CENTER);
        lblMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-line-spacing: 4px;");

        // 4. Nút bấm OK
        Button btnOk = new Button("Tuyệt vời");
        btnOk.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 12 40; -fx-cursor: hand;");
        
        // Hiệu ứng hover cho nút
        btnOk.setOnMouseEntered(e -> btnOk.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 12 40; -fx-cursor: hand;"));
        btnOk.setOnMouseExited(e -> btnOk.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 12 40; -fx-cursor: hand;"));

        // Khi bấm nút thì đóng cửa sổ thông báo
        btnOk.setOnAction(e -> dialogStage.close());

        box.getChildren().addAll(icon, lblTitle, lblMessage, btnOk);

        // Set Scene trong suốt
        Scene scene = new Scene(box);
        scene.setFill(Color.TRANSPARENT);
        dialogStage.setScene(scene);

        // Hiển thị và chờ người dùng bấm nút mới chạy tiếp code bên dưới
        dialogStage.showAndWait();
    }

    @FXML
    void handleCancel(ActionEvent event) {
        try {
            // Quay về trang danh sách bằng cách tìm AnchorPane cha
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