package com.pharmacy.controller.admin;

import com.pharmacy.model.Product;
import com.pharmacy.util.ApiService;
import com.pharmacy.model.Batch;
import com.pharmacy.model.Category;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pharmacy.dto.ApiResponse;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

public class ProductManagerController {

    @FXML private Label lblTotalProducts;
    @FXML private Label lblLowStock;

    @FXML private TextField txtSearchProduct;
    @FXML private ComboBox<String> cbCategoryFilter;
    
    @FXML private Button btnConfirmEditProduct;
    @FXML private Button btnAddProduct;
    @FXML private Button btnEditProduct;
    
    @FXML private TableView<Product> tableProduct;
    @FXML private TableColumn<Product, String> colProdId;
    @FXML private TableColumn<Product, String> colProdName;
    @FXML private TableColumn<Product, String> colProdActive;
    @FXML private TableColumn<Product, String> colProdCat;
    @FXML private TableColumn<Product, String> colProdUnit;
    @FXML private TableColumn<Product, String> colProdQty;

    @FXML private TableView<Batch> tableBatch;
    @FXML private TableColumn<Batch, String> colBatchId;
    @FXML private TableColumn<Batch, String> colBatchMfg;
    @FXML private TableColumn<Batch, String> colBatchExp;
    @FXML private TableColumn<Batch, String> colBatchImport;
    @FXML private TableColumn<Batch, String> colBatchQty;
    @FXML private TableColumn<Batch, String> colBatchStatus;

    @FXML private TextField txtSearchCategory;
    
    @FXML private Button btnConfirmEditCategory;
    @FXML private Button btnAddCategory;
    @FXML private Button btnEditCategory;

    @FXML private TableView<Category> tableCategory;
    @FXML private TableColumn<Category, String> colCatId;
    @FXML private TableColumn<Category, String> colCatName;
    @FXML private TableColumn<Category, String> colCatNote;

    private ObservableList<Product> productList;
    private ObservableList<Category> categoryList;
    private FilteredList<Product> filteredProducts;

    @FXML
    public void initialize() {
        setupProductTable();
        setupBatchTable();
        setupCategoryTable();
        loadData();
        setupSearchFilter();

        tableProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadBatchesForProduct(newVal.getId());
            } else {
                tableBatch.getItems().clear();
            }
        });
    }

    private void setupProductTable() {
        tableProduct.setEditable(false); // Mặc định khóa sửa

        colProdId.setCellValueFactory(cell -> cell.getValue().idProperty());
        colProdName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        colProdActive.setCellValueFactory(cell -> cell.getValue().activeIngredientProperty());
        colProdCat.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        colProdUnit.setCellValueFactory(cell -> cell.getValue().unitProperty());
        colProdQty.setCellValueFactory(cell -> cell.getValue().quantityProperty());

        colProdName.setCellFactory(TextFieldTableCell.forTableColumn());
        colProdName.setOnEditCommit(e -> e.getRowValue().nameProperty().set(e.getNewValue()));

        colProdActive.setCellFactory(TextFieldTableCell.forTableColumn());
        colProdActive.setOnEditCommit(e -> e.getRowValue().activeIngredientProperty().set(e.getNewValue()));

        colProdCat.setCellFactory(TextFieldTableCell.forTableColumn());
        colProdCat.setOnEditCommit(e -> e.getRowValue().categoryProperty().set(e.getNewValue()));

        colProdUnit.setCellFactory(TextFieldTableCell.forTableColumn());
        colProdUnit.setOnEditCommit(e -> e.getRowValue().unitProperty().set(e.getNewValue()));
    }

    private void setupBatchTable() {
        colBatchId.setCellValueFactory(cell -> cell.getValue().batchIdProperty());
        colBatchMfg.setCellValueFactory(cell -> cell.getValue().mfgDateProperty());
        colBatchExp.setCellValueFactory(cell -> cell.getValue().expDateProperty());
        colBatchImport.setCellValueFactory(cell -> cell.getValue().importDateProperty());
        colBatchQty.setCellValueFactory(cell -> cell.getValue().currentQtyProperty());
        colBatchStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());
    }

    private void setupCategoryTable() {
        tableCategory.setEditable(false);

        colCatId.setCellValueFactory(cell -> cell.getValue().categoryIdProperty());
        colCatName.setCellValueFactory(cell -> cell.getValue().categoryNameProperty());
        colCatNote.setCellValueFactory(cell -> cell.getValue().noteProperty());

        colCatName.setCellFactory(TextFieldTableCell.forTableColumn());
        colCatName.setOnEditCommit(e -> e.getRowValue().categoryNameProperty().set(e.getNewValue()));

        colCatNote.setCellFactory(TextFieldTableCell.forTableColumn());
        colCatNote.setOnEditCommit(e -> e.getRowValue().noteProperty().set(e.getNewValue()));
    }

    private void setupSearchFilter() {
        filteredProducts = new FilteredList<>(productList, p -> true);

        txtSearchProduct.textProperty().addListener((obs, oldV, newV) -> updateFilter());
        cbCategoryFilter.valueProperty().addListener((obs, oldV, newV) -> updateFilter());

        SortedList<Product> sortedData = new SortedList<>(filteredProducts);
        sortedData.comparatorProperty().bind(tableProduct.comparatorProperty());
        tableProduct.setItems(sortedData);

        cbCategoryFilter.setItems(FXCollections.observableArrayList(
            "Tất cả danh mục", "Kháng sinh", "Giảm đau - Hạ sốt", "Vitamin", "Thực phẩm chức năng"
        ));
        cbCategoryFilter.getSelectionModel().selectFirst();
    }

    private void updateFilter() {
        String searchText = txtSearchProduct.getText() != null ? txtSearchProduct.getText().toLowerCase() : "";
        String selectedCat = cbCategoryFilter.getValue();

        filteredProducts.setPredicate(p -> {
            boolean matchCat = (selectedCat == null || selectedCat.equals("Tất cả danh mục")) 
                               || p.getCategory().equals(selectedCat);
            boolean matchText = searchText.isEmpty() 
                               || p.getName().toLowerCase().contains(searchText)
                               || p.getId().toLowerCase().contains(searchText);
            return matchCat && matchText;
        });
    }
    

    
    private void loadData() {
    if (productList == null) {
        productList = FXCollections.observableArrayList();
    }
    productList.clear();

    if (categoryList == null) {
        categoryList = FXCollections.observableArrayList();
    }
    tableCategory.setItems(categoryList);

    ApiService.get("/api/products")
        .thenApply(HttpResponse::body)
        .thenAccept(jsonResponseBody -> {
            try {
                ApiResponse<List<Product>> apiRes = ApiService.mapper.readValue(
                    jsonResponseBody,
                    new TypeReference<ApiResponse<List<Product>>>() {}
                );

                if (apiRes != null && apiRes.getStatus() == 200 && apiRes.getData() != null) {
                    List<Product> serverProducts = apiRes.getData();

                    Platform.runLater(() -> {
                        productList.setAll(serverProducts);
                        tableProduct.setItems(productList); // Đổ vào bảng sản phẩm
                        if (lblTotalProducts != null) {
                            lblTotalProducts.setText(String.valueOf(productList.size()));
                        }
                        setupSearchFilter(); // Chạy hàm lọc tìm kiếm của bạn
                    });
                } else {
                    String msg = (apiRes != null) ? apiRes.getMessage() : "Lỗi không xác định";
                    Platform.runLater(() -> showAlert("Thông báo hệ thống", "Không thể lấy dữ liệu: " + msg));
                }

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert("Lỗi cấu trúc", "Đọc dữ liệu sản phẩm thất bại (Lỗi JSON)."));
            }
        })
        .exceptionally(ex -> {
            ex.printStackTrace();
            Platform.runLater(() -> showAlert("Lỗi kết nối", "Không thể lấy dữ liệu ngầm từ Backend!"));
            return null;
        });
}

    private void loadBatchesForProduct(String productId) {
        ObservableList<Batch> batches = FXCollections.observableArrayList(
            new Batch("BATCH-01", "01/01/2024", "01/01/2027", "10/01/2024", "250", "Tốt"),
            new Batch("BATCH-02", "12/02/2024", "12/02/2027", "20/02/2024", "250", "Tốt")
        );
        tableBatch.setItems(batches);
    }

    // ==========================================
    // --- XỬ LÝ SỰ KIỆN SẢN PHẨM ---
    // ==========================================

    @FXML
    void handleAddProduct(ActionEvent event) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Thêm Sản Phẩm Mới");
        dialog.setHeaderText("Điền thông tin SP (Tồn kho mặc định = 0)");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtId = new TextField(); txtId.setPromptText("Mã SP");
        TextField txtName = new TextField(); txtName.setPromptText("Tên thương mại");
        TextField txtActive = new TextField(); txtActive.setPromptText("Hoạt chất");
        TextField txtCat = new TextField(); txtCat.setPromptText("Danh mục");
        TextField txtUnit = new TextField(); txtUnit.setPromptText("Đơn vị tính");

        grid.add(new Label("Mã SP:"), 0, 0); grid.add(txtId, 1, 0);
        grid.add(new Label("Tên thương mại:"), 0, 1); grid.add(txtName, 1, 1);
        grid.add(new Label("Hoạt chất:"), 0, 2); grid.add(txtActive, 1, 2);
        grid.add(new Label("Danh mục:"), 0, 3); grid.add(txtCat, 1, 3);
        grid.add(new Label("Đơn vị tính:"), 0, 4); grid.add(txtUnit, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Product(txtId.getText(), txtName.getText(), txtActive.getText(), 
                                   txtCat.getText(), txtUnit.getText(), "0", "Chưa cập nhật", "Mới");
            }
            return null;
        });

        dialog.showAndWait().ifPresent(p -> {
            productList.add(p);
            lblTotalProducts.setText(String.valueOf(productList.size()));
        });
    }

    @FXML
    void handleEditProduct(ActionEvent event) {
        tableProduct.setEditable(true); // Bật chế độ click đúp
        btnConfirmEditProduct.setVisible(true);
        btnConfirmEditProduct.setManaged(true);
        btnAddProduct.setDisable(true); 
        
        showAlert("Chế độ sửa", "Đã bật chế độ sửa. Hãy nháy đúp chuột vào ô cần sửa trong bảng. Sau khi xong, bấm nút '✔ Xong' bên trái để lưu.");
    }

    @FXML
    void handleConfirmEditProduct(ActionEvent event) {
        tableProduct.setEditable(false); 
        btnConfirmEditProduct.setVisible(false);
        btnConfirmEditProduct.setManaged(false);
        btnAddProduct.setDisable(false); 
        
        showAlert("Thành công", "Dữ liệu sản phẩm đã được cập nhật!");
    }

    @FXML
    void handleDeleteProduct(ActionEvent event) {
        Product selected = tableProduct.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Xóa sản phẩm " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
            if (alert.showAndWait().get() == ButtonType.YES) {
                productList.remove(selected);
                lblTotalProducts.setText(String.valueOf(productList.size()));
            }
        } else {
            showAlert("Thông báo", "Vui lòng chọn sản phẩm cần xóa!");
        }
    }

    
    @FXML
    void handleAddCategory(ActionEvent event) {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle("Thêm Danh Mục Mới");
        
        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtId = new TextField();
        TextField txtName = new TextField();
        TextField txtNote = new TextField();

        grid.add(new Label("Mã DM:"), 0, 0); grid.add(txtId, 1, 0);
        grid.add(new Label("Tên DM:"), 0, 1); grid.add(txtName, 1, 1);
        grid.add(new Label("Ghi chú:"), 0, 2); grid.add(txtNote, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Category(txtId.getText(), txtName.getText(), txtNote.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(cat -> categoryList.add(cat));
    }

    @FXML
    void handleEditCategory(ActionEvent event) {
        tableCategory.setEditable(true);
        btnConfirmEditCategory.setVisible(true);
        btnConfirmEditCategory.setManaged(true);
        btnAddCategory.setDisable(true);
        
        showAlert("Chế độ sửa", "Nháy đúp vào ô trên bảng để sửa danh mục. Bấm '✔ Xong' để hoàn tất.");
    }

    @FXML
    void handleConfirmEditCategory(ActionEvent event) {
        tableCategory.setEditable(false);
        btnConfirmEditCategory.setVisible(false);
        btnConfirmEditCategory.setManaged(false);
        btnAddCategory.setDisable(false);
    }

    @FXML
    void handleDeleteCategory(ActionEvent event) {
        Category selected = tableCategory.getSelectionModel().getSelectedItem();
        if (selected != null) {
            categoryList.remove(selected);
        } else {
            showAlert("Thông báo", "Vui lòng chọn danh mục cần xóa!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}