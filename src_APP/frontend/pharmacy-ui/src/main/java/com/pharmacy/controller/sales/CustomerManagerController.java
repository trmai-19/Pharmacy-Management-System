package com.pharmacy.controller.sales;

import com.pharmacy.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomerManagerController {

    // Các nhãn thống kê
    @FXML private Label lblTotalCustomers;
    @FXML private Label lblVIPCustomers;
    @FXML private Label lblSpecialNotes;

    // Thanh công cụ
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbTier;

    // Bảng và Cột
    @FXML private TableView<Customer> tableCustomer;
    @FXML private TableColumn<Customer, String> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colTier;
    @FXML private TableColumn<Customer, String> colPoints;
    @FXML private TableColumn<Customer, String> colTotalSpent;
    @FXML private TableColumn<Customer, String> colLastVisit;

    // --- CÁC THÀNH PHẦN CHO FORM TẠO HỒ SƠ (MODAL) ---
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

        // 1. Liên kết TableColumn với Model
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        colTier.setCellValueFactory(cellData -> cellData.getValue().tierProperty());
        colPoints.setCellValueFactory(cellData -> cellData.getValue().pointsProperty());
        colTotalSpent.setCellValueFactory(cellData -> cellData.getValue().totalSpentProperty());
        colLastVisit.setCellValueFactory(cellData -> cellData.getValue().lastVisitProperty());

        // 2. Khởi tạo Dropdown Lọc Hạng Thành Viên
        cbTier.setItems(FXCollections.observableArrayList(
                "Tất cả hạng mức", "Thành viên", "Bạc", "Vàng", "Kim Cương"
        ));
        cbTier.getSelectionModel().selectFirst();

        // 3. Nạp dữ liệu giả lập (Mock Data)
        loadMockData();

        // 4. Thiết lập bộ lọc kép Real-time
        setupSearchAndFilter();

        // 5. Khởi tạo dữ liệu cho Form Tạo Hồ Sơ
        cbNewGender.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
    }

    private void loadMockData() {
        customerList = FXCollections.observableArrayList(
                new Customer("KH001", "Nguyễn Thu Hà", "0988123456", "Vàng", "1,250", "12,500,000", "15/04/2026"),
                new Customer("KH002", "Trần Văn Luân", "0905999888", "Thành viên", "120", "1,200,000", "02/04/2026"),
                new Customer("KH003", "Lê Thị Lan Anh", "0912333444", "Kim Cương", "5,400", "54,000,000", "18/04/2026"),
                new Customer("KH004", "Phạm Trọng Đạt", "0944555777", "Bạc", "650", "6,500,000", "10/03/2026"),
                new Customer("KH005", "Hoàng Kim Liên", "0977888111", "Vàng", "2,100", "21,000,000", "17/04/2026")
        );
        tableCustomer.setItems(customerList);
    }

    private void setupSearchAndFilter() {
        filteredData = new FilteredList<>(customerList, b -> true);

        // Lắng nghe text thay đổi
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        // Lắng nghe hạng mục thay đổi
        cbTier.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        SortedList<Customer> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableCustomer.comparatorProperty());
        tableCustomer.setItems(sortedData);
    }

    private void updateFilter() {
        String searchText = txtSearch.getText().toLowerCase();
        String selectedTier = cbTier.getValue();

        filteredData.setPredicate(customer -> {
            // Lọc theo hạng mức
            boolean matchesTier = true;
            if (selectedTier != null && !selectedTier.equals("Tất cả hạng mức")) {
                matchesTier = customer.getTier().equals(selectedTier);
            }

            // Lọc theo tên hoặc số điện thoại
            boolean matchesSearch = true;
            if (searchText != null && !searchText.isEmpty()) {
                matchesSearch = customer.getName().toLowerCase().contains(searchText) ||
                                customer.getPhone().contains(searchText); 
            }

            return matchesTier && matchesSearch;
        });
    }

    // ==========================================
    // LOGIC CHO FORM TẠO HỒ SƠ KHÁCH HÀNG MỚI
    // ==========================================

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

        // 1. Kiểm tra bắt buộc điền ĐẦY ĐỦ 4 trường
        if (name == null || name.trim().isEmpty() || 
            phone == null || phone.trim().isEmpty() || 
            gender == null || 
            dob == null) {
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText("Thông tin không đầy đủ");
            alert.setContentText("Vui lòng nhập đầy đủ: Họ tên, Số điện thoại, Giới tính và Ngày sinh!");
            alert.showAndWait();
            return; // Dừng lại, không chạy tiếp code bên dưới
        }

        // 2. Validate (Kiểm tra) tính hợp lệ của ngày sinh ở Frontend
        LocalDate today = LocalDate.now();
        
        // Lỗi 1: Ngày sinh ở tương lai
        if (dob.isAfter(today)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText("Ngày sinh không hợp lệ");
            alert.setContentText("Ngày sinh không thể lớn hơn ngày hiện tại!");
            alert.showAndWait();
            return;
        }
        
        // Lỗi 2: Nhập số năm sinh quá xa (ví dụ > 120 tuổi)
        if (today.getYear() - dob.getYear() > 120) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText("Ngày sinh không hợp lệ");
            alert.setContentText("Hệ thống phát hiện số tuổi lớn hơn 120. Vui lòng kiểm tra lại năm sinh!");
            alert.showAndWait();
            return;
        }

        // 3. Nếu qua hết các bài kiểm tra thì bắt đầu tạo dữ liệu giả lập
        String newId = String.format("KH%03d", customerList.size() + 1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String todayStr = today.format(formatter);

        // Thêm khách hàng mới vào danh sách (Khởi tạo: Hạng Thành viên, 0 điểm, 0 đồng)
        // Lưu ý: Nếu Model Customer của bạn có thêm thuộc tính Gender và DOB, bạn có thể truyền thêm vào đây
        Customer newCustomer = new Customer(newId, name, phone, "Thành viên", "0", "0", todayStr);
        customerList.add(newCustomer);

        // 4. Hiển thị thông báo thành công
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText("Đã tạo hồ sơ thành công cho khách hàng: " + name);
        alert.showAndWait();

        // 5. Đóng Form và Xóa trắng trường nhập liệu
        modalOverlay.setVisible(false);
        clearCreateForm();
    }

    private void clearCreateForm() {
        txtNewName.clear();
        txtNewPhone.clear();
        cbNewGender.getSelectionModel().clearSelection();
        dpNewDOB.setValue(null);
    }
}