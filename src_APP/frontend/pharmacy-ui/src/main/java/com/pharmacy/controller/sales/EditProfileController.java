package com.pharmacy.controller.sales;

import com.pharmacy.model.User;
import com.pharmacy.util.Session;
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
        
        // LẤY THÔNG TIN USER ĐANG ĐĂNG NHẬP TỪ SESSION
        User currentUser = Session.getCurrentUser();
        
        if (currentUser != null) {
            // Đổ dữ liệu thật lên form
            lblFullNameCard.setText(currentUser.getFullName().toUpperCase());
            txtName.setText(currentUser.getFullName());
            txtPhone.setText(currentUser.getUsername());
            
            // Xử lý hiển thị Role
            String roleDisplay = "Nhân viên bán hàng (Sales)";
            if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                roleDisplay = "Quản trị viên (Admin)";
            } else if ("WAREHOUSE_STAFF".equalsIgnoreCase(currentUser.getRole())) {
                roleDisplay = "Nhân viên Kho";
            }
            txtRole.setText(roleDisplay);

            // Set rỗng các trường chưa có dữ liệu trong Session
            cbGender.setValue(null);
            dpBirthDate.setValue(null);
            txtEmail.setText("");
            txtJoinDate.setText("");
            
            // BẢO MẬT: Không cho phép tự ý sửa SĐT (Username) và Chức vụ (Role)
            txtPhone.setEditable(false);
            txtPhone.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #64748b;"); // Làm xám ô SĐT
            txtRole.setEditable(false);
            txtRole.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #64748b;"); // Làm xám ô Role
            txtJoinDate.setEditable(false);
            txtJoinDate.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #64748b;");
        }
    }

    @FXML
    void handleUpdateInfo(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận cập nhật");
        alert.setHeaderText("Lưu thông tin cá nhân");
        alert.setContentText("Bạn có chắc chắn muốn thay đổi thông tin cá nhân?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            // TODO: Ở đây sau này sẽ gọi ApiService.post(...) hoặc put để gửi dữ liệu lên Backend
            
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Thành công");
            success.setHeaderText(null);
            success.setContentText("Dữ liệu cá nhân đã được cập nhật an toàn.");
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
            javafx.scene.layout.AnchorPane salesContentPane = 
                (javafx.scene.layout.AnchorPane) source.getScene().lookup("#contentPane");

            if (salesContentPane != null) {
                com.pharmacy.util.SceneManager.loadContent(salesContentPane, "/com/pharmacy/views/sales/profile.fxml");
            } else {
                System.err.println("Lỗi: Không tìm thấy khung #contentPane của Sales.");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi quay lại trang Profile Sales.");
            e.printStackTrace();
        }
    }
}