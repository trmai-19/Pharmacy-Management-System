package com.pharmacy.controller.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;           // ← Thêm
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import java.io.IOException;
import java.net.URL;
import javafx.scene.layout.Region;

public class AdminController {

    @FXML private AnchorPane contentPane;
    @FXML private Label lblTitle;
    @FXML private VBox sideMenu;
    @FXML private Label lblAdminName;
    @FXML private Button btnDashboard;
    private ContextMenu notificationMenu;   

    // Thêm fx:id cho chuông thông báo (đã có trong FXML)
    @FXML private StackPane bellIcon;
    @FXML private Label lblNotificationCount;

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

    // ====================== CÁC HÀM MENU ======================
    @FXML
    void showDashboard(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "dashboard.fxml", "BẢNG ĐIỀU KHIỂN TỔNG QUAN");
    }

    @FXML
    void showProductManager(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "product-management.fxml", "QUẢN LÝ SẢN PHẨM");
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

    @FXML
    void showRoleManager(ActionEvent event) {
        handleMenuClick((Button) event.getSource(), "role-manager.fxml", "QUẢN LÝ QUYỀN");
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
        clickedButton.setStyle("-fx-background-color: rgba(28, 201, 183, 0.85); -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20; -fx-font-weight: bold;");
        currentActiveButton = clickedButton;
    }

    private void loadView(String fxmlFileName, String title) {
        if (lblTitle != null) {
            lblTitle.setText(title);
        }
        
        String path = "/com/pharmacy/views/admin/" + fxmlFileName;
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
            
            // Hiệu ứng fade
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
void handleShowNotifications(MouseEvent event) {
    // Ẩn badge thông báo
    if (lblNotificationCount != null) {
        lblNotificationCount.setText("0");
        lblNotificationCount.getParent().setVisible(false);
    }

    // Toggle
    if (notificationMenu != null && notificationMenu.isShowing()) {
        notificationMenu.hide();
        notificationMenu = null;
        return;
    }

    // ===== CONTEXT MENU =====
    notificationMenu = new ContextMenu();
    notificationMenu.setStyle("-fx-background-color: transparent; "
            + "-fx-padding: 0; "
            + "-fx-focus-color: transparent; "
            + "-fx-faint-focus-color: transparent; "
            + "-fx-border-color: transparent;"
        + "-fx-border-width: 0;");

    VBox popupContainer = new VBox();
    popupContainer.setFocusTraversable(false); // 🔥 QUAN TRỌNG
    popupContainer.setStyle("-fx-background-color: white; "
            + "-fx-background-radius: 12; "
            + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.12), 15, 0, 0, 6); "
            + "-fx-border-radius: 12; "
            + "-fx-border-color: #e2e8f0; "
            + "-fx-border-width: 1; "
            + "-fx-focus-color: transparent; "
            + "-fx-faint-focus-color: transparent;");

    // Header
    HBox headerBox = new HBox();
    headerBox.setFocusTraversable(false);
    headerBox.setAlignment(Pos.CENTER_LEFT);
    headerBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 11 11 0 0; -fx-padding: 15 20; "
            + "-fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");

    Label headerLabel = new Label("Thông báo hệ thống");
    headerLabel.setStyle("-fx-font-weight: 800; -fx-font-size: 16px; -fx-text-fill: #0f766e;");
    headerBox.getChildren().add(headerLabel);

    // List
    VBox notifList = new VBox(0);
    notifList.setFocusTraversable(false);
    notifList.setStyle("-fx-background-color: white;");

    notifList.getChildren().add(createNotificationRow("CẢNH BÁO", "Thuốc Paracetamol 500mg sắp hết (Còn 3 hộp).", "Vừa xong", "WARNING"));
    notifList.getChildren().add(createNotificationRow("THÀNH CÔNG", "Đơn hàng #DH1024 đã thanh toán thành công.", "2 phút trước", "SUCCESS"));
    notifList.getChildren().add(createNotificationRow("HỆ THỐNG", "Bản cập nhật phần mềm Pharmacy v2.0 đã sẵn sàng.", "1 giờ trước", "INFO"));
    notifList.getChildren().add(createNotificationRow("CẢNH BÁO", "Lô hàng Vitamin C sẽ hết hạn sau 30 ngày nữa.", "2 giờ trước", "WARNING"));

    for (int i = 5; i <= 10; i++) {
        notifList.getChildren().add(createNotificationRow("THÀNH CÔNG", "Đơn hàng #DH" + (1000 + i) + " đã hoàn tất.", i + " giờ trước", "SUCCESS"));
    }

    ScrollPane scrollPane = new ScrollPane(notifList);
    scrollPane.setFocusTraversable(false); // 🔥 QUAN TRỌNG
    scrollPane.setPrefWidth(380);
    scrollPane.setPrefHeight(420);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setStyle("-fx-background-color: white; -fx-background: white; -fx-border-color: transparent;");

    // Footer
    HBox footerBox = new HBox();
    footerBox.setFocusTraversable(false);
    footerBox.setAlignment(Pos.CENTER);
    footerBox.setStyle("-fx-background-color: white; -fx-background-radius: 0 0 11 11; -fx-padding: 12; "
            + "-fx-border-color: #f1f5f9; -fx-border-width: 1 0 0 0; -fx-cursor: hand;");

    popupContainer.getChildren().addAll(headerBox, scrollPane, footerBox);

    CustomMenuItem customItem = new CustomMenuItem(popupContainer, false);
popupContainer.setFocusTraversable(false);    customItem.setStyle("-fx-background-color: transparent; -fx-padding: 0;" + "-fx-focus-color: transparent; "
  + "-fx-faint-focus-color: transparent;");

    notificationMenu.getItems().add(customItem);

    notificationMenu.show(bellIcon, event.getScreenX() - 340, event.getScreenY() + 15);

    notificationMenu.setOnHidden(e -> notificationMenu = null);
}
    /**
     * Hàm phụ trợ tạo một dòng thông báo có Avatar Trạng Thái (Indicator)
     */
    private HBox createNotificationRow(String title, String content, String timeStr, String type) {
        HBox container = new HBox(15); // Khoảng cách giữa icon và chữ
        container.setAlignment(Pos.TOP_LEFT);
        container.setStyle("-fx-padding: 16 20; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0; -fx-cursor: hand; -fx-background-color: white;");

        // 1. CHỈ BÁO TRẠNG THÁI (Màu sắc)
        Color dotColor;
        String bgDotColor;
        switch (type) {
            case "WARNING": // Cảnh báo (Đỏ/Cam)
                dotColor = Color.valueOf("#ef4444");
                bgDotColor = "#fee2e2";
                break;
            case "SUCCESS": // Thành công (Xanh lá)
                dotColor = Color.valueOf("#10b981");
                bgDotColor = "#d1fae5";
                break;
            default:        // Thông tin (Xanh dương)
                dotColor = Color.valueOf("#3b82f6");
                bgDotColor = "#dbeafe";
                break;
        }

        // Tạo vòng tròn nền nhạt bọc lấy dấu chấm đậm (giống UI Web hiện đại)
        StackPane iconPane = new StackPane();
        iconPane.setStyle("-fx-background-color: " + bgDotColor + "; -fx-background-radius: 50;");
        iconPane.setPrefSize(32, 32);
        iconPane.setMinSize(32, 32);
        Circle dot = new Circle(5, dotColor);
        iconPane.getChildren().add(dot);

        // 2. NỘI DUNG CHỮ
        VBox textContainer = new VBox(4);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + dotColor.toString().replace("0x", "#") + ";");

        Label lblContent = new Label(content);
        lblContent.setWrapText(true);
        lblContent.setMaxWidth(290);
        lblContent.setStyle("-fx-font-size: 14px; -fx-text-fill: #334155; -fx-line-spacing: 2px;"); // Thêm line-spacing cho dễ đọc

        Label lblTime = new Label(timeStr);
        lblTime.setStyle("-fx-font-size: 11.5px; -fx-text-fill: #94a3b8;");

        textContainer.getChildren().addAll(lblTitle, lblContent, lblTime);

        container.getChildren().addAll(iconPane, textContainer);

        // HIỆU ỨNG HOVER NÂNG CAO (Đổi màu viền trái khi lướt qua)
        container.setOnMouseEntered(e -> container.setStyle("-fx-padding: 16 20; -fx-border-color: " + dotColor.toString().replace("0x", "#") + " transparent #f1f5f9 transparent; -fx-border-width: 0 0 1 4; -fx-cursor: hand; -fx-background-color: #f8fafc;"));
        container.setOnMouseExited(e -> container.setStyle("-fx-padding: 16 20; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0; -fx-cursor: hand; -fx-background-color: white;"));
        container.setFocusTraversable(false); // 🔥 FIX KHUNG XANH
        return container;
    }
        // ====================== XỬ LÝ MENU AVATAR ======================
    @FXML
    private void handleViewProfile(ActionEvent event) {
        System.out.println("👤 Mở Thông tin tài khoản");
        // TODO: Mở popup hoặc load view thông tin cá nhân
            loadView("profile.fxml", "THÔNG TIN TÀI KHOẢN");
    }

    @FXML
    private void handleChangePassword(ActionEvent event) {
        System.out.println("🔑 Mở Đổi mật khẩu");
        // TODO: Mở form đổi mật khẩu (có thể dùng Dialog)
        loadView("change-password.fxml", "ĐỔI MẬT KHẨU");
    }

@FXML
    void handleLogout(ActionEvent event) {
        System.out.println("🚪 Đăng xuất...");
        try {
            // Tận dụng Tuyệt chiêu SceneManager chúng ta đã viết
            com.pharmacy.util.SceneManager.switchScene("/com/pharmacy/views/login.fxml");
            System.out.println("✅ Đã chuyển về màn hình đăng nhập thành công!");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi load màn hình login!");
            e.printStackTrace();
        }
    }
}