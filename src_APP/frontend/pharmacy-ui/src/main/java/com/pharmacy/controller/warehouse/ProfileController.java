package com.pharmacy.controller.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmacy.util.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.time.LocalDate;

public class ProfileController {

    @FXML private Label lblFullNameCard;
    @FXML private TextField txtName;
    @FXML private ComboBox<String> cbGender;
    @FXML private DatePicker dpBirthDate;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtRole;
    @FXML private TextField txtJoinDate;

    @FXML
    public void initialize() {
        System.out.println("✅ Đang nạp dữ liệu vào trang Profile...");
        cbGender.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        
        ApiService.get("/api/profile/me").thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode data = ApiService.mapper.readTree(response.body()).get("data");

                        String hoten = data.get("hoten").asText();
                        lblFullNameCard.setText(hoten.toUpperCase());
                        txtName.setText(hoten);
                        txtPhone.setText(data.get("sdt").asText());
                        txtRole.setText(data.get("vaitro").asText());
                        
                        if (data.has("email") && !data.get("email").isNull()) {
                            txtEmail.setText(data.get("email").asText());
                        }

                        if (data.has("gioitinh") && !data.get("gioitinh").isNull()) {
                            cbGender.setValue(data.get("gioitinh").asText());
                        }

                        if (data.has("ngaysinh") && !data.get("ngaysinh").isNull()) {
                            String dobStr = data.get("ngaysinh").asText().split("T")[0];
                            dpBirthDate.setValue(LocalDate.parse(dobStr));
                        }

                        if (data.has("ngayvaolam") && !data.get("ngayvaolam").isNull()) {
                            txtJoinDate.setText(data.get("ngayvaolam").asText().split("T")[0]);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    @FXML
    void goToEditProfile(ActionEvent event) {
        try {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            javafx.scene.layout.AnchorPane adminContentPane = 
                (javafx.scene.layout.AnchorPane) source.getScene().lookup("#contentPane");

            if (adminContentPane != null) {
                com.pharmacy.util.SceneManager.loadContent(adminContentPane, "/com/pharmacy/views/warehouse/edit-profile.fxml");
            } else {
                System.err.println("❌ Không tìm thấy AnchorPane tổng để chuyển trang!");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi mở trang Chỉnh sửa thông tin!");
            e.printStackTrace();
        }
    }
}