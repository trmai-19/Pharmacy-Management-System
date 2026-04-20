package com.pharmacy.controller.admin;

import com.pharmacy.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class EmployeeManagerController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Employee> tableEmployee;
    @FXML private TableColumn<Employee, String> colId;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colPosition;
    @FXML private TableColumn<Employee, String> colPhone;
    @FXML private TableColumn<Employee, String> colStatus;

    // Danh sách lưu trữ dữ liệu
    private ObservableList<Employee> employeeList;

    @FXML
    public void initialize() {
        // 1. Ánh xạ các cột của TableView với các thuộc tính trong Model
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colPosition.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // 2. Load dữ liệu ảo
        loadMockData();

        // (Sau này đổi thành: loadDataFromOracle();)
    }

    /**
     * Dữ liệu ảo dùng để test UI
     */
    private void loadMockData() {
        employeeList = FXCollections.observableArrayList(
                new Employee("NV001", "Nguyễn Văn Phát", "Trưởng nhóm", "0901234567", "Đang làm việc"),
                new Employee("NV002", "Trần Thị Lan", "Dược sĩ chính", "0987654321", "Đang làm việc"),
                new Employee("NV003", "Lê Minh Tuấn", "Thu ngân", "0912223334", "Nghỉ phép"),
                new Employee("NV004", "Phạm Hoàng Sơn", "Kiểm kho", "0944555666", "Đang làm việc")
        );
        tableEmployee.setItems(employeeList);
    }

    /**
     * TÍCH HỢP ORACLE DATABASE (Dành cho giai đoạn sau)
     */
    /*
    private void loadDataFromOracle() {
        employeeList = FXCollections.observableArrayList();
        String query = "SELECT EMP_ID, FULL_NAME, POSITION, PHONE, STATUS FROM EMPLOYEES";
        
        try (Connection conn = OracleDatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
             
             while (rs.next()) {
                 employeeList.add(new Employee(
                     rs.getString("EMP_ID"),
                     rs.getString("FULL_NAME"),
                     rs.getString("POSITION"),
                     rs.getString("PHONE"),
                     rs.getString("STATUS")
                 ));
             }
             tableEmployee.setItems(employeeList);
             
        } catch (SQLException e) {
             e.printStackTrace();
        }
    }
    */
}