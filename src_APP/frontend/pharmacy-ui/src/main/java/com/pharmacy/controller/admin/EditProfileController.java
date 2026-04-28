package com.pharmacy.controller.admin;

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
        
        // Nạp data mặc định vào các ô để người dùng sửa
        cbGender.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        lblFullNameCard.setText("NGUYỄN VĂN PHÁT");
        txtName.setText("Nguyễn Văn Phát");
        cbGender.setValue("Nam");
        dpBirthDate.setValue(LocalDate.of(2004, 1, 1));
        txtPhone.setText("0987.654.321");
        txtEmail.setText("admin.phat@pharmacy.com");
        txtRole.setText("Quản trị viên (Admin)");
        txtJoinDate.setText("15/10/2023");
    }

    @FXML
    void handleUpdateInfo(ActionEvent event) {
        // 1. Hiện Pop-up xác nhận
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận cập nhật");
        alert.setHeaderText("Lưu thông tin cá nhân");
        alert.setContentText("Bạn có chắc chắn muốn thay đổi thông tin cá nhân của mình?");

        // 2. Chờ người dùng bấm nút
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // NẾU BẤM OK -> Báo thành công và quay lại
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Thành công");
            success.setHeaderText(null);
            success.setContentText("Dữ liệu của bạn đã được cập nhật an toàn vào hệ thống.");
            success.showAndWait();
            
            returnToView(event);
        }
    }

    @FXML
    void cancelEdit(ActionEvent event) {
        // NẾU BẤM HỦY -> Quay lại luôn không cần hỏi
        returnToView(event);
    }

    // Hàm tuyệt chiêu: Quay lại trang Profile ban đầu
    private void returnToView(ActionEvent event) {
        try {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            javafx.scene.layout.AnchorPane adminContentPane = 
                (javafx.scene.layout.AnchorPane) source.getScene().lookup("#contentPane");

            if (adminContentPane != null) {
                com.pharmacy.util.SceneManager.loadContent(adminContentPane, "/com/pharmacy/views/admin/profile.fxml");
            } else {
                System.err.println("❌ Lỗi: Không tìm thấy khung #contentPane để quay lại.");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi cấu trúc khi quay lại trang Profile.");
            e.printStackTrace();
        }
    }
}