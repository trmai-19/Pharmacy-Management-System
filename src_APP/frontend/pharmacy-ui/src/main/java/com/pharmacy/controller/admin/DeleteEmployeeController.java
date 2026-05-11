package com.pharmacy.controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import com.pharmacy.util.SceneManager;

public class DeleteEmployeeController {

    @FXML private TextField txtDeleteEmployeeId;

    @FXML
    void confirmDelete(ActionEvent event) {
        String id = txtDeleteEmployeeId.getText();
        if (id == null || id.trim().isEmpty()) {
            System.out.println("⚠️ Vui lòng nhập mã nhân viên!");
            return;
        }
        
        // Thực hiện logic xóa ở đây (ví dụ gọi xuống Database)
        System.out.println("✅ Đang xóa nhân viên có mã: " + id);
        
        // Sau khi xóa xong thì quay lại trang quản lý
        handleCancel(event);
    }

    @FXML
    void handleCancel(ActionEvent event) {
        try {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            AnchorPane adminContentPane = (AnchorPane) source.getScene().lookup("#contentPane");
            if (adminContentPane != null) {
                SceneManager.loadContent(adminContentPane, "/com/pharmacy/views/admin/employee-management.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}