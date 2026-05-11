package com.pharmacy.controller.warehouse;

import com.pharmacy.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.time.LocalDate;

public class InventoryController {

    // --- THỐNG KÊ KHO ---
    @FXML private Label lblTotalProducts, lblLowStock, lblExpiring;

    // --- THANH CÔNG CỤ TÌM KIẾM ---
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbCategory;

    // --- BẢNG DỮ LIỆU TỒN KHO ---
    @FXML private TableView<Product> tableProduct;
    @FXML private TableColumn<Product, String> colId, colName, colActiveIngredient, colCategory, colUnit, colQuantity, colExpiryDate, colStatus;

    // --- MODAL NHẬP KHO ---
    @FXML private Pane modalNhapKho;
    @FXML private TextField txtTenThuocImport, txtMaLoImport, txtSoLuongImport, txtGiaNhapImport;
    @FXML private ComboBox<String> cbNhaCungCapImport;
    @FXML private DatePicker dpHanSuDungImport, dpNgaySanXuatImport;
    @FXML private ComboBox<String> cbDanhMucImport;
    @FXML private TextField txtGhiChuImport;

    // --- MODAL XUẤT TRẢ ---
    @FXML private Pane modalDoiTra;
    @FXML private TextField txtMaPhieuNhapReturn;
    @FXML private DatePicker dpNgayTraReturn;
    @FXML private TextField txtTenSanPhamReturn;
    @FXML private TextField txtMaLoReturn;
    @FXML private TextField txtSoLuongReturn;
    @FXML private TextArea txtLyDoReturn;

    private ObservableList<Product> productList;
    private FilteredList<Product> filteredData;

    @FXML
    public void initialize() {
        System.out.println("✅ Đã load trang Quản Lý Tồn Kho");
        
        setupTableColumns();
        setupDropdowns();
        
        // Ẩn các modal lúc khởi tạo
        if (modalNhapKho != null) modalNhapKho.setVisible(false);
        if (modalDoiTra != null) modalDoiTra.setVisible(false);

        loadMockData();
        setupSearchAndFilter();
        updateStatistics();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colActiveIngredient.setCellValueFactory(cellData -> cellData.getValue().activeIngredientProperty());
        colCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        colUnit.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        colExpiryDate.setCellValueFactory(cellData -> cellData.getValue().expiryDateProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    }

    private void setupDropdowns() {
        ObservableList<String> searchCategories = FXCollections.observableArrayList(
            "Tất cả danh mục", "Kháng sinh", "Giảm đau - Hạ sốt", "Vitamin", "Thực phẩm chức năng", "Vật tư y tế"
        );
        if (cbCategory != null) {
            cbCategory.setItems(searchCategories);
            cbCategory.getSelectionModel().selectFirst();
        }
        
        ObservableList<String> importCategories = FXCollections.observableArrayList(
            "Kháng sinh", "Giảm đau - Hạ sốt", "Vitamin", "Thực phẩm chức năng", "Vật tư y tế"
        );
        if (cbDanhMucImport != null) {
            cbDanhMucImport.setItems(importCategories);
        }
        
        ObservableList<String> suppliers = FXCollections.observableArrayList("Dược Hậu Giang", "Traphaco", "Imexpharm", "GSK", "AstraZeneca");
        if (cbNhaCungCapImport != null) cbNhaCungCapImport.setItems(suppliers);
    }

    private void loadMockData() {
        productList = FXCollections.observableArrayList(
            new Product("SP001", "Panadol Extra", "Paracetamol 500mg", "Giảm đau - Hạ sốt", "Hộp", "150", "20/12/2027", "Tốt"),
            new Product("SP002", "Augmentin 1g", "Amoxicillin", "Kháng sinh", "Hộp", "45", "15/06/2026", "Tốt"),
            new Product("SP003", "Vitamin C 500mg", "Ascorbic Acid", "Vitamin", "Lọ", "8", "01/01/2028", "Sắp hết hàng"),
            new Product("SP004", "Omega 3 Fish Oil", "DHA, EPA", "Thực phẩm chức năng", "Lọ", "120", "15/05/2024", "Cận Date")
        );
        tableProduct.setItems(productList);
    }

    private void setupSearchAndFilter() {
        filteredData = new FilteredList<>(productList, p -> true);
        
        txtSearch.textProperty().addListener((obs, old, newVal) -> updatePredicate());
        cbCategory.valueProperty().addListener((obs, old, newVal) -> updatePredicate());

        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableProduct.comparatorProperty());
        tableProduct.setItems(sortedData);
    }

    private void updatePredicate() {
        String searchText = txtSearch.getText().toLowerCase();
        String selectedCat = cbCategory.getValue();

        filteredData.setPredicate(product -> {
            boolean matchesSearch = searchText.isEmpty() || 
                                    product.getName().toLowerCase().contains(searchText) || 
                                    product.getId().toLowerCase().contains(searchText);
            boolean matchesCat = selectedCat.equals("Tất cả danh mục") || product.getCategory().equals(selectedCat);
            return matchesSearch && matchesCat;
        });
    }

    private void updateStatistics() {
        if (lblTotalProducts != null) lblTotalProducts.setText("1,245");
        if (lblLowStock != null) lblLowStock.setText("18");
        if (lblExpiring != null) lblExpiring.setText("5");
    }

    // --- EVENT HANDLERS ---

    @FXML void handleAddProduct(ActionEvent event) { 
        // Reset form nhập mới
        if (txtTenThuocImport != null) txtTenThuocImport.clear();
        if (cbNhaCungCapImport != null) cbNhaCungCapImport.getSelectionModel().clearSelection();
        if (txtMaLoImport != null) txtMaLoImport.clear();
        if (txtSoLuongImport != null) txtSoLuongImport.clear();
        if (txtGiaNhapImport != null) txtGiaNhapImport.clear();
        if (dpHanSuDungImport != null) dpHanSuDungImport.setValue(null);
        if (dpNgaySanXuatImport != null) dpNgaySanXuatImport.setValue(null);
        if (cbDanhMucImport != null) cbDanhMucImport.getSelectionModel().clearSelection();
        if (txtGhiChuImport != null) txtGhiChuImport.clear();
        
        modalNhapKho.setVisible(true); 
    }
    
    @FXML void onBtnDoiTraClick(ActionEvent event) { 
        // Reset form đổi trả
        if (txtMaPhieuNhapReturn != null) txtMaPhieuNhapReturn.clear();
        if (dpNgayTraReturn != null) dpNgayTraReturn.setValue(LocalDate.now()); // Set mặc định ngày hôm nay
        if (txtTenSanPhamReturn != null) txtTenSanPhamReturn.clear();
        if (txtMaLoReturn != null) txtMaLoReturn.clear();
        if (txtSoLuongReturn != null) txtSoLuongReturn.clear();
        if (txtLyDoReturn != null) txtLyDoReturn.clear();

        modalDoiTra.setVisible(true); 
    }

    @FXML void onBtnHideModals(ActionEvent event) {
        if (modalNhapKho != null) modalNhapKho.setVisible(false);
        if (modalDoiTra != null) modalDoiTra.setVisible(false);
    }

    @FXML void onBtnXacNhanNhapKhoClick(ActionEvent event) {
        showAlert("Thành công", "Đã ghi nhận lô thuốc mới vào hệ thống kho!");
        onBtnHideModals(null);
    }

    @FXML void onBtnXacNhanDoiTraClick(ActionEvent event) {
        showAlert("Thành công", "Đã tạo phiếu xuất trả thành công!");
        onBtnHideModals(null);
    }

    @FXML void onBtnXuatExcelClick(ActionEvent event) {
        showAlert("Thông báo", "Đang trích xuất dữ liệu tồn kho ra file Excel...");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}