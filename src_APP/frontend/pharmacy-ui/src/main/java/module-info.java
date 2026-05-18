module com.pharmacy {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.net.http;
    requires transitive javafx.base;
<<<<<<< HEAD
    requires com.google.gson;
    // Mở folder dto cho Gson dùng reflection để gán dữ liệu
    opens com.pharmacy.dto to com.google.gson;

=======
    requires com.fasterxml.jackson.databind;
>>>>>>> d0d91036713e81c30bf5e178ecc766bae107245c
    // Mở tất cả package chứa Controller cho JavaFX FXMLLoader
    opens com.pharmacy.controller.admin to javafx.fxml, javafx.base;
    opens com.pharmacy.controller.common to javafx.fxml;   // nếu có

    // Mở package chứa LoginController (vì nó cũng là controller)
    opens com.pharmacy to javafx.fxml;
    opens com.pharmacy.controller.warehouse;
    // Nếu sau này có controller ở package khác, thêm tiếp:
    opens com.pharmacy.controller.sales to javafx.fxml;
    //opens com.pharmacy.controller.warehouse to javafx.fxml;

    // Export các package cần thiết
    exports com.pharmacy;
    exports com.pharmacy.util;
    exports com.pharmacy.model;

    // exports com.pharmacy.controller.admin;   // không bắt buộc, nhưng có thể thêm
}