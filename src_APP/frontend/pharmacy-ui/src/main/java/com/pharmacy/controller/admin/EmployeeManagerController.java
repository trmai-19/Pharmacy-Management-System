package com.pharmacy.controller.admin;

import com.pharmacy.model.Employee;
import com.pharmacy.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;

public class EmployeeManagerController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Employee> tableEmployee;
    @FXML private TableColumn<Employee, String> colId;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colPosition;
    @FXML private TableColumn<Employee, String> colPhone;
    @FXML private TableColumn<Employee, String> colStatus;

    // Danh sách gốc chứa toàn bộ dữ liệu
    private ObservableList<Employee> employeeList;

    @FXML
    public void initialize() {
        System.out.println("✅ Đã load trang Quản Lý Nhân Sự");
        
        // 1. Ánh xạ cột
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colPosition.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // 2. Load dữ liệu
        loadMockData();

        // 3. Kích hoạt bộ máy tìm kiếm Real-time
        setupSearch();
    }

    /**
     * 🔍 BỘ MÁY TÌM KIẾM THEO THỜI GIAN THỰC (REAL-TIME FILTERING)
     */
    private void setupSearch() {
        FilteredList<Employee> filteredData = new FilteredList<>(employeeList, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(employee -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (employee.getId().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } 
                else if (employee.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                }
                else if (employee.getPhone().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });

        SortedList<Employee> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableEmployee.comparatorProperty());
        tableEmployee.setItems(sortedData);
    }

    /**
     * Chuyển hướng sang giao diện thêm nhân viên
     */
    @FXML
    void openAddEmployeeForm(ActionEvent event) {
        loadForm(event, "/com/pharmacy/views/admin/add-employee.fxml");
    }

    /**
     * 🔥 Chuyển hướng sang giao diện xóa nhân viên
     */
    @FXML
    void openDeleteEmployeeForm(ActionEvent event) {
        loadForm(event, "/com/pharmacy/views/admin/delete-employee.fxml");
    }

    /**
     * Hàm dùng chung để load content vào giao diện chính
     */
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

    private void loadMockData() {
        employeeList = FXCollections.observableArrayList(
                new Employee("NV001", "Nguyễn Văn Phát", "Trưởng nhóm", "0901234567", "Đang làm việc"),
                new Employee("NV002", "Trần Thị Lan", "Dược sĩ ", "0987654321", "Đang làm việc"),
                new Employee("NV003", "Lê Minh Tuấn", "Thu ngân", "0912223334", "Nghỉ phép"),
                new Employee("NV004", "Phạm Hoàng Sơn", "Kiểm kho", "0944555666", "Đang làm việc")
        );
    }
}