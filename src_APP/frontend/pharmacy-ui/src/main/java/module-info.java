module com.pharmacy {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.net.http;
    requires transitive javafx.base;
    requires com.fasterxml.jackson.databind;
    // Mở tất cả package chứa Controller cho JavaFX FXMLLoader
    opens com.pharmacy.controller.admin to javafx.fxml;
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