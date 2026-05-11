package com.pharmacy.controller.sales;

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
            // Khởi tạo danh sách giới tính (Mặc dù bị disable nhưng vẫn phải có data để hiển thị)
            cbGender.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
            
            // Set dữ liệu hiển thị mặc định
            lblFullNameCard.setText("NGUYỄN VĂN PHÁT");
            txtName.setText("Nguyễn Văn Phát");
            cbGender.setValue("Nam");
            dpBirthDate.setValue(LocalDate.of(2004, 1, 1));
            txtPhone.setText("0987.654.321");
            txtEmail.setText("admin.phat@pharmacy.com");
            txtRole.setText("Quản trị viên (Admin)");
            txtJoinDate.setText("15/10/2023");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi nạp dữ liệu Profile!");
            e.printStackTrace();
        }
    }

    @FXML
    void goToEditProfile(ActionEvent event) {
        try {
            // Tuyệt chiêu nhảy trang nội bộ không báo lỗi
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            javafx.scene.layout.AnchorPane adminContentPane = 
                (javafx.scene.layout.AnchorPane) source.getScene().lookup("#contentPane");

            if (adminContentPane != null) {
                com.pharmacy.util.SceneManager.loadContent(adminContentPane, "/com/pharmacy/views/admin/edit-profile.fxml");
            } else {
                System.err.println("❌ Không tìm thấy AnchorPane tổng để chuyển trang!");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi mở trang Chỉnh sửa thông tin!");
            e.printStackTrace();
        }
    }
}