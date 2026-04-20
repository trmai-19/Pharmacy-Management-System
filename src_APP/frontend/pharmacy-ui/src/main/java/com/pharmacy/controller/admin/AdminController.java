package com.pharmacy.controller.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

public class AdminController {

    @FXML private AnchorPane contentPane;
    @FXML private Label lblTitle;
    @FXML private VBox sideMenu;
    @FXML private Label lblAdminName;
    @FXML private Button btnDashboard; 

    private Button currentActiveButton; 

    @FXML
    public void initialize() {
        System.out.println("✅ Khởi tạo giao diện Admin...");
        
        // Load trang mặc định
        loadView("dashboard.fxml", "BẢNG ĐIỀU KHIỂN TỔNG QUAN");
        
        if (btnDashboard != null) {
            setActiveButtonStyle(btnDashboard);
        }
    }

    // --- CÁC HÀM NHẬN SỰ KIỆN CLICK TỪ MENU ---

    @FXML
    void showDashboard(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "dashboard.fxml", "BẢNG ĐIỀU KHIỂN TỔNG QUAN");
    }

    @FXML
    void showProductManager(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "product-management.fxml", "QUẢN LÝ KHO DƯỢC");
    }

    @FXML
    void showEmployeeManager(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "employee-management.fxml", "QUẢN LÝ NHÂN SỰ");
    }

    @FXML
    void showCustomerManager(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "customer-management.fxml", "QUẢN LÝ KHÁCH HÀNG");
    }

    @FXML
    void showAccountManager(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "account-management.fxml", "QUẢN LÝ TÀI KHOẢN");
    }

    @FXML
    void showReports(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "report.fxml", "BÁO CÁO & THỐNG KÊ");
    }

    // --- LOGIC ĐỔI TRANG ---

    private void handleMenuClick(Button clickedButton, String fxmlName, String title) {
        setActiveButtonStyle(clickedButton);
        loadView(fxmlName, title);
    }

    private void setActiveButtonStyle(Button clickedButton) {
        for (Node node : sideMenu.getChildren()) {
            if (node instanceof Button btn) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 15px; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20;");
            }
        }
        clickedButton.setStyle("-fx-background-color: rgba(28, 201, 183, 0.85); -fx-text-fill: white; -fx-font-size: 15px; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20; -fx-font-weight: bold;");
        currentActiveButton = clickedButton;
    }

    private void loadView(String fxmlFileName, String title) {
        if (lblTitle != null) {
            lblTitle.setText(title);
        }
        
        String path = "/com/pharmacy/views/admin/" + fxmlFileName;
        System.out.println("🔄 Đang chuyển sang trang: " + path);
        
        try {
            // 1. Kiểm tra xem file FXML có tồn tại hay không
            URL resource = getClass().getResource(path);
            if (resource == null) {
                System.err.println("❌ LỖI TRẦM TRỌNG: KHÔNG TÌM THẤY FILE " + fxmlFileName + " TRONG THƯ MỤC " + "/com/pharmacy/views/admin/");
                System.err.println("👉 Vui lòng tạo file này đi nhé!");
                return; // Dừng lại luôn để app không bị crash
            }

            // 2. Load giao diện mới
            FXMLLoader loader = new FXMLLoader(resource);
            Node view = loader.load();
            
            // 3. Xóa nội dung cũ trong giỏ
            contentPane.getChildren().clear();
            
            // 4. Bỏ giao diện mới vào giỏ
            contentPane.getChildren().add(view);
            
            // 5. Căng đều 4 góc cho khít màn hình
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            
            // 6. Hiệu ứng mượt mà
            contentPane.setOpacity(0);
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(400), contentPane);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
            
        } catch (IOException e) {
            System.err.println("❌ LỖI MÃ NGUỒN TRONG FILE: " + fxmlFileName);
            e.printStackTrace();
        }
    }
}