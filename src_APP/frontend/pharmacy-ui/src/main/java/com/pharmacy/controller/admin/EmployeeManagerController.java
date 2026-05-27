package com.pharmacy.controller.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pharmacy.dto.ApiResponse;
import com.pharmacy.dto.EmployeeResponse;
import com.pharmacy.model.Employee;
import com.pharmacy.util.ApiService;
import com.pharmacy.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow; // BẮT BUỘC PHẢI CÓ IMPORT NÀY ĐỂ KHÔNG BỊ LỖI
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeManagerController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Employee> tableEmployee;
    @FXML private TableColumn<Employee, String> colId;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colPosition;
    @FXML private TableColumn<Employee, String> colPhone;
    @FXML private TableColumn<Employee, String> colStatus;
    @FXML private Label lblTotalEmployees;

    // Danh sách gốc chứa toàn bộ dữ liệu
    private ObservableList<Employee> employeeList;

    @FXML
    public void initialize() {
        System.out.println("✅ Đã load trang Quản Lý Nhân Sự");
        
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colPosition.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // ===============================================
        // BÔI XÁM DÒNG NHÂN VIÊN ĐÃ NGHỈ (RESIGNED)
        // ===============================================
        tableEmployee.setRowFactory(tv -> new TableRow<Employee>() {
            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if ("RESIGNED".equalsIgnoreCase(item.getStatus())) {
                        // Màu xám nhạt cho background và làm mờ chữ
                        setStyle("-fx-background-color: #f1f5f9; -fx-opacity: 0.6;"); 
                    } else {
                        setStyle(""); // Màu mặc định
                    }
                }
            }
        });

        employeeList = FXCollections.observableArrayList();
        setupSearch();

        loadEmployeeData("");
    }

    private void setupSearch() {
        FilteredList<Employee> filteredData = new FilteredList<>(employeeList, employee -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            loadEmployeeData(newValue);
        });

        SortedList<Employee> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableEmployee.comparatorProperty());
        tableEmployee.setItems(sortedData);
    }

    @FXML
    void openAddEmployeeForm(ActionEvent event) {
        loadForm(event, "/com/pharmacy/views/admin/add-employee.fxml");
    }

    // ===============================================
    // HÀM XỬ LÝ XÓA MỀM (ĐỔI TRẠNG THÁI) TRỰC TIẾP TRÊN BẢNG
    // ===============================================
    @FXML
    void handleDeleteEmployee(ActionEvent event) {
        Employee selectedEmployee = tableEmployee.getSelectionModel().getSelectedItem();
        
        if (selectedEmployee == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn nhân sự cần chuyển trạng thái!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if ("RESIGNED".equalsIgnoreCase(selectedEmployee.getStatus())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Nhân viên này đã ở trạng thái nghỉ việc!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
            "Xác nhận cho thôi việc nhân viên: " + selectedEmployee.getName() + " (" + selectedEmployee.getId() + ")?\n" +
            "Hệ thống sẽ chuyển trạng thái người này sang NGHỈ VIỆC (RESIGNED).", 
            ButtonType.YES, ButtonType.NO);
        
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            
            // Gọi đúng API chuẩn của backend: DELETE /api/admin/employees/{id}
            String endpoint = "/api/admin/employees/" + selectedEmployee.getId();
            
            ApiService.delete(endpoint).thenAccept(res -> {
                javafx.application.Platform.runLater(() -> {
                    // Trả về 200 OK từ ApiResponse của Backend
                    if (res.statusCode() == 200 || res.statusCode() == 204) {
                        Alert success = new Alert(Alert.AlertType.INFORMATION, "Đã chuyển trạng thái nhân sự thành RESIGNED thành công!");
                        success.showAndWait();
                        
                        // Gọi lại hàm load dữ liệu để bảng giật status sang RESIGNED ngay lập tức
                        loadEmployeeData(txtSearch.getText()); 
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Lỗi từ Backend: " + res.statusCode() + " - " + res.body());
                        error.showAndWait();
                    }
                });
            });
        }
    }

    private void loadForm(ActionEvent event, String fxmlPath) {
        try {
            Node source = (Node) event.getSource();
            AnchorPane adminContentPane = (AnchorPane) source.getScene().lookup("#contentPane");

            if (adminContentPane != null) {
                SceneManager.loadContent(adminContentPane, fxmlPath);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi load form: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void loadEmployeeData(String keyword) {
        if (keyword == null) {
            keyword = "";
        }

        String endpoint = "/api/admin/employees";
        if (!keyword.isBlank()) {
            String encoded = URLEncoder.encode(keyword.trim(), StandardCharsets.UTF_8);
            endpoint += "/search?keyword=" + encoded;
        }

        ApiService.get(endpoint)
                .thenApply(HttpResponse::body)
                .thenAccept(jsonResponseBody -> {
                    try {
                        ApiResponse<List<EmployeeResponse>> apiRes = ApiService.mapper.readValue(
                                jsonResponseBody,
                                new TypeReference<ApiResponse<List<EmployeeResponse>>>() {}
                        );

                        if (apiRes != null && apiRes.getStatus() == 200 && apiRes.getData() != null) {
                            List<Employee> serverEmployees = apiRes.getData().stream()
                                    .map(this::toEmployeeModel)
                                    .collect(Collectors.toList());

                            // ===============================================
                            // THUẬT TOÁN ĐẨY RESIGNED XUỐNG CUỐI DANH SÁCH
                            // ===============================================
                            serverEmployees.sort((e1, e2) -> {
                                boolean e1Resigned = "RESIGNED".equalsIgnoreCase(e1.getStatus());
                                boolean e2Resigned = "RESIGNED".equalsIgnoreCase(e2.getStatus());
                                
                                if (e1Resigned && !e2Resigned) return 1;  // e1 nghỉ thì đẩy xuống dưới
                                if (!e1Resigned && e2Resigned) return -1; // e2 nghỉ thì đẩy e1 lên trên
                                
                                // Nếu cùng trạng thái thì sắp xếp theo Mã NV (A-Z)
                                return e1.getId().compareToIgnoreCase(e2.getId());
                            });

                            javafx.application.Platform.runLater(() -> {
                                employeeList.setAll(serverEmployees);
                                updateTotals();
                            });
                        } else {
                            System.err.println("Lỗi backend load nhân viên: " + (apiRes != null ? apiRes.getMessage() : "null response"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private Employee toEmployeeModel(EmployeeResponse response) {
        return new Employee(
                response.getManv(),
                response.getTennv(),
                response.getChucvu(),
                response.getSdt(),
                response.getTrangthai()
        );
    }

    private void updateTotals() {
        if (lblTotalEmployees != null) {
            lblTotalEmployees.setText(String.valueOf(employeeList.size()));
        }
    }
}