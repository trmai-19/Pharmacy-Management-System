// package com.pharmacy;

// import java.io.IOException;

// import javafx.application.Application;
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.stage.Stage;

// /**
//  * JavaFX App
//  */
// public class App extends Application {

//     private static Scene scene;

//     @Override
//     public void start(Stage stage) throws IOException {
//         scene = new Scene(loadFXML("login"), 640, 480);
//         stage.setScene(scene);
//         stage.setTitle("Pharmacy-Management-System");
//         stage.show();
//     }

//     static void setRoot(String fxml) throws IOException {
//         scene.setRoot(loadFXML(fxml));
//     }

//     private static Parent loadFXML(String fxml) throws IOException {
//         FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
//         return fxmlLoader.load();
//     }

//     public static void main(String[] args) {
//         launch();
//     }
// }



package com.pharmacy;

import com.pharmacy.util.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    @Override
public void start(Stage stage) throws IOException {
    SceneManager.setPrimaryStage(stage);

    FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/pharmacy/views/login.fxml"));
    
    if (loader.getLocation() == null) {
        System.err.println("❌ Không tìm thấy login.fxml!");
        return;
    }

    Parent root = loader.load();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle("Pharmacy Management System");
    stage.show();
}

    public static void main(String[] args) {
        launch(args);
    }
}