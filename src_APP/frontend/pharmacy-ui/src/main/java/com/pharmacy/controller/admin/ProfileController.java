package com.pharmacy.controller.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmacy.util.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class ProfileController {

    @FXML private Label lblFullNameCard;
    @FXML private TextField txtName, txtPhone, txtEmail, txtRole, txtJoinDate;
    @FXML private ComboBox<String> cbGender;
    @FXML private DatePicker dpBirthDate;

    @FXML
    public void initialize() {
        cbGender.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        loadProfileData();
    }

    private void loadProfileData() {
        ApiService.get("/api/profile/me").thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode root = ApiService.mapper.readTree(response.body());
                        JsonNode data = root.get("data");

                        // Đổ dữ liệu vào giao diện
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
                            String joinDateStr = data.get("ngayvaolam").asText().split("T")[0];
                            txtJoinDate.setText(joinDateStr);
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
            javafx.scene.Scene currentScene = txtName.getScene();
            javafx.scene.layout.AnchorPane salesContentPane = (javafx.scene.layout.AnchorPane) currentScene.lookup("#contentPane");
            if (salesContentPane != null) {
                com.pharmacy.util.SceneManager.loadContent(salesContentPane, "/com/pharmacy/views/sales/edit-profile.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}