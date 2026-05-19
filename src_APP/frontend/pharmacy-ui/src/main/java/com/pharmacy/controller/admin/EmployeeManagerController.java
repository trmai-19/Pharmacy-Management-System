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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
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

    @FXML
    void openDeleteEmployeeForm(ActionEvent event) {
        loadForm(event, "/com/pharmacy/views/admin/delete-employee.fxml");
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