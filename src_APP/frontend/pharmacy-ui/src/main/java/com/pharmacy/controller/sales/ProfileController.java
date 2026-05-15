package com.pharmacy.controller.sales;

import com.pharmacy.model.User;
import com.pharmacy.util.Session;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.time.LocalDate;

public class ProfileController {

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
        System.out.println("✅ Đang nạp dữ liệu vào trang Profile...");
        try {
            cbGender.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
            
            // LẤY THÔNG TIN USER ĐANG ĐĂNG NHẬP TỪ SESSION
            User currentUser = Session.getCurrentUser();
            
            if (currentUser != null) {
                // Đổ dữ liệu thật từ Session lên giao diện
                lblFullNameCard.setText(currentUser.getFullName().toUpperCase());
                txtName.setText(currentUser.getFullName());
                txtPhone.setText(currentUser.getUsername()); // SĐT chính là username lúc login
                
                // Hiển thị Role tiếng Việt cho đẹp
                String roleDisplay = "Nhân viên bán hàng (Sales)";
                if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                    roleDisplay = "Quản trị viên (Admin)";
                } else if ("WAREHOUSE_STAFF".equalsIgnoreCase(currentUser.getRole())) {
                    roleDisplay = "Nhân viên Kho";
                }
                txtRole.setText(roleDisplay);

                // Tạm thời các thông tin chưa có trong Session sẽ để "Đang cập nhật" 
                // (Sau này nếu có API Get Profile Detail thì đắp thêm vào đây)
                cbGender.setValue("Khác");
                txtEmail.setText("Chưa cập nhật");
                txtJoinDate.setText("Chưa cập nhật");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi nạp dữ liệu Profile!");
            e.printStackTrace();
        }
    }

    @FXML
    void goToEditProfile(ActionEvent event) {
        try {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            // Đổi tên biến cho đúng ngữ cảnh Sales
            javafx.scene.layout.AnchorPane salesContentPane = 
                (javafx.scene.layout.AnchorPane) source.getScene().lookup("#contentPane");

            if (salesContentPane != null) {
                // ĐÃ SỬA: Phải trỏ về file edit-profile của module sales
                com.pharmacy.util.SceneManager.loadContent(salesContentPane, "/com/pharmacy/views/sales/edit-profile.fxml");
            } else {
                System.err.println("Không tìm thấy AnchorPane tổng để chuyển trang!");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi mở trang Chỉnh sửa thông tin!");
            e.printStackTrace();
        }
    }
}