module com.pharmacy {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http;

    // Chỉ mở những package thực sự tồn tại
    opens com.pharmacy to javafx.fxml;
    opens com.pharmacy.views to javafx.fxml;
    opens com.pharmacy.controller.common to javafx.fxml;

    // Export các package chính
    exports com.pharmacy;
    exports com.pharmacy.util;
    exports com.pharmacy.model;
}