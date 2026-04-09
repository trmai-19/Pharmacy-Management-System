module com.pharmacy {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires java.net.http;
    opens com.pharmacy to javafx.fxml;
    exports com.pharmacy;
}