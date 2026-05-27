package com.pharmacy.controller.warehouse;

import com.pharmacy.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton; 
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

public class WarehouseController {

    @FXML private AnchorPane contentPane;
    @FXML private Label lblTitle;
    @FXML private VBox sideMenu;
    @FXML private Label lblWarehouseName;
    @FXML private Button btnInventory;
    @FXML private Button btnReturn; 
    @FXML private Button btnSupplier; // Nút Quản lý Nhà cung cấp
    @FXML private MenuButton avatarMenuButton; 
    
    private Button currentActiveButton;

    @FXML
    public void initialize() {
        System.out.println("✅ Khởi tạo giao diện Warehouse (Nhân viên kho)...");
        
        // CẬP NHẬT TÊN LÊN HEADER
        if (lblWarehouseName != null && com.pharmacy.util.Session.getCurrentUser() != null) {
            String fullName = com.pharmacy.util.Session.getFullName();
            if (fullName == null || fullName.trim().isEmpty() || fullName.equalsIgnoreCase("null") || fullName.equals("Unknown User")) {
                String phone = com.pharmacy.util.Session.getCurrentUser().getUsername();
                if (phone != null && phone.length() >= 4) {
                    lblWarehouseName.setText("User" + phone.substring(phone.length() - 4));
                } else {
                    lblWarehouseName.setText("Nhân viên kho");
                }
            } else {
                lblWarehouseName.setText(fullName);
            }
        }

        loadView("inventory.fxml", "QUẢN LÝ KHO");
        
        if (btnInventory != null) {
            setActiveButtonStyle(btnInventory);
        }
    }

    @FXML
    void showInventoryManager(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "inventory.fxml", "QUẢN LÝ KHO");
    }

    @FXML
    void showReturnManager(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "return-manager.fxml", "QUẢN LÝ ĐỔI TRẢ");
    }

    // ==== HÀM MỚI THÊM CHO QUẢN LÝ NHÀ CUNG CẤP ====
    @FXML
    void showSupplierManager(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "supplier-manager.fxml", "QUẢN LÝ NHÀ CUNG CẤP");
    }

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
        if (lblTitle != null) {
            lblTitle.setText(title);
        }
        
        String path = "/com/pharmacy/views/warehouse/" + fxmlFileName;
        System.out.println("🔄 Đang chuyển sang trang: " + path);
        
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
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(400), contentPane);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
            
        } catch (IOException e) {
            System.err.println("❌ LỖI LOAD FILE: " + fxmlFileName);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        System.out.println("👤 Mở Thông tin tài khoản kho");
        setActiveButtonStyle(null); 
        loadView("profile.fxml", "THÔNG TIN TÀI KHOẢN");
    }

    @FXML
    private void handleChangePassword(ActionEvent event) {
        System.out.println("🔑 Mở Đổi mật khẩu kho");
        setActiveButtonStyle(null);
        loadView("change-password.fxml", "ĐỔI MẬT KHẨU");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        System.out.println("🚪 Đăng xuất...");
        try {
            SceneManager.switchScene("/com/pharmacy/views/login.fxml");
            System.out.println("✅ Đã chuyển về màn hình đăng nhập thành công!");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi load màn hình login!");
            e.printStackTrace();
        }
    }
}