package com.pharmacy.controller.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pharmacy.dto.ApiResponse;
import com.pharmacy.dto.CustomerResponse;
import com.pharmacy.dto.QuickCreateCustomerRequest;
import com.pharmacy.model.Customer;
import com.pharmacy.util.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerManagerController {

    @FXML private Label lblTotalCustomers;
    @FXML private Label lblVIPCustomers;
    @FXML private Label lblSpecialNotes;
    

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbTier;

    @FXML private TableView<Customer> tableCustomer;
    @FXML private TableColumn<Customer, String> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colTier;
    @FXML private TableColumn<Customer, String> colPoints;
    @FXML private TableColumn<Customer, String> colTotalSpent;

    @FXML private StackPane modalOverlay;
    @FXML private TextField txtNewName;
    @FXML private TextField txtNewPhone;
    @FXML private ComboBox<String> cbNewGender;
    @FXML private DatePicker dpNewDOB;

    private ObservableList<Customer> customerList;
    private FilteredList<Customer> filteredData;

    @FXML
    public void initialize() {
        System.out.println("🤝 CustomerManagerController đang tải...");

        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        colTier.setCellValueFactory(cellData -> cellData.getValue().tierProperty());
        colPoints.setCellValueFactory(cellData -> cellData.getValue().pointsProperty());
        colTotalSpent.setCellValueFactory(cellData -> cellData.getValue().totalSpentProperty());

        cbTier.setItems(FXCollections.observableArrayList(
                "Tất cả hạng mức", "Thành viên", "Bạc", "Vàng", "Kim Cương"
        ));
        cbTier.getSelectionModel().selectFirst();

        customerList = FXCollections.observableArrayList();
        setupSearchAndFilter();

        loadCustomerData("");

        cbNewGender.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
    }

    private void loadCustomerData(String keyword) {
        if (customerList == null) {
            customerList = FXCollections.observableArrayList();
        }

        String encodedKeyword = "";
        try {
            if (keyword != null && !keyword.trim().isEmpty()) {
                encodedKeyword = URLEncoder.encode(keyword.trim(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            encodedKeyword = "";
        }

        String endpoint = "/api/sales/customers/search" + (encodedKeyword.isEmpty() ? "" : "?keyword=" + encodedKeyword);

        ApiService.get(endpoint)
                .thenApply(HttpResponse::body)
                .thenAccept(jsonResponseBody -> {
                    try {
                        ApiResponse<List<CustomerResponse>> apiRes = ApiService.mapper.readValue(
                                jsonResponseBody,
                                new TypeReference<ApiResponse<List<CustomerResponse>>>() {}
                        );

                        if (apiRes != null && apiRes.getStatus() == 200 && apiRes.getData() != null) {
                            List<Customer> serverCustomers = apiRes.getData().stream()
                                    .map(this::toCustomerModel)
                                    .collect(Collectors.toList());

                            Platform.runLater(() -> {
                                customerList.setAll(serverCustomers);
                                updateStats();
                            });
                        } else {
                            String msg = (apiRes != null) ? apiRes.getMessage() : "Lỗi không xác định";
                            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi dữ liệu", msg));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi xử lý", "Không thể đọc dữ liệu khách hàng từ Backend."));
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể kết nối đến Backend để tải khách hàng!"));
                    return null;
                });
    }

    private Customer toCustomerModel(CustomerResponse response) {
        String formattedPoints = response.getDiemtichluy() == 0 ? "0" : String.valueOf((int) response.getDiemtichluy());
        String formattedTotal = response.getTongdoanhthu() == null ? "0" : String.format("%.0f", response.getTongdoanhthu());
        String tier = response.getHangtv() == null ? "Thành viên" : normalizeTier(response.getHangtv());

        return new Customer(
                response.getMakh() != null ? response.getMakh() : "",
                response.getTenkh() != null ? response.getTenkh() : "N/A",
                response.getSdt() != null ? response.getSdt() : "",
                tier,
                formattedPoints,
                formattedTotal,
                ""
        );
    }

    private void setupSearchAndFilter() {
        filteredData = new FilteredList<>(customerList, this::matchesCurrentFilter);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            loadCustomerData(newValue);
            updateFilter();
        });

        cbTier.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        SortedList<Customer> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableCustomer.comparatorProperty());
        tableCustomer.setItems(sortedData);
    }

    private void updateFilter() {
        filteredData.setPredicate(this::matchesCurrentFilter);
    }

    private boolean matchesCurrentFilter(Customer customer) {
        String searchText = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase().trim();
        String selectedTier = cbTier.getValue();

        boolean matchesTier = selectedTier == null || selectedTier.equals("Tất cả hạng mức") || customer.getTier().equals(selectedTier);
        boolean matchesSearch = searchText.isEmpty() || customer.getName().toLowerCase().contains(searchText) || customer.getPhone().contains(searchText);

        return matchesTier && matchesSearch;
    }

    private String normalizeTier(String rawTier) {
        if (rawTier == null || rawTier.isBlank()) {
            return "Thành viên";
        }
        String normalized = rawTier.trim().toUpperCase();
        if (normalized.contains("THANH")) {
            return "Thành viên";
        }
        if (normalized.contains("BAC")) {
            return "Bạc";
        }
        if (normalized.contains("VANG")) {
            return "Vàng";
        }
        if (normalized.contains("KIM")) {
            return "Kim Cương";
        }
        return Character.toUpperCase(rawTier.charAt(0)) + rawTier.substring(1).toLowerCase();
    }

    private void updateStats() {
        if (lblTotalCustomers != null) {
            lblTotalCustomers.setText(String.valueOf(customerList.size()));
        }
        if (lblVIPCustomers != null) {
            long vipCount = customerList.stream()
                    .filter(c -> c.getTier() != null && (c.getTier().equalsIgnoreCase("Vàng") || c.getTier().equalsIgnoreCase("Kim Cương")))
                    .count();
            lblVIPCustomers.setText(String.valueOf(vipCount));
        }
        if (lblSpecialNotes != null) {
            lblSpecialNotes.setText("Hiển thị " + customerList.size() + " khách hàng");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void handleShowCreateForm(ActionEvent event) {
        modalOverlay.setVisible(true);
    }

    @FXML
    void handleCloseCreateForm(ActionEvent event) {
        modalOverlay.setVisible(false);
        clearCreateForm();
    }

@FXML
    void handleSaveCustomer(ActionEvent event) {
        String name = txtNewName.getText();
        String phone = txtNewPhone.getText();
        String gender = cbNewGender.getValue();
        LocalDate dob = dpNewDOB.getValue();

        if (name == null || name.trim().isEmpty() || 
            phone == null || phone.trim().isEmpty() || 
            gender == null || 
            dob == null) {
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText("Thông tin không đầy đủ");
            alert.setContentText("Vui lòng nhập đầy đủ: Họ tên, Số điện thoại, Giới tính và Ngày sinh!");
            alert.showAndWait();
            return; 
        }

        LocalDate today = LocalDate.now();
        
        if (dob.isAfter(today)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText("Ngày sinh không hợp lệ");
            alert.setContentText("Ngày sinh không thể lớn hơn ngày hiện tại!");
            alert.showAndWait();
            return;
        }
        
        if (today.getYear() - dob.getYear() > 120) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText("Ngày sinh không hợp lệ");
            alert.setContentText("Hệ thống phát hiện số tuổi lớn hơn 120. Vui lòng kiểm tra lại năm sinh!");
            alert.showAndWait();
            return;
        }

        QuickCreateCustomerRequest request = new QuickCreateCustomerRequest(name, phone, gender);
        String requestBody;
        try {
            requestBody = ApiService.mapper.writeValueAsString(request);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi nội bộ");
            alert.setHeaderText(null);
            alert.setContentText("Không thể tạo dữ liệu request gửi lên backend.");
            alert.showAndWait();
            return;
        }

        ApiService.post("/api/sales/customers/quick-create", requestBody)
                .thenApply(HttpResponse::body)
                .thenAccept(jsonResponseBody -> {
                    try {
                        ApiResponse<CustomerResponse> apiRes = ApiService.mapper.readValue(
                                jsonResponseBody,
                                new com.fasterxml.jackson.core.type.TypeReference<ApiResponse<CustomerResponse>>() {}
                        );

                        if (apiRes != null && apiRes.getStatus() == 200 && apiRes.getData() != null) {
                            Customer created = toCustomerModel(apiRes.getData());
                            Platform.runLater(() -> {
                                customerList.add(created);
                                updateStats();

                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                successAlert.setTitle("Thành công");
                                successAlert.setHeaderText(null);
                                successAlert.setContentText("Đã tạo hồ sơ thành công cho khách hàng: " + name);
                                successAlert.showAndWait();

                                modalOverlay.setVisible(false);
                                clearCreateForm();
                            });
                        } else {
                            String msg = (apiRes != null) ? apiRes.getMessage() : "Lỗi không xác định";
                            Platform.runLater(() -> {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Lỗi backend");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText(msg);
                                errorAlert.showAndWait();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Lỗi xử lý");
                            errorAlert.setHeaderText(null);
                            errorAlert.setContentText("Không thể đọc phản hồi từ backend.");
                            errorAlert.showAndWait();
                        });
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Lỗi kết nối");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Không thể liên hệ với backend để tạo khách hàng.");
                        errorAlert.showAndWait();
                    });
                    return null;
                });
    }

    private void clearCreateForm() {
        txtNewName.clear();
        txtNewPhone.clear();
        cbNewGender.getSelectionModel().clearSelection();
        dpNewDOB.setValue(null);
    }
}