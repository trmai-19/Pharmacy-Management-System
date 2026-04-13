package com.pharmacy.util;

import com.pharmacy.ThemeService;
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
        System.out.println("✅ Load FXML thành công: " + fxmlPath);

        AnchorPane wrapper = new AnchorPane(root);
        ThemeService.applyTheme(wrapper);

        Scene scene = new Scene(wrapper);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void loadContent(AnchorPane contentPane, String fxmlPath) {
    try {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent content = loader.load();

        // Xóa hết nội dung cũ trước khi thêm mới
        contentPane.getChildren().clear();
        contentPane.getChildren().add(content);

        System.out.println("✅ Đã load nội dung: " + fxmlPath);
    } catch (IOException e) {
        System.err.println("❌ Không load được file: " + fxmlPath);
        e.printStackTrace();
    }
}
}
