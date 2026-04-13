package com.pharmacy.controller.common;

import com.pharmacy.model.User;
import com.pharmacy.util.SceneManager;
import com.pharmacy.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private Label lblUserInfo;
    @FXML private Button btnLogout;
    @FXML private VBox sidebarMenu;
    @FXML private AnchorPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User user = Session.getCurrentUser();
        if (user != null) {
            lblUserInfo.setText(user.getFullName() + " - " + getRoleDisplay(user.getRole()));
        }
        setupSidebar();
    }

    private String getRoleDisplay(String role) {
        if (role == null) return "";

        switch (role) {
            case "ADMIN":
                return "Quản trị viên";
            case "SALES":
                return "Nhân viên bán hàng";
            case "WAREHOUSE":
                return "Nhân viên kho";
            default:
                return role;
        }
    }

    private void setupSidebar() {
        String role = Session.getRole();
        if (role == null) return;

        sidebarMenu.getChildren().clear();

        switch (role) {
            case "ADMIN":
                setupAdminMenu();
                break;
            case "SALES":
                setupSalesMenu();
                break;
            case "WAREHOUSE":
                setupWarehouseMenu();
                break;
            default:
                setupSalesMenu();   // mặc định là Sales
                break;
        }
    }

    private void setupAdminMenu() {
        addMenuButton("📊 Xem báo cáo thống kê", "/com/pharmacy/views/admin/report.fxml");
        addMenuButton("👥 Quản lý nhân viên", "/com/pharmacy/views/admin/employee-management.fxml");
        addMenuButton("🔑 Quản lý tài khoản hệ thống", "/com/pharmacy/views/admin/account-management.fxml");
        addMenuButton("📦 Quản lý sản phẩm", "/com/pharmacy/views/admin/product-management.fxml");
        addMenuButton("👤 Quản lý khách hàng", "/com/pharmacy/views/admin/customer-management.fxml");
    }

    private void setupSalesMenu() {
        addMenuButton("🔍 Tra cứu thông tin sản phẩm", "/com/pharmacy/views/sales/product-search.fxml");
        addMenuButton("⚠️ Xem cảnh báo hạn sử dụng", "/com/pharmacy/views/sales/expiration-warning.fxml");
        addMenuButton("🧾 Lập hóa đơn bán hàng", "/com/pharmacy/views/sales/create-invoice.fxml");
        addMenuButton("💰 Thanh toán hóa đơn", "/com/pharmacy/views/sales/payment.fxml");
        addMenuButton("🔄 Đổi trả hàng cho khách hàng", "/com/pharmacy/views/sales/return-to-customer.fxml");
    }

    private void setupWarehouseMenu() {
        addMenuButton("⚠️ Xem cảnh báo tồn kho", "/com/pharmacy/views/warehouse/stock-alert.fxml");
        addMenuButton("📥 Quản lý nhập kho", "/com/pharmacy/views/warehouse/stock-in-management.fxml");
        addMenuButton("🏭 Quản lý nhà cung cấp", "/com/pharmacy/views/warehouse/supplier-management.fxml");
        addMenuButton("🔄 Đổi trả hàng cho nhà cung cấp", "/com/pharmacy/views/warehouse/return-to-supplier.fxml");
    }

    private void addMenuButton(String text, String fxmlPath) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-font-size: 14px; -fx-padding: 12 15; " +
                     "-fx-alignment: CENTER_LEFT; -fx-background-color: transparent; " +
                     "-fx-text-fill: white;");
        btn.setOnAction(e -> SceneManager.loadContent(contentArea, fxmlPath));
        sidebarMenu.getChildren().add(btn);
    }

    @FXML
    private void handleLogout() {
        Session.logout();
        try {
            SceneManager.switchScene("/com/pharmacy/views/login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}