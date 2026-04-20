package com.pharmacy.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlPath) throws IOException {
        System.out.println("🔄 SceneManager đang thử load: " + fxmlPath);

        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));

        if (loader.getLocation() == null) {
            System.err.println("❌ KHÔNG TÌM THẤY FILE: " + fxmlPath);
            throw new IOException("File FXML không tồn tại: " + fxmlPath);
        }

        Parent root = loader.load();
        
        // 1. Ép root (nội dung FXML) phải giãn nở hết cỡ trong cửa sổ
        if (root instanceof AnchorPane) {
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
        }

        Scene scene = new Scene(root); // Không cần wrapper AnchorPane nữa để tránh rắc rối layout
        primaryStage.setScene(scene);

        // 2. Ép cửa sổ luôn mở to nhất (Maximize) để tránh bị nhỏ theo Scene trước
        primaryStage.setMaximized(true); 
        
        // 3. Đảm bảo cửa sổ có thể co giãn mượt mà
        primaryStage.setResizable(true);

        primaryStage.show();
        System.out.println("✅ Chuyển scene thành công: " + fxmlPath);
    }

    public static void loadContent(AnchorPane contentPane, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
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

            System.out.println("✅ Đã load nội dung vào Pane: " + fxmlPath);
        } catch (IOException e) {
            System.err.println("❌ Không load được nội dung: " + fxmlPath);
            e.printStackTrace();
        }
    }
}