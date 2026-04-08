module com.pharmacy {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    opens com.pharmacy to javafx.fxml;
    exports com.pharmacy;
}