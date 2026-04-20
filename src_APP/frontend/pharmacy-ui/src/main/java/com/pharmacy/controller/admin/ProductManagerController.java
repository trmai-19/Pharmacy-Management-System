package com.pharmacy.controller.admin;

import com.pharmacy.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProductManagerController {

    // Thống kê
    @FXML private Label lblTotalProducts;
    @FXML private Label lblLowStock;
    @FXML private Label lblExpiring;

    // Thanh công cụ
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbCategory;

    // Bảng và Cột
    @FXML private TableView<Product> tableProduct;
    @FXML private TableColumn<Product, String> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colActiveIngredient;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, String> colUnit;
    @FXML private TableColumn<Product, String> colQuantity;
    @FXML private TableColumn<Product, String> colExpiryDate;
    @FXML private TableColumn<Product, String> colStatus;

    private ObservableList<Product> productList;
    private FilteredList<Product> filteredData;

    @FXML
    public void initialize() {
        System.out.println("📦 ProductManagerController đang tải...");

        // 1. Ánh xạ các cột với Model Product
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colActiveIngredient.setCellValueFactory(cellData -> cellData.getValue().activeIngredientProperty());
        colCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        colUnit.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        colExpiryDate.setCellValueFactory(cellData -> cellData.getValue().expiryDateProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // 2. Khởi tạo dữ liệu Dropdown Danh mục
        cbCategory.setItems(FXCollections.observableArrayList(
                "Tất cả danh mục", "Kháng sinh", "Giảm đau - Hạ sốt", "Vitamin - Khoáng chất", "Thực phẩm chức năng", "Vật tư y tế"
        ));
        cbCategory.getSelectionModel().selectFirst();

        // 3. Tải dữ liệu giả lập (Mock Data chuẩn ngành Dược)
        loadMockData();

        // 4. Thiết lập tính năng Tìm kiếm & Lọc thời gian thực
        setupSearchAndFilter();
    }

    private void loadMockData() {
        productList = FXCollections.observableArrayList(
                new Product("SP001", "Panadol Extra", "Paracetamol 500mg, Caffeine 65mg", "Giảm đau - Hạ sốt", "Hộp", "150", "20/12/2027", "Tốt"),
                new Product("SP002", "Augmentin 1g", "Amoxicillin, Clavulanic Acid", "Kháng sinh", "Hộp", "45", "15/06/2026", "Tốt"),
                new Product("SP003", "Vitamin C 500mg", "Ascorbic Acid", "Vitamin - Khoáng chất", "Lọ", "8", "01/01/2028", "Sắp hết hàng"),
                new Product("SP004", "Omega 3 Fish Oil", "DHA, EPA", "Thực phẩm chức năng", "Lọ", "120", "15/05/2024", "Cận Date"),
                new Product("SP005", "Khẩu trang Y tế 4 lớp", "Vải không dệt", "Vật tư y tế", "Hộp", "500", "N/A", "Tốt"),
                new Product("SP006", "Effer-Paralmax", "Paracetamol 500mg", "Giảm đau - Hạ sốt", "Tuýp", "0", "10/10/2025", "Hết hàng")
        );
        tableProduct.setItems(productList);
    }

    private void setupSearchAndFilter() {
        filteredData = new FilteredList<>(productList, b -> true);

        // Lắng nghe sự thay đổi của Text Search
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        // Lắng nghe sự thay đổi của ComboBox Danh mục
        cbCategory.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableProduct.comparatorProperty());
        tableProduct.setItems(sortedData);
    }

    // Hàm xử lý lọc kép: Phải thỏa mãn cả từ khóa tìm kiếm VÀ danh mục đã chọn
    private void updateFilter() {
        String searchText = txtSearch.getText().toLowerCase();
        String selectedCategory = cbCategory.getValue();

        filteredData.setPredicate(product -> {
            // 1. Kiểm tra lọc theo Danh mục trước
            boolean matchesCategory = true;
            if (selectedCategory != null && !selectedCategory.equals("Tất cả danh mục")) {
                matchesCategory = product.getCategory().equals(selectedCategory);
            }

            // 2. Kiểm tra lọc theo Text
            boolean matchesSearch = true;
            if (searchText != null && !searchText.isEmpty()) {
                matchesSearch = product.getName().toLowerCase().contains(searchText) ||
                                product.getId().toLowerCase().contains(searchText) ||
                                product.getActiveIngredient().toLowerCase().contains(searchText);
            }

            // Phải thỏa mãn cả 2 điều kiện
            return matchesCategory && matchesSearch;
        });
    }

    @FXML
    void handleAddProduct(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thêm thuốc mới");
        alert.setHeaderText("Mở Form Nhập Kho");
        alert.setContentText("Tại đây sẽ bật lên một Dialog để nhập thông tin thuốc mới, mã vạch, số lô, v.v.");
        alert.showAndWait();
    }
}