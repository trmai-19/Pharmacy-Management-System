package com.pharmacy.controller.sales; // Đã đổi package

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
        System.out.println("✅ Đã vào chế độ Chỉnh Sửa Thông Tin (Phân hệ Sales)!");
        
        cbGender.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        
        // Data mẫu cho Sales
        lblFullNameCard.setText("NGUYỄN VĂN PHÁT");
        txtName.setText("Nguyễn Văn Phát");
        cbGender.setValue("Nam");
        dpBirthDate.setValue(LocalDate.of(2004, 1, 1));
        txtPhone.setText("0987.654.321");
        txtEmail.setText("sales.phat@pharmacy.com"); // Đổi email demo thành sales
        txtRole.setText("Nhân viên bán hàng (Sales)"); // Đổi role
        txtJoinDate.setText("15/10/2023");
    }

    @FXML
    void handleUpdateInfo(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận cập nhật");
        alert.setHeaderText("Lưu thông tin cá nhân");
        alert.setContentText("Bạn có chắc chắn muốn thay đổi thông tin cá nhân?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Thành công");
            success.setHeaderText(null);
            success.setContentText("Dữ liệu bán hàng đã được cập nhật an toàn.");
            success.showAndWait();
            
            returnToView(event);
        }
    }

    @FXML
    void cancelEdit(ActionEvent event) {
        returnToView(event);
    }

    private void returnToView(ActionEvent event) {
        try {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            // Đổi tên biến từ adminContentPane thành salesContentPane cho đúng ngữ cảnh
            javafx.scene.layout.AnchorPane salesContentPane = 
                (javafx.scene.layout.AnchorPane) source.getScene().lookup("#contentPane");

            if (salesContentPane != null) {
                // ĐÃ ĐỔI: Đường dẫn view từ /admin/ sang /sales/
                com.pharmacy.util.SceneManager.loadContent(salesContentPane, "/com/pharmacy/views/sales/profile.fxml");
            } else {
                System.err.println("❌ Lỗi: Không tìm thấy khung #contentPane của Sales.");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi quay lại trang Profile Sales.");
            e.printStackTrace();
        }
    }
}