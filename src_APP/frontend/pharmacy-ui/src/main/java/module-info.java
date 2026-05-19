module com.pharmacy {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.net.http;
    requires transitive javafx.base;
    requires com.google.gson;
    opens com.pharmacy.dto to com.google.gson;

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    // Mở tất cả package chứa Controller cho JavaFX FXMLLoader
    opens com.pharmacy.controller.admin to javafx.fxml, javafx.base;
    opens com.pharmacy.controller.common to javafx.fxml;   // nếu có

    // Mở package chứa LoginController (vì nó cũng là controller)
    opens com.pharmacy to javafx.fxml;
    opens com.pharmacy.controller.warehouse;
    opens com.pharmacy.controller.sales to javafx.fxml;

    // Mở các package chứa DTO và Model để Jackson có thể truy cập (Reflection)
    
    opens com.pharmacy.model to com.fasterxml.jackson.databind;

    // Export các package cần thiết
    exports com.pharmacy;
    exports com.pharmacy.util;
    exports com.pharmacy.model;
    exports com.pharmacy.dto;
}