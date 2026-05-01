package com.pharmacy.controller.admin;

import com.pharmacy.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import com.pharmacy.util.SceneManager;

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
        // 1. Bọc danh sách gốc vào FilteredList (Khởi tạo mặc định là hiển thị tất cả b -> true)
        FilteredList<Employee> filteredData = new FilteredList<>(employeeList, b -> true);

        // 2. Lắng nghe sự thay đổi từng ký tự khi người dùng gõ vào ô tìm kiếm
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(employee -> {
                // Nếu ô tìm kiếm trống rỗng -> Hiển thị lại toàn bộ nhân viên
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Chuyển chữ gõ vào thành chữ thường để so sánh không phân biệt hoa/thường
                String lowerCaseFilter = newValue.toLowerCase();

                // KIỂM TRA ĐIỀU KIỆN TÌM KIẾM
                // Tìm theo Mã NV (Ví dụ gõ "001" sẽ ra "NV001")
                if (employee.getId().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } 
                // Tìm theo Tên NV (Ví dụ gõ "Phát" sẽ ra "Nguyễn Văn Phát")
                else if (employee.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                }
                // Tìm theo Số điện thoại
                else if (employee.getPhone().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false; // Không khớp cái nào thì giấu nhân viên này đi
            });
        });

        // 3. Bọc FilteredList vào SortedList để không bị lỗi khi người dùng click vào tiêu đề cột để sắp xếp (Sort A-Z)
        SortedList<Employee> sortedData = new SortedList<>(filteredData);

        // 4. Liên kết quy tắc sắp xếp của SortedList với TableView
        sortedData.comparatorProperty().bind(tableEmployee.comparatorProperty());

        // 5. Đổ dữ liệu cuối cùng vào bảng
        tableEmployee.setItems(sortedData);
    }

    @FXML
    void openAddEmployeeForm(ActionEvent event) {
        try {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            javafx.scene.layout.AnchorPane adminContentPane = 
                (javafx.scene.layout.AnchorPane) source.getScene().lookup("#contentPane");

            if (adminContentPane != null) {
                SceneManager.loadContent(adminContentPane, "/com/pharmacy/views/admin/add-employee.fxml");
            }
        } catch (Exception e) {
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
        // Lưu ý: KHÔNG set items trực tiếp ở đây nữa, vì setupSearch() sẽ đảm nhận việc đổ dữ liệu
    }
}