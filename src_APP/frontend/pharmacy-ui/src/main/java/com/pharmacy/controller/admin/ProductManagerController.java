package com.pharmacy.controller.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pharmacy.util.ApiService;
import com.pharmacy.model.Category;
import com.pharmacy.model.Medicine; // Đã đổi sang Model Medicine siêu xịn của Warehouse
import com.pharmacy.model.Batch;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ProductManagerController {

    @FXML private Label lblTotalProducts, lblLowStock;
    
    // TAB SẢN PHẨM
    @FXML private TextField txtSearchProduct;
    @FXML private ComboBox<String> cbCategoryFilter; // Bộ lọc dùng String như Warehouse
    @FXML private TableView<Medicine> tableProduct;
    @FXML private TableColumn<Medicine, String> colProdId, colProdName, colProdActive, colProdCat, colProdUnit, colProdUsage;
    @FXML private TableColumn<Medicine, Number> colProdPrice;
    
    // BẢNG LÔ HÀNG
    @FXML private TableView<Batch> tableBatch;
    @FXML private TableColumn<Batch, String> colBatchId, colBatchMfg, colBatchExp, colBatchImport, colBatchStatus, colBatchQty;
    
    // TAB DANH MỤC
    @FXML private TextField txtSearchCategory;
    @FXML private TableView<Category> tableCategory;
    @FXML private TableColumn<Category, String> colCatId, colCatName, colCatNote;

    // MODAL SẢN PHẨM (Quyền lực Admin)
    @FXML private StackPane modalProduct;
    @FXML private Label lblProductModalTitle;
    @FXML private TextField txtProdName, txtProdUnit, txtProdActive, txtProdUsage, txtProdPrice;
    @FXML private ComboBox<Category> cbProdCategory; // Thêm/Sửa bắt buộc dùng Object Category
    private Medicine currentEditingProduct = null;

    // MODAL DANH MỤC
    @FXML private StackPane modalCategory;
    @FXML private Label lblCategoryModalTitle;
    @FXML private TextField txtCatName, txtCatNote;
    private Category currentEditingCategory = null;

    // DATA LISTS
    private Map<String, String> categoryDictionary = new HashMap<>();
    private ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private ObservableList<Medicine> productList = FXCollections.observableArrayList();
    private ObservableList<Batch> batchList = FXCollections.observableArrayList();
    
    private FilteredList<Medicine> filteredProducts;
    private FilteredList<Category> filteredCategories;

    @FXML
    public void initialize() {
        setupTables();
        loadCategories(); // Gọi API Danh mục trước, sau đó nó tự gọi loadProducts()
        loadLowStockAlert();
        setupSearchFilters();

        tableProduct.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) loadBatchesForProduct(newVal.getId()); else batchList.clear();
        });

        // Setup ComboBox Add/Edit Product
        cbProdCategory.setItems(categoryList);
        cbProdCategory.setConverter(new StringConverter<>() {
            @Override public String toString(Category c) { return c == null ? "" : c.getCategoryName(); }
            @Override public Category fromString(String s) { return null; }
        });
    }

    private void setupTables() {
        // MAP CHUẨN MODEL MEDICINE
        colProdId.setCellValueFactory(c -> c.getValue().idProperty());
        colProdName.setCellValueFactory(c -> c.getValue().nameProperty());
        colProdActive.setCellValueFactory(c -> c.getValue().ingredientProperty());
        colProdCat.setCellValueFactory(c -> c.getValue().categoryProperty());
        colProdUnit.setCellValueFactory(c -> c.getValue().unitProperty());
        colProdUsage.setCellValueFactory(c -> c.getValue().usageProperty());
        
        colProdPrice.setCellValueFactory(c -> c.getValue().priceProperty());
        colProdPrice.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : new DecimalFormat("#,### đ").format(item.doubleValue()));
            }
        });

        // MAP LÔ HÀNG
        colBatchId.setCellValueFactory(c -> c.getValue().batchIdProperty());
        colBatchMfg.setCellValueFactory(c -> c.getValue().mfgDateProperty());
        colBatchExp.setCellValueFactory(c -> c.getValue().expDateProperty());
        colBatchImport.setCellValueFactory(c -> c.getValue().importDateProperty());
        colBatchQty.setCellValueFactory(c -> c.getValue().currentQtyProperty());
        colBatchStatus.setCellValueFactory(c -> c.getValue().statusProperty());
        tableBatch.setItems(batchList);

        // MAP DANH MỤC
        colCatId.setCellValueFactory(c -> c.getValue().categoryIdProperty());
        colCatName.setCellValueFactory(c -> c.getValue().categoryNameProperty());
        colCatNote.setCellValueFactory(c -> c.getValue().noteProperty());
        tableCategory.setItems(categoryList);
    }

    private void setupSearchFilters() {
        // Bộ lọc Sản phẩm (Y hệt Warehouse)
        ObservableList<String> searchCategories = FXCollections.observableArrayList("Tất cả danh mục");
        cbCategoryFilter.setItems(searchCategories);
        cbCategoryFilter.getSelectionModel().selectFirst();

        filteredProducts = new FilteredList<>(productList, p -> true);
        txtSearchProduct.textProperty().addListener((obs, old, newVal) -> updateProductFilter());
        cbCategoryFilter.valueProperty().addListener((obs, old, newVal) -> updateProductFilter());
        
        SortedList<Medicine> sortedProducts = new SortedList<>(filteredProducts);
        sortedProducts.comparatorProperty().bind(tableProduct.comparatorProperty());
        tableProduct.setItems(sortedProducts);

        // Bộ lọc Danh mục
        filteredCategories = new FilteredList<>(categoryList, c -> true);
        txtSearchCategory.textProperty().addListener((obs, oldV, newV) -> {
            filteredCategories.setPredicate(c -> {
                if (newV == null || newV.trim().isEmpty()) return true;
                String lower = newV.toLowerCase();
                return c.getCategoryName().toLowerCase().contains(lower) || c.getCategoryId().toLowerCase().contains(lower);
            });
        });
        SortedList<Category> sortedCats = new SortedList<>(filteredCategories);
        sortedCats.comparatorProperty().bind(tableCategory.comparatorProperty());
        tableCategory.setItems(sortedCats);
    }

    private void updateProductFilter() {
        String searchText = txtSearchProduct.getText() != null ? txtSearchProduct.getText().toLowerCase() : "";
        String selectedCat = cbCategoryFilter.getValue();
        
        filteredProducts.setPredicate(product -> {
            boolean matchesSearch = searchText.isEmpty() || product.getName().toLowerCase().contains(searchText) || product.getId().toLowerCase().contains(searchText);
            boolean matchesCat = selectedCat == null || selectedCat.equals("Tất cả danh mục") || product.getCategory().contains(selectedCat);
            return matchesSearch && matchesCat;
        });
        batchList.clear(); // Clear chi tiết lô khi lọc
    }

    // ==========================================
    // --- GỌI API LOAD DỮ LIỆU ---
    // ==========================================

    private void loadCategories() {
        ApiService.get("/api/warehouse/categories").thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        categoryList.clear();
                        categoryDictionary.clear();
                        ObservableList<String> searchCategories = FXCollections.observableArrayList("Tất cả danh mục");
                        
                        JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                        for (JsonNode node : dataNode) {
                            String id = node.has("madm") ? node.get("madm").asText() : "";
                            String name = node.has("tendm") ? node.get("tendm").asText() : id;
                            String note = node.has("mota") ? node.get("mota").asText() : "";
                            
                            if (!id.isEmpty()) {
                                categoryDictionary.put(id, name);
                                searchCategories.add(name);       
                                categoryList.add(new Category(id, name, note));
                            }
                        }
                        cbCategoryFilter.setItems(searchCategories);
                        cbCategoryFilter.getSelectionModel().selectFirst();
                        
                        loadProducts(); // Nạp danh mục xong mới nạp sản phẩm
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
    }

    private void loadProducts() {
        ApiService.get("/api/products/all").thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                        productList.clear();
                        for (JsonNode node : dataNode) {
                            String id = node.has("masp") ? node.get("masp").asText() : "";
                            String name = node.has("tensanpham") ? node.get("tensanpham").asText() : "";
                            String unit = node.has("dvt") ? node.get("dvt").asText() : "";
                            String ingredient = node.has("thanhphan") ? node.get("thanhphan").asText() : "";
                            String usage = node.has("congdung") ? node.get("congdung").asText() : "";
                            double price = node.has("giaban") ? node.get("giaban").asDouble() : 0.0;
                            
                            // Map Tên Danh Mục thông minh
                            String categoryName = "Không xác định";
                            if (node.has("danhMuc") && !node.get("danhMuc").isNull()) {
                                JsonNode dmNode = node.get("danhMuc");
                                categoryName = dmNode.has("tendm") ? dmNode.get("tendm").asText() : "Không xác định";
                            } else if (node.has("tendm")) {
                                categoryName = node.get("tendm").asText();
                            } else if (node.has("madm")) {
                                String madm = node.get("madm").asText();
                                categoryName = categoryDictionary.getOrDefault(madm, madm.isEmpty() ? "Không xác định" : madm);
                            }

                            productList.add(new Medicine(id, name, unit, ingredient, usage, categoryName, price));
                        }
                        lblTotalProducts.setText(String.valueOf(productList.size()));
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
    }

    private void loadBatchesForProduct(String masp) {
        if (masp == null || masp.isEmpty()) return;
        ApiService.get("/api/warehouse/batches/" + masp).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                        batchList.clear();
                        for (JsonNode node : dataNode) {
                            String malo = node.path("malo").asText("");
                            
                            // XỬ LÝ DATE AN TOÀN TRÁNH CRASH NULL (Lấy phần trước dấu T)
                            String nsx = node.path("ngaysx").asText("");
                            if (nsx.isEmpty()) nsx = node.path("nsx").asText("");
                            
                            String hsd = node.path("hsd").asText("");
                            String nhap = node.path("ngaynhap").asText("");
                            
                            String qty = node.has("slsp") ? node.path("slsp").asText() : node.path("sl").asText("0");
                            String status = node.path("trangthai").asText("");
                            
                            batchList.add(new Batch(
                                malo, 
                                nsx.contains("T") ? nsx.split("T")[0] : nsx, 
                                hsd.contains("T") ? hsd.split("T")[0] : hsd, 
                                nhap.contains("T") ? nhap.split("T")[0] : nhap, 
                                qty, status, "0"
                            ));
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
    }

    private void loadLowStockAlert() {
        ApiService.get("/api/warehouse/alerts/low-stock").thenAccept(res -> {
            Platform.runLater(() -> {
                try {
                    if (res.statusCode() == 200) lblLowStock.setText(String.valueOf(ApiService.mapper.readTree(res.body()).get("data").size()));
                } catch (Exception e) {}
            });
        });
    }

    // ==========================================
    // --- XỬ LÝ SẢN PHẨM BẰNG MODAL ---
    // ==========================================

    @FXML void handleShowAddProduct(ActionEvent event) {
        currentEditingProduct = null;
        lblProductModalTitle.setText("💊 Thêm Sản Phẩm Thuốc Mới");
        txtProdName.clear(); txtProdUnit.clear(); txtProdActive.clear(); txtProdUsage.clear(); txtProdPrice.clear();
        if(!categoryList.isEmpty()) cbProdCategory.getSelectionModel().selectFirst();
        modalProduct.setVisible(true);
    }

    @FXML void handleShowEditProduct(ActionEvent event) {
        currentEditingProduct = tableProduct.getSelectionModel().getSelectedItem();
        if (currentEditingProduct == null) {
            showAlert(AlertType.WARNING, "Cảnh báo", "Vui lòng chọn 1 sản phẩm để sửa!");
            return;
        }
        lblProductModalTitle.setText("✏ Sửa Thông Tin Sản Phẩm");
        
        // ĐỔ DỮ LIỆU CÓ SẴN TỪ MODEL MEDICINE (Không cần gọi API thêm lần nữa)
        txtProdName.setText(currentEditingProduct.getName());
        txtProdUnit.setText(currentEditingProduct.getUnit());
        txtProdActive.setText(currentEditingProduct.getIngredient());
        txtProdUsage.setText(currentEditingProduct.getUsage());
        txtProdPrice.setText(String.format("%.0f", currentEditingProduct.getPrice())); // Giá VNĐ
        
        // Chọn đúng Danh Mục trong ComboBox
        cbProdCategory.getItems().stream()
            .filter(c -> c.getCategoryName().equals(currentEditingProduct.getCategory()))
            .findFirst()
            .ifPresent(cbProdCategory.getSelectionModel()::select);

        modalProduct.setVisible(true);
    }

    @FXML void handleSaveProduct(ActionEvent event) {
        if (txtProdName.getText().trim().isEmpty() || txtProdUnit.getText().trim().isEmpty() || txtProdPrice.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền các trường bắt buộc (*)"); return;
        }

        try {
            ObjectNode json = ApiService.mapper.createObjectNode();
            json.put("tensanpham", txtProdName.getText().trim());
            json.put("madm", cbProdCategory.getValue().getCategoryId());
            json.put("thanhphan", txtProdActive.getText().trim());
            json.put("congdung", txtProdUsage.getText().trim());
            json.put("dvt", txtProdUnit.getText().trim());
            json.put("giaban", Double.parseDouble(txtProdPrice.getText().trim()));
            json.put("trangthai", "DANG_BAN");
            
            if (currentEditingProduct == null) {
                ApiService.post("/api/warehouse/products", json.toString()).thenAccept(res -> Platform.runLater(() -> {
                    if (res.statusCode() == 200) { showAlert(AlertType.INFORMATION, "Thành công", "Đã thêm sản phẩm mới!"); handleCloseProductModal(null); loadProducts(); }
                }));
            } else {
                ApiService.put("/api/warehouse/products/" + currentEditingProduct.getId(), json.toString()).thenAccept(res -> Platform.runLater(() -> {
                    if (res.statusCode() == 200) { showAlert(AlertType.INFORMATION, "Thành công", "Đã cập nhật sản phẩm!"); handleCloseProductModal(null); loadProducts(); }
                }));
            }
        } catch (NumberFormatException e) { showAlert(AlertType.ERROR, "Lỗi", "Vui lòng nhập giá bán đúng định dạng số!"); }
    }

    @FXML void handleDeleteProduct(ActionEvent event) {
        Medicine selected = tableProduct.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(AlertType.WARNING, "Thông báo", "Vui lòng chọn sản phẩm cần xóa!"); return; }
        Alert alert = new Alert(AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa thuốc " + selected.getName() + " không?", ButtonType.YES, ButtonType.NO);
        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            ApiService.postPublic("/api/warehouse/products/" + selected.getId() + "?_method=DELETE", "").thenAccept(res -> Platform.runLater(this::loadProducts));
        }
    }

    // ==========================================
    // --- XỬ LÝ DANH MỤC BẰNG MODAL ---
    // ==========================================

    @FXML void handleShowAddCategory(ActionEvent event) { 
        currentEditingCategory = null;
        lblCategoryModalTitle.setText("📂 Thêm Danh Mục Mới");
        txtCatName.clear(); txtCatNote.clear();
        modalCategory.setVisible(true);
    }

    @FXML void handleShowEditCategory(ActionEvent event) {
        currentEditingCategory = tableCategory.getSelectionModel().getSelectedItem();
        if (currentEditingCategory == null) { showAlert(AlertType.WARNING, "Cảnh báo", "Vui lòng chọn 1 danh mục để sửa!"); return; }
        lblCategoryModalTitle.setText("✏ Sửa Danh Mục");
        txtCatName.setText(currentEditingCategory.getCategoryName());
        txtCatNote.setText(currentEditingCategory.getNote());
        modalCategory.setVisible(true);
    }

    @FXML void handleSaveCategory(ActionEvent event) {
        if (txtCatName.getText().trim().isEmpty()) { showAlert(AlertType.WARNING, "Thiếu thông tin", "Tên danh mục không được để trống!"); return; }
        ObjectNode json = ApiService.mapper.createObjectNode();
        json.put("tendm", txtCatName.getText().trim()); json.put("mota", txtCatNote.getText().trim());

        if (currentEditingCategory == null) {
            ApiService.post("/api/warehouse/categories", json.toString()).thenAccept(res -> Platform.runLater(() -> { handleCloseCategoryModal(null); loadCategories(); }));
        } else {
            ApiService.put("/api/admin/categories/" + currentEditingCategory.getCategoryId(), json.toString()).thenAccept(res -> Platform.runLater(() -> { handleCloseCategoryModal(null); loadCategories(); }));
        }
    }

    @FXML void handleDeleteCategory(ActionEvent event) {
        Category selected = tableCategory.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(AlertType.WARNING, "Thông báo", "Vui lòng chọn danh mục cần xóa!"); return; }
        Alert alert = new Alert(AlertType.CONFIRMATION, "Bạn có chắc muốn xóa Danh Mục" + selected.getCategoryName() + "?", ButtonType.YES, ButtonType.NO);
        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            ApiService.postPublic("/api/admin/categories/" + selected.getCategoryId() + "?_method=DELETE", "").thenAccept(res -> Platform.runLater(this::loadCategories));
        }
    }

    @FXML void handleCloseProductModal(ActionEvent e) { modalProduct.setVisible(false); }
    @FXML void handleCloseCategoryModal(ActionEvent e) { modalCategory.setVisible(false); }
    private void showAlert(AlertType t, String title, String content) { Alert a = new Alert(t); a.setTitle(title); a.setHeaderText(null); a.setContentText(content); a.showAndWait(); }
}