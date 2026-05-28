package com.pharmacy.controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import com.pharmacy.util.Session;

public class DashboardController {

    @FXML private Label lblGreeting;
    @FXML private VBox cardProduct, cardEmployee, cardCustomer, cardAccount, cardReturn, cardReport, cardRole, cardSupport, cardSupplier;

    @FXML
    public void initialize() {
        System.out.println("📊 Dashboard chính đang được nạp...");

        String adminName = Session.getFullName() != null ? Session.getFullName() : "Quản trị viên";
        if (lblGreeting != null) {
            lblGreeting.setText("Chào mừng quay trở lại, " + adminName + "! 👋");
        }

        // Khởi tạo hiệu ứng Hover xịn xò cho TẤT CẢ các thẻ (Truyền màu viền tương ứng)
        setupHoverEffect(cardProduct, "#14b8a6");   // Xanh ngọc
        setupHoverEffect(cardEmployee, "#3b82f6");  // Xanh dương
        setupHoverEffect(cardCustomer, "#f59e0b");  // Cam
        setupHoverEffect(cardAccount, "#8b5cf6");   // Tím
        setupHoverEffect(cardReturn, "#ec4899");    // Hồng
        setupHoverEffect(cardReport, "#f43f5e");    // Đỏ hồng
        setupHoverEffect(cardRole, "#64748b");      // Xám đen
        setupHoverEffect(cardSupport, "#0ea5e9");   // Xanh dương nhạt
        setupHoverEffect(cardSupplier, "#10b981");  // Xanh lá (Emerald)
    }

    private void setupHoverEffect(VBox card, String borderColor) {
        if (card == null) return;
        
        // Trạng thái tĩnh bình thường
        String defaultStyle = "-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 22; " +
                              "-fx-border-color: " + borderColor + "; -fx-border-width: 0 0 0 6; " +
                              "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 10, 0, 0, 5);";
        
        // Trạng thái Hover (Phóng to nhẹ bóng đổ và nhích lên trên 3px)
        String hoverStyle = "-fx-background-color: #f8fafc; -fx-background-radius: 12; -fx-padding: 22; " +
                            "-fx-border-color: " + borderColor + "; -fx-border-width: 0 0 0 6; " +
                            "-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 8); -fx-translate-y: -3px;";
        
        card.setStyle(defaultStyle);
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(defaultStyle));
    }

    // ====================== GỌI LỆNH CHUYỂN TRANG ======================
    @FXML void goToProduct(MouseEvent event) { navigate("PRODUCT"); }
    @FXML void goToEmployee(MouseEvent event) { navigate("EMPLOYEE"); }
    @FXML void goToCustomer(MouseEvent event) { navigate("CUSTOMER"); }
    @FXML void goToAccount(MouseEvent event) { navigate("ACCOUNT"); }
    @FXML void goToReturn(MouseEvent event) { navigate("RETURN"); }
    @FXML void goToReport(MouseEvent event) { navigate("REPORT"); }
    @FXML void goToRole(MouseEvent event) { navigate("ROLE"); }
    @FXML void goToSupplier(MouseEvent event) { navigate("SUPPLIER"); } // Lệnh chuyển qua tab Nhà cung cấp

    private void navigate(String moduleCode) {
        AdminController admin = AdminController.getInstance();
        if (admin != null) {
            admin.navigateFromDashboard(moduleCode);
        } else {
            System.err.println("❌ Lỗi: Không kết nối được với AdminController!");
        }
    }
}