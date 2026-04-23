package com.pharmacy.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;

    // 1. Cài đặt Stage chính của ứng dụng (Nên gọi ở file App.java / Main.java lúc start)
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * 2. HÀM CHUYỂN ĐỔI TOÀN BỘ MÀN HÌNH (Ví dụ: Đăng nhập <-> Quên mật khẩu)
     * Sử dụng kỹ thuật "Tráo ruột (setRoot)" để không làm co giật hay đổi kích thước cửa sổ.
     */
    public static void switchScene(String fxmlPath) throws IOException {
        System.out.println("🔄 SceneManager đang tráo đổi giao diện: " + fxmlPath);

        if (primaryStage == null) {
            System.err.println("❌ LỖI: Chưa khởi tạo primaryStage bằng setPrimaryStage()!");
            return;
        }

        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));

        if (loader.getLocation() == null) {
            System.err.println("❌ KHÔNG TÌM THẤY FILE: " + fxmlPath);
            throw new IOException("File FXML không tồn tại: " + fxmlPath);
        }

        Parent newRoot = loader.load();
        Scene currentScene = primaryStage.getScene();

        // THUẬT TOÁN ĐỔI TRANG MƯỢT MÀ
        if (currentScene == null) {
            // Trường hợp ứng dụng vừa mới bật, chưa có Scene nào -> Tạo mới
            Scene scene = new Scene(newRoot);
            primaryStage.setScene(scene);
        } else {
            // Trường hợp đang ở Login sang Quên Pass -> Giữ nguyên Scene, chỉ thay nội dung
            // Việc này giúp khung cửa sổ đứng im 100%, không bị co giật!
            currentScene.setRoot(newRoot);
        }

        primaryStage.setResizable(true);
        primaryStage.show();
        System.out.println("✅ Chuyển scene thành công: " + fxmlPath);
    }

    /**
     * 3. HÀM NẠP NỘI DUNG VÀO 1 VÙNG CỤ THỂ (Ví dụ: Đổi Tab trong trang Admin)
     */
    public static void loadContent(AnchorPane contentPane, String fxmlPath) {
        System.out.println("🔄 SceneManager đang load nội dung vào Pane: " + fxmlPath);
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            
            if (loader.getLocation() == null) {
                System.err.println("❌ KHÔNG TÌM THẤY FILE CHỨC NĂNG: " + fxmlPath);
                return;
            }

            Parent content = loader.load();

            // Ép nội dung mới phải phủ kín cái ContentPane (Dashboard)
            if (content instanceof AnchorPane) {
                AnchorPane.setTopAnchor(content, 0.0);
                AnchorPane.setBottomAnchor(content, 0.0);
                AnchorPane.setLeftAnchor(content, 0.0);
                AnchorPane.setRightAnchor(content, 0.0);
            }

            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);

            System.out.println("✅ Đã load thành công: " + fxmlPath);
            
        } catch (IOException e) {
            System.err.println("❌ Lỗi cấu trúc FXML hoặc lỗi Code trong Controller tại: " + fxmlPath);
            e.printStackTrace();
        }
    }
}