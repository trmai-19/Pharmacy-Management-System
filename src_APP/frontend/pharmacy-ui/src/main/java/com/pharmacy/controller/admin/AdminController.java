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
import com.pharmacy.model.User;

public class AdminController {

    @FXML private AnchorPane contentPane;
    @FXML private Label lblTitle;
    @FXML private VBox sideMenu;
    @FXML private Label lblAdminName; 
    @FXML private Button btnDashboard;

    private Button currentActiveButton;
    
    // Tạo cầu nối (Singleton) để DashboardController có thể gọi hàm điều hướng
    private static AdminController instance;

    public static AdminController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        System.out.println("✅ Khởi tạo giao diện Admin...");
        instance = this; // Gán instance hiện tại
        
        // ==========================================
        // XỬ LÝ HIỂN THỊ TÊN ĐÚNG LOGIC USER
        // ==========================================
        if (lblAdminName != null) {
            String fullName = com.pharmacy.util.Session.getFullName();
            
            if (fullName == null || fullName.trim().isEmpty() || 
                fullName.equalsIgnoreCase("null") || fullName.equals("Unknown User")) {
                
                User currentUser = com.pharmacy.util.Session.getCurrentUser();
                if (currentUser != null) {
                    String phone = currentUser.getUsername(); 
                    if (phone != null && phone.length() >= 4) {
                        String last4 = phone.substring(phone.length() - 4);
                        lblAdminName.setText("User" + last4);
                    } else {
                        lblAdminName.setText("Quản trị viên");
                    }
                } else {
                    lblAdminName.setText("Quản trị viên");
                }
            } else {
                lblAdminName.setText(fullName);
            }
        }
        
        loadView("dashboard.fxml", "BẢNG ĐIỀU KHIỂN TỔNG QUAN");
        
        if (btnDashboard != null) {
            setActiveButtonStyle(btnDashboard);
        }
    }

    // ====================== CÁC HÀM CHUYỂN MENU TỪ SIDEBAR ======================
    @FXML void showDashboard(ActionEvent event) { handleMenuClick((Button) event.getSource(), "dashboard.fxml", "BẢNG ĐIỀU KHIỂN TỔNG QUAN"); }
    @FXML void showProductManager(ActionEvent event) { handleMenuClick((Button) event.getSource(), "product-management.fxml", "QUẢN LÝ SẢN PHẨM"); }
    @FXML void showSupplierManager(ActionEvent event) { handleMenuClick((Button) event.getSource(), "supplier-manager.fxml", "QUẢN LÝ NHÀ CUNG CẤP"); } // THÊM MỚI Ở ĐÂY
    @FXML void showEmployeeManager(ActionEvent event) { handleMenuClick((Button) event.getSource(), "employee-management.fxml", "QUẢN LÝ NHÂN SỰ"); }
    @FXML void showCustomerManager(ActionEvent event) { handleMenuClick((Button) event.getSource(), "customer-management.fxml", "QUẢN LÝ KHÁCH HÀNG"); }
    @FXML void showAccountManager(ActionEvent event) { handleMenuClick((Button) event.getSource(), "account-management.fxml", "QUẢN LÝ TÀI KHOẢN"); }
    @FXML void showReturnManager(ActionEvent event) { handleMenuClick((Button) event.getSource(), "return-management.fxml", "QUẢN LÝ ĐỔI TRẢ"); }
    @FXML void showReports(ActionEvent event) { handleMenuClick((Button) event.getSource(), "report.fxml", "BÁO CÁO & THỐNG KÊ"); }
    @FXML void showRoleManager(ActionEvent event) { handleMenuClick((Button) event.getSource(), "role-manager.fxml", "QUẢN LÝ QUYỀN"); }

    // ====================== ĐIỀU HƯỚNG TỪ CARD DASHBOARD ======================
    public void navigateFromDashboard(String moduleCode) {
        String fxml = "";
        String title = "";
        String btnKeyword = "";

        switch (moduleCode) {
            case "PRODUCT": fxml = "product-management.fxml"; title = "QUẢN LÝ SẢN PHẨM"; btnKeyword = "SẢN PHẨM"; break;
            case "SUPPLIER": fxml = "supplier-manager.fxml"; title = "QUẢN LÝ NHÀ CUNG CẤP"; btnKeyword = "NHÀ CUNG CẤP"; break; // THÊM MỚI Ở ĐÂY
            case "EMPLOYEE": fxml = "employee-management.fxml"; title = "QUẢN LÝ NHÂN SỰ"; btnKeyword = "NHÂN SỰ"; break;
            case "CUSTOMER": fxml = "customer-management.fxml"; title = "QUẢN LÝ KHÁCH HÀNG"; btnKeyword = "KHÁCH HÀNG"; break;
            case "ACCOUNT": fxml = "account-management.fxml"; title = "QUẢN LÝ TÀI KHOẢN"; btnKeyword = "TÀI KHOẢN"; break;
            case "RETURN": fxml = "return-management.fxml"; title = "QUẢN LÝ ĐỔI TRẢ"; btnKeyword = "ĐỔI TRẢ"; break;
            case "REPORT": fxml = "report.fxml"; title = "BÁO CÁO & THỐNG KÊ"; btnKeyword = "BÁO CÁO"; break;
            case "ROLE": fxml = "role-manager.fxml"; title = "QUẢN LÝ QUYỀN"; btnKeyword = "QUYỀN"; break;
        }

        // Tự động tìm kiếm nút tương ứng bên Sidebar để highlight
        Button targetBtn = null;
        if (sideMenu != null) {
            for (Node node : sideMenu.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    if (btn.getText().toUpperCase().contains(btnKeyword)) {
                        targetBtn = btn;
                        break;
                    }
                }
            }
        }
        handleMenuClick(targetBtn, fxml, title);
    }

    // ====================== LOGIC ĐỔI TRANG ======================
    private void handleMenuClick(Button clickedButton, String fxmlName, String title) {
        setActiveButtonStyle(clickedButton);
        loadView(fxmlName, title);
    }

    private void setActiveButtonStyle(Button clickedButton) {
        for (Node node : sideMenu.getChildren()) {
            if (node instanceof Button btn) {
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
        
        String path = "/com/pharmacy/views/admin/" + fxmlFileName;
        System.out.println("🔄 Đang chuyển sang trang: " + path);
        
        try {
            URL resource = getClass().getResource(path);
            if (resource == null) { System.err.println("❌ LỖI: KHÔNG TÌM THẤY FILE " + fxmlFileName); return; }
            
            FXMLLoader loader = new FXMLLoader(resource);
            Node view = loader.load();
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
            
            AnchorPane.setTopAnchor(view, 0.0); AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0); AnchorPane.setRightAnchor(view, 0.0);
            
            contentPane.setOpacity(0);
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(400), contentPane);
            ft.setFromValue(0.0); ft.setToValue(1.0); ft.play();
            
        } catch (IOException e) {
            System.err.println("❌ LỖI LOAD FILE: " + fxmlFileName); e.printStackTrace();
        }
    }

    // ====================== XỬ LÝ MENU AVATAR ======================
    @FXML private void handleViewProfile(ActionEvent event) { loadView("profile.fxml", "THÔNG TIN TÀI KHOẢN"); }
    @FXML private void handleChangePassword(ActionEvent event) { loadView("change-password.fxml", "ĐỔI MẬT KHẨU"); }

    @FXML
    void handleLogout(ActionEvent event) {
        System.out.println("🚪 Đăng xuất...");
        try {
            com.pharmacy.util.SceneManager.switchScene("/com/pharmacy/views/login.fxml");
        } catch (Exception e) { e.printStackTrace(); }
    }
}