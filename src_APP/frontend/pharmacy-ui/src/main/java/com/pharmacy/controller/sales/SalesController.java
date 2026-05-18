package com.pharmacy.controller.sales;

import com.pharmacy.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton; 
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;

public class SalesController {

    @FXML private AnchorPane contentPane;
    @FXML private Label lblTitle;
    @FXML private VBox sideMenu;
    @FXML private Label lblEmployeeName;
    @FXML private Button btnSales;
    @FXML private Button btnCustomers;
    @FXML private Button btnReturns; // Nút Đổi Trả được thêm vào
    @FXML private MenuButton avatarMenuButton; 
    
    private ContextMenu notificationMenu;   

    @FXML private StackPane bellIcon;
    @FXML private Label lblNotificationCount;

    private Button currentActiveButton;
    @FXML private Label lblRole;

    @FXML
    public void initialize() {
        System.out.println("✅ Khởi tạo giao diện Sales...");

        // 1. Đổ tên thật đã xử lý ở bước Login
        if (lblEmployeeName != null) {
            lblEmployeeName.setText(com.pharmacy.util.Session.getFullName());
        }

        // 2. Đổi chữ QUẢN TRỊ VIÊN thành đúng vai trò
        if (lblRole != null && com.pharmacy.util.Session.getCurrentUser() != null) {
            String role = com.pharmacy.util.Session.getCurrentUser().getRole();
            String roleDisplay = "";

            // Map từ mã role của backend sang tên hiển thị tiếng Việt (viết hoa cho đúng style UI của ông)
            switch (role.toUpperCase()) {
                case "ADMIN":
                    roleDisplay = "QUẢN TRỊ VIÊN";
                    break;
                case "SALES_STAFF":
                    roleDisplay = "NHÂN VIÊN BÁN HÀNG";
                    break;
                case "WAREHOUSE_STAFF":
                    roleDisplay = "NHÂN VIÊN KHO";
                    break;
                default:
                    roleDisplay = "NHÂN VIÊN";
            }
            lblRole.setText(roleDisplay);
        }

        loadView("sales-pos.fxml", "LẬP HÓA ĐƠN BÁN HÀNG");

        if (btnSales != null) {
            setActiveButtonStyle(btnSales);
        }
    }

    // ====================== CÁC HÀM MENU BÊN TRÁI ======================
    @FXML
    void showSalesManager(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "sales-pos.fxml", "LẬP HÓA ĐƠN BÁN HÀNG");
    }

    @FXML
    void showCustomerManager(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "customer-management.fxml", "QUẢN LÝ KHÁCH HÀNG");
    }

    // Hàm mở giao diện Quản Lý Đổi Trả
    @FXML
    void showReturnManager(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "return-management.fxml", "QUẢN LÝ ĐỔI TRẢ");
    }

    // ====================== LOGIC ĐỔI TRANG ======================
    private void handleMenuClick(Button clickedButton, String fxmlName, String title) {
        setActiveButtonStyle(clickedButton);
        loadView(fxmlName, title);
    }

    private void setActiveButtonStyle(Button clickedButton) {
        for (Node node : sideMenu.getChildren()) {
            if (node instanceof Button btn) {
                btn.getStyleClass().remove("nav-btn-active");
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 13px; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20;");
            }
        }
        if (clickedButton != null) {
            clickedButton.setStyle("-fx-background-color: rgba(28, 201, 183, 0.85); -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20; -fx-font-weight: bold;");
            currentActiveButton = clickedButton;
        }
    }

    private void loadView(String fxmlFileName, String title) {
        if (lblTitle != null) lblTitle.setText(title);
        
        String path = "/com/pharmacy/views/sales/" + fxmlFileName;
        try {
            URL resource = getClass().getResource(path);
            if (resource == null) {
                System.err.println("❌ LỖI: KHÔNG TÌM THẤY FILE " + fxmlFileName);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(resource);
            Node view = loader.load();
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
            
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            
            contentPane.setOpacity(0);
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(400), contentPane);
            ft.setFromValue(0.0); ft.setToValue(1.0); ft.play();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ====================== THÔNG BÁO & AVATAR (Giữ nguyên như Warehouse) ======================
    @FXML
    void handleShowNotifications(MouseEvent event) {
        // ... (Copy logic handleShowNotifications của Warehouse qua đây)
    }

    private HBox createNotificationRow(String title, String content, String timeStr, String type) {
        // ... (Copy logic createNotificationRow của Warehouse qua đây)
        return new HBox(); 
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        setActiveButtonStyle(null); 
        loadView("profile.fxml", "THÔNG TIN TÀI KHOẢN");
    }

    @FXML
    private void handleChangePassword(ActionEvent event) {
        setActiveButtonStyle(null);
        loadView("change-password.fxml", "ĐỔI MẬT KHẨU");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            SceneManager.switchScene("/com/pharmacy/views/login.fxml");
        } catch (Exception e) { e.printStackTrace(); }
    }
}