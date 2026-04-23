package com.pharmacy.controller.admin;

import com.pharmacy.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
                                customer.getPhone().contains(searchText); // SĐT thường không cần toLowerCase
            }

            return matchesTier && matchesSearch;
        });
    }

    @FXML
    void handleAddCustomer(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hồ sơ khách hàng");
        alert.setHeaderText("Thêm hồ sơ khách hàng mới");
        alert.setContentText("Tại đây sẽ mở Form nhập thông tin khách hàng, bao gồm lịch sử bệnh lý.");
        alert.showAndWait();
    }
}