package com.pharmacy.controller.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pharmacy.model.Batch;
import com.pharmacy.model.Medicine;
import com.pharmacy.model.ImportItemRow;
import com.pharmacy.util.ApiService;
import com.pharmacy.util.Session;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class InventoryController {

    @FXML private Label lblTotalProducts, lblLowStock, lblExpiring;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbCategory;
    @FXML private VBox cardLowStock, cardExpiring;

    @FXML private TableView<Medicine> tableProduct;
    @FXML private TableColumn<Medicine, String> colId, colName, colUnit, colIngredient, colUsage, colCategory;
    @FXML private TableColumn<Medicine, Number> colPrice;

    @FXML private Label lblBatchDetailTitle;
    @FXML private TableView<Batch> tableBatch;
    @FXML private TableColumn<Batch, String> colBatchId, colMfgDate, colExpDate, colImportDate, colBatchQuantity, colBatchStatus, colImportPrice;

    @FXML private Pane modalNhapKho;
    @FXML private ComboBox<String> cbNhaCungCapImport, cbSanPhamImport, cbKhoImport;
    @FXML private TextField txtDvtImport, txtSoLuongImport, txtGiaNhapImport, txtGhiChuImport;
    @FXML private DatePicker dpNgaySanXuatImport, dpHanSuDungImport;
    @FXML private Label lblTotalImportValue;
    @FXML private TableView<ImportItemRow> tableImportItems;
    @FXML private TableColumn<ImportItemRow, String> colImpProductName, colImpKho, colImpMfg, colImpExp, colImpAction;
    @FXML private TableColumn<ImportItemRow, Number> colImpQty, colImpPrice;

    @FXML private Pane modalAddProduct;
    @FXML private TextField txtTenSpNew, txtDvtSpNew, txtThanhPhanNew, txtCongDungNew;
    @FXML private ComboBox<String> cbCategoryNew;

    @FXML private Button btnAiSuggestWh;
    @FXML private VBox aiPanelWh;
    @FXML private VBox aiPanelContentWh;
    @FXML private VBox vboxAiResultsWh;
    private boolean isAiPanelWhOpen = false;

    @FXML private Pane modalAddSupplier;
    @FXML private TextField txtTenNccNew, txtSdtNccNew, txtEmailNccNew, txtDiaChiNccNew;

    @FXML private StackPane modalAlertBatches;
    @FXML private Label lblAlertTitle;
    @FXML private TableView<Batch> tableAlertBatches;
    @FXML private TableColumn<Batch, String> colAlertBatchId, colAlertProductId, colAlertMfgDate, colAlertExpDate, colAlertImportDate, colAlertQuantity, colAlertStatus, colAlertImportPrice;

    private ObservableList<Medicine> productList;
    private FilteredList<Medicine> filteredData;
    private ObservableList<ImportItemRow> temporaryImportList = FXCollections.observableArrayList();
    
    private Map<String, String> categoryDictionary = new HashMap<>();

    @FXML
    public void initialize() {
        tableProduct.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableBatch.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableImportItems.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableAlertBatches.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Đã xóa hàm styleTableHeaders() ở đây để trả về style gốc của hệ thống

        setupTableColumns();
        setupTemporaryTableColumns();
        setupAlertTableColumns();
        setupDropdowns(); 
        
        onBtnHideModals(null);

        productList = FXCollections.observableArrayList();
        setupSearchAndFilter();
        
        setupSearchDropdown();
        updateStatistics();

        tableProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                lblBatchDetailTitle.setText("LÔ SẢN PHẨM : " + newSelection.getName().toUpperCase());
                loadBatchesForProduct(newSelection);
            } else {
                lblBatchDetailTitle.setText("CHI TIẾT LÔ HÀNG NHẬP THỰC TẾ");
                tableBatch.setItems(FXCollections.observableArrayList());
            }
        });
    }

    private String getProductNameById(String masp) {
        for (Medicine m : productList) {
            if (m.getId() != null && m.getId().equals(masp)) {
                return m.getName();
            }
        }
        return masp; 
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colUnit.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        colIngredient.setCellValueFactory(cellData -> cellData.getValue().ingredientProperty());
        colUsage.setCellValueFactory(cellData -> cellData.getValue().usageProperty());
        colCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty()); 
        colPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        colBatchId.setCellValueFactory(cellData -> cellData.getValue().batchIdProperty());
        colMfgDate.setCellValueFactory(cellData -> cellData.getValue().mfgDateProperty());
        colExpDate.setCellValueFactory(cellData -> cellData.getValue().expDateProperty());
        colImportDate.setCellValueFactory(cellData -> cellData.getValue().importDateProperty());
        colImportPrice.setCellValueFactory(cellData -> cellData.getValue().importPriceProperty());
        colBatchQuantity.setCellValueFactory(cellData -> cellData.getValue().currentQtyProperty()); 
        colBatchStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        tableBatch.setRowFactory(tv -> {
            TableRow<Batch> row = new TableRow<>() {
                @Override
                protected void updateItem(Batch item, boolean empty) {
                    super.updateItem(item, empty);
                    updateBatchRowStyle(this, item, empty);
                }
            };
            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                updateBatchRowStyle(row, row.getItem(), row.isEmpty());
            });
            return row;
        });
    }

    private void setupAlertTableColumns() {
        colAlertBatchId.setCellValueFactory(cellData -> cellData.getValue().batchIdProperty());
        colAlertProductId.setCellValueFactory(cellData -> cellData.getValue().productNameProperty()); 
        colAlertMfgDate.setCellValueFactory(cellData -> cellData.getValue().mfgDateProperty());
        colAlertExpDate.setCellValueFactory(cellData -> cellData.getValue().expDateProperty());
        colAlertImportDate.setCellValueFactory(cellData -> cellData.getValue().importDateProperty());
        colAlertImportPrice.setCellValueFactory(cellData -> cellData.getValue().importPriceProperty());
        colAlertQuantity.setCellValueFactory(cellData -> cellData.getValue().currentQtyProperty());
        colAlertStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        tableAlertBatches.setRowFactory(tv -> {
            TableRow<Batch> row = new TableRow<>() {
                @Override
                protected void updateItem(Batch item, boolean empty) {
                    super.updateItem(item, empty);
                    updateBatchRowStyle(this, item, empty);
                }
            };
            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                updateBatchRowStyle(row, row.getItem(), row.isEmpty());
            });
            return row;
        });
    }

    private void updateBatchRowStyle(TableRow<Batch> row, Batch item, boolean empty) {
        if (item == null || empty) {
            row.setStyle("");
        } else {
            try {
                int qty = Integer.parseInt(item.currentQtyProperty().get());
                LocalDate expDate = LocalDate.parse(item.expDateProperty().get());
                LocalDate now = LocalDate.now();

                String bgColor = "";
                if (qty == 0 || expDate.isBefore(now)) {
                    bgColor = "#fee2e2"; 
                } else if (qty <= 10 || expDate.isBefore(now.plusMonths(3))) {
                    bgColor = "#ffedd5"; 
                }

                if (row.isSelected()) {
                    row.setStyle("-fx-background-color: #bae6fd; -fx-text-background-color: #0f172a;");
                } else {
                    if (!bgColor.isEmpty()) {
                        row.setStyle("-fx-background-color: " + bgColor + "; -fx-text-background-color: #0f172a;");
                    } else {
                        row.setStyle("");
                    }
                }
            } catch (Exception e) {
                row.setStyle("");
            }
        }
    }

    private void setupTemporaryTableColumns() {
        colImpProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colImpKho.setCellValueFactory(new PropertyValueFactory<>("makho"));
        colImpQty.setCellValueFactory(new PropertyValueFactory<>("sl"));
        colImpPrice.setCellValueFactory(new PropertyValueFactory<>("gianhap"));
        colImpMfg.setCellValueFactory(new PropertyValueFactory<>("ngaysx"));
        colImpExp.setCellValueFactory(new PropertyValueFactory<>("hsd"));
        colImpAction.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>("Xóa"));
        colImpAction.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("✕");
            {
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setOnAction(e -> {
                    temporaryImportList.remove(getTableView().getItems().get(getIndex()));
                    calculateTotalImportValue();
                });
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
        tableImportItems.setItems(temporaryImportList);
    }

    private void setupSearchDropdown() {
        ObservableList<String> searchCategories = FXCollections.observableArrayList("Tất cả danh mục");
        if (cbCategory != null) { 
            cbCategory.setItems(searchCategories); 
            cbCategory.getSelectionModel().selectFirst(); 
            
            ApiService.get("/api/warehouse/categories").thenAccept(response -> {
                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        try {
                            JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                            for (JsonNode node : dataNode) {
                                String id = node.has("madm") ? node.get("madm").asText() : (node.has("maDm") ? node.get("maDm").asText() : "");
                                String name = node.has("tendm") ? node.get("tendm").asText() : (node.has("tenDm") ? node.get("tenDm").asText() : id);
                                
                                if (!id.isEmpty()) {
                                    categoryDictionary.put(id, name);
                                    searchCategories.add(name);       
                                }
                            }
                            cbCategory.setItems(searchCategories);
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                    loadProductData();
                });
            });
        } else {
            loadProductData(); 
        }
    }

    private void setupDropdowns() {
        if (cbKhoImport != null) {
            ApiService.get("/api/warehouse/warehouses").thenAccept(response -> {
                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        try {
                            JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                            ObservableList<String> list = FXCollections.observableArrayList();
                            for (JsonNode node : dataNode) list.add(node.get("makho").asText() + " - " + node.get("loaikho").asText());
                            cbKhoImport.setItems(list);
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                });
            });
        }
        if (cbNhaCungCapImport != null) {
            cbNhaCungCapImport.getEditor().setOnKeyReleased(event -> {
                String keyword = cbNhaCungCapImport.getEditor().getText();
                if (keyword.length() >= 2) {
                    ApiService.get("/api/warehouse/suppliers?search=" + keyword).thenAccept(response -> {
                        Platform.runLater(() -> {
                            if (response.statusCode() == 200) {
                                try {
                                    JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                                    ObservableList<String> list = FXCollections.observableArrayList();
                                    
                                    if (dataNode.size() == 0) {
                                        list.add("Không tìm thấy NCC! Vui lòng Thêm mới.");
                                    } else {
                                        for (JsonNode node : dataNode) list.add(node.get("mancc").asText() + " - " + node.get("tenncc").asText());
                                    }
                                    
                                    String current = cbNhaCungCapImport.getEditor().getText();
                                    cbNhaCungCapImport.setItems(list);
                                    cbNhaCungCapImport.getEditor().setText(current);
                                    cbNhaCungCapImport.getEditor().positionCaret(current.length());
                                    cbNhaCungCapImport.show();
                                } catch (Exception e) { e.printStackTrace(); }
                            }
                        });
                    });
                }
            });
        }
        if (cbSanPhamImport != null) {
            cbSanPhamImport.getEditor().setOnKeyReleased(event -> {
                String keyword = cbSanPhamImport.getEditor().getText();
                if (keyword.length() >= 2) {
                    ApiService.get("/api/products?search=" + keyword).thenAccept(response -> {
                        Platform.runLater(() -> {
                            if (response.statusCode() == 200) {
                                try {
                                    JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                                    ObservableList<String> list = FXCollections.observableArrayList();
                                    
                                    if (dataNode.size() == 0) {
                                        list.add("Không tìm thấy Sản phẩm! Vui lòng Thêm mới.");
                                    } else {
                                        for (JsonNode node : dataNode) list.add(node.get("masp").asText() + " - " + node.get("tensanpham").asText());
                                    }
                                    
                                    String current = cbSanPhamImport.getEditor().getText();
                                    cbSanPhamImport.setItems(list);
                                    cbSanPhamImport.getEditor().setText(current);
                                    cbSanPhamImport.getEditor().positionCaret(current.length());
                                    cbSanPhamImport.show();
                                } catch (Exception e) { e.printStackTrace(); }
                            }
                        });
                    });
                }
            });
        }
    }

    private void loadProductData() {
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
                            
                            String categoryName = "Không xác định";
                            if (node.has("danhMuc") && !node.get("danhMuc").isNull()) {
                                JsonNode dmNode = node.get("danhMuc");
                                categoryName = dmNode.has("tendm") ? dmNode.get("tendm").asText() : 
                                              (dmNode.has("tenDm") ? dmNode.get("tenDm").asText() : "Không xác định");
                            } else if (node.has("tendm") || node.has("tenDm")) {
                                categoryName = node.has("tendm") ? node.get("tendm").asText() : node.get("tenDm").asText();
                            } else if (node.has("madm") || node.has("maDm")) {
                                String madm = node.has("madm") ? node.get("madm").asText() : node.get("maDm").asText();
                                categoryName = categoryDictionary.getOrDefault(madm, madm.isEmpty() ? "Không xác định" : madm);
                            }

                            productList.add(new Medicine(id, name, unit, ingredient, usage, categoryName, price));
                        }
                        if (lblTotalProducts != null) lblTotalProducts.setText(String.valueOf(productList.size()));
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
    }

    private void populateBatchTable(String jsonBody) {
        try {
            JsonNode dataNode = ApiService.mapper.readTree(jsonBody).get("data");
            ObservableList<Batch> batchList = FXCollections.observableArrayList();
            for (JsonNode node : dataNode) {
                String malo = node.has("malo") ? node.get("malo").asText() : "";
                String masp = node.has("masp") ? node.get("masp").asText() : "";
                String mfgDate = node.has("ngaysx") && !node.get("ngaysx").isNull() ? node.get("ngaysx").asText().split("T")[0] : "";
                String expDate = node.has("hsd") && !node.get("hsd").isNull() ? node.get("hsd").asText().split("T")[0] : "";
                String importDate = node.has("ngaynhap") && !node.get("ngaynhap").isNull() ? node.get("ngaynhap").asText().split("T")[0] : "";
                String qty = node.has("slsp") ? node.get("slsp").asText() : "0";
                String status = node.has("trangthai") ? node.get("trangthai").asText() : "";
                String importPrice = node.has("gianhap") && !node.get("gianhap").isNull() ? String.format("%,.0f", node.get("gianhap").asDouble()) : "0";
                
                String tensanpham = getProductNameById(masp); 

                batchList.add(new Batch(malo, tensanpham, mfgDate, expDate, importDate, qty, status, importPrice));
            }
            tableBatch.setItems(batchList);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private void loadBatchesForProduct(Medicine product) {
        if (product == null || product.getId() == null) return;
        ApiService.get("/api/warehouse/batches/" + product.getId()).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) populateBatchTable(response.body());
            });
        });
    }

    private void showAlertModal(String title, String apiUrl) {
        ApiService.get(apiUrl).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                        ObservableList<Batch> batchList = FXCollections.observableArrayList();
                        for (JsonNode node : dataNode) {
                            String malo = node.has("malo") ? node.get("malo").asText() : "";
                            String masp = node.has("masp") ? node.get("masp").asText() : "";
                            String mfgDate = node.has("ngaysx") && !node.get("ngaysx").isNull() ? node.get("ngaysx").asText().split("T")[0] : "";
                            String expDate = node.has("hsd") && !node.get("hsd").isNull() ? node.get("hsd").asText().split("T")[0] : "";
                            String importDate = node.has("ngaynhap") && !node.get("ngaynhap").isNull() ? node.get("ngaynhap").asText().split("T")[0] : "";
                            String qty = node.has("slsp") ? node.get("slsp").asText() : "0";
                            String status = node.has("trangthai") ? node.get("trangthai").asText() : "";
                            String importPrice = node.has("gianhap") && !node.get("gianhap").isNull() ? String.format("%,.0f", node.get("gianhap").asDouble()) : "0";
                            
                            String tensanpham = getProductNameById(masp); 

                            batchList.add(new Batch(malo, tensanpham, mfgDate, expDate, importDate, qty, status, importPrice));
                        }
                        tableAlertBatches.setItems(batchList);
                        lblAlertTitle.setText(title);
                        openModalWithEffect();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu cảnh báo.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể kết nối đến máy chủ.");
                }
            });
        });
    }

    private void openModalWithEffect() {
        modalAlertBatches.setVisible(true);
        modalAlertBatches.setScaleX(0.8);
        modalAlertBatches.setScaleY(0.8);
        modalAlertBatches.setOpacity(0);
        Timeline openTimeline = new Timeline(
            new KeyFrame(Duration.millis(200), new KeyValue(modalAlertBatches.scaleXProperty(), 1.0)),
            new KeyFrame(Duration.millis(200), new KeyValue(modalAlertBatches.scaleYProperty(), 1.0)),
            new KeyFrame(Duration.millis(200), new KeyValue(modalAlertBatches.opacityProperty(), 1.0))
        );
        openTimeline.play();
    }

    @FXML
    private void closeAlertModal() {
        Timeline closeTimeline = new Timeline(
            new KeyFrame(Duration.millis(150), new KeyValue(modalAlertBatches.scaleXProperty(), 0.8)),
            new KeyFrame(Duration.millis(150), new KeyValue(modalAlertBatches.scaleYProperty(), 0.8)),
            new KeyFrame(Duration.millis(150), new KeyValue(modalAlertBatches.opacityProperty(), 0.0))
        );
        closeTimeline.setOnFinished(e -> modalAlertBatches.setVisible(false));
        closeTimeline.play();
    }

    @FXML
    void showLowStockBatches(MouseEvent event) {
        showAlertModal("📦 DANH SÁCH SẮP HẾT HÀNG (CẦN NHẬP)", "/api/warehouse/alerts/low-stock");
    }

    @FXML
    void showExpiringBatches(MouseEvent event) {
        showAlertModal("⚠️ DANH SÁCH CẬN DATE / HẾT HẠN (CẦN XỬ LÝ)", "/api/warehouse/alerts/expiring-soon");
    }

    @FXML
    void toggleAiPanelWh(ActionEvent event) {
        isAiPanelWhOpen = !isAiPanelWhOpen;
        Timeline timeline = new Timeline();
        
        if (isAiPanelWhOpen) {
            aiPanelContentWh.setVisible(true);
            btnAiSuggestWh.setText("Đóng Gợi ý");
            KeyValue kvWidth = new KeyValue(aiPanelWh.maxWidthProperty(), 380.0, javafx.animation.Interpolator.EASE_BOTH);
            KeyValue kvPref = new KeyValue(aiPanelWh.prefWidthProperty(), 380.0, javafx.animation.Interpolator.EASE_BOTH);
            KeyValue kvOpacity = new KeyValue(aiPanelWh.opacityProperty(), 1.0, javafx.animation.Interpolator.EASE_BOTH);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(350), kvWidth, kvPref, kvOpacity));
            timeline.play();
            
            String keyword = txtTenSpNew.getText().trim();
            if (!keyword.isEmpty()) {
                fetchAiSuggestionsWh(keyword);
            } else {
                vboxAiResultsWh.getChildren().clear();
                vboxAiResultsWh.getChildren().add(new Label("Nhập tên thuốc để AI tìm kiếm..."));
            }
        } else {
            btnAiSuggestWh.setText("Gợi ý AI");
            KeyValue kvWidth = new KeyValue(aiPanelWh.maxWidthProperty(), 0.0, javafx.animation.Interpolator.EASE_BOTH);
            KeyValue kvPref = new KeyValue(aiPanelWh.prefWidthProperty(), 0.0, javafx.animation.Interpolator.EASE_BOTH);
            KeyValue kvOpacity = new KeyValue(aiPanelWh.opacityProperty(), 0.0, javafx.animation.Interpolator.EASE_BOTH);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(350), kvWidth, kvPref, kvOpacity));
            timeline.setOnFinished(e -> aiPanelContentWh.setVisible(false));
            timeline.play();
        }
    }

    private void fetchAiSuggestionsWh(String keyword) {
        vboxAiResultsWh.getChildren().clear();
        Label lblLoading = new Label("⏳ AI đang xử lý dữ liệu...");
        lblLoading.setStyle("-fx-font-style: italic; -fx-text-fill: #64748b;");
        vboxAiResultsWh.getChildren().add(lblLoading);
        btnAiSuggestWh.setDisable(true);

        try {
            String encodedKeyword = java.net.URLEncoder.encode(keyword, java.nio.charset.StandardCharsets.UTF_8);
            ApiService.get("/api/medicines/ai-suggest?keyword=" + encodedKeyword).thenAccept(response -> {
                Platform.runLater(() -> {
                    btnAiSuggestWh.setDisable(false);
                    vboxAiResultsWh.getChildren().clear();
                    
                    if (response.statusCode() == 200) {
                        try {
                            JsonNode data = ApiService.mapper.readTree(response.body());
                            if (data.isEmpty() || !data.isArray()) {
                                vboxAiResultsWh.getChildren().add(new Label("❌ Không tìm thấy thông tin phù hợp."));
                                return;
                            }
                            for (JsonNode item : data) {
                                vboxAiResultsWh.getChildren().add(createAiResultCardWh(item));
                            }
                        } catch (Exception e) {
                            vboxAiResultsWh.getChildren().add(new Label("❌ Lỗi giải mã dữ liệu JSON."));
                        }
                    } else {
                        vboxAiResultsWh.getChildren().add(new Label("❌ Kết nối AI thất bại (Lỗi " + response.statusCode() + ")"));
                    }
                });
            });
        } catch (Exception e) {
            btnAiSuggestWh.setDisable(false);
        }
    }

    private VBox createAiResultCardWh(JsonNode item) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;");
        
        String tenChuan = item.path("tenChuan").asText("");
        String donViTinh = item.path("donViTinh").asText("");
        String thanhPhan = item.path("thanhPhan").asText("");
        String congDung = item.path("congDung").asText("");
        
        Label lblName = new Label(tenChuan);
        lblName.setStyle("-fx-font-weight: bold; -fx-text-fill: #0f766e; -fx-font-size: 14px;");
        lblName.setWrapText(true);
        
        Label lblUnit = new Label("• ĐVT: " + donViTinh);
        lblUnit.setStyle("-fx-text-fill: #475569;");
        
        Label lblActive = new Label("• TP: " + thanhPhan);
        lblActive.setWrapText(true);
        lblActive.setStyle("-fx-text-fill: #475569;");
        
        Label lblUsage = new Label("• Chỉ định: " + congDung);
        lblUsage.setWrapText(true);
        lblUsage.setStyle("-fx-text-fill: #475569;");
        
        Button btnSelect = new Button("Thêm dữ liệu này");
        btnSelect.setMaxWidth(Double.MAX_VALUE);
        btnSelect.setStyle("-fx-background-color: #cffafe; -fx-text-fill: #0891b2; -fx-font-weight: bold; -fx-background-radius: 5;");
        
        btnSelect.setOnAction(e -> {
            txtTenSpNew.setText(tenChuan);
            txtDvtSpNew.setText(donViTinh);
            txtThanhPhanNew.setText(thanhPhan);
            txtCongDungNew.setText(congDung);
            toggleAiPanelWh(null);
        });
        
        card.getChildren().addAll(lblName, lblUnit, lblActive, lblUsage, btnSelect);
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #94a3b8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;"));
        return card;
    }

    @FXML 
    void onBtnAddSupplierNew(ActionEvent event) {
        txtTenNccNew.clear();
        txtSdtNccNew.clear();
        txtEmailNccNew.clear();
        txtDiaChiNccNew.clear();
        modalAddSupplier.setVisible(true);
    }

    @FXML 
    void onBtnSubmitAddSupplier(ActionEvent event) {
        String tenncc = txtTenNccNew.getText().trim();
        if (tenncc.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Tên Nhà Cung Cấp!"); return;
        }

        ObjectNode json = ApiService.mapper.createObjectNode();
        json.put("tenncc", tenncc);
        json.put("sdt", txtSdtNccNew.getText().trim());
        json.put("email", txtEmailNccNew.getText().trim());
        json.put("diachi", txtDiaChiNccNew.getText().trim());

        ApiService.post("/api/warehouse/suppliers", json.toString()).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm nhà cung cấp mới thành công!");
                    modalAddSupplier.setVisible(false);
                    setupDropdowns(); 
                } else {
                    String errorMessage = "Thêm nhà cung cấp lỗi: " + response.statusCode();
                    try {
                        JsonNode root = ApiService.mapper.readTree(response.body());
                        if (root.has("message") && !root.get("message").asText().isBlank()) {
                            errorMessage = root.get("message").asText();
                        }
                    } catch (Exception ignored) {}
                    showAlert(Alert.AlertType.ERROR, "Thất bại", errorMessage);
                }
            });
        });
    }

    @FXML 
    void onBtnAddProductNew(ActionEvent event) {
        txtTenSpNew.clear(); txtDvtSpNew.clear(); txtThanhPhanNew.clear(); txtCongDungNew.clear();
        
        ObservableList<String> listDm = FXCollections.observableArrayList(categoryDictionary.values());
        cbCategoryNew.setItems(listDm);
        if (!listDm.isEmpty()) cbCategoryNew.getSelectionModel().selectFirst();
        
        modalAddProduct.setVisible(true);
    }

    @FXML 
    void onBtnSubmitAddProduct(ActionEvent event) {
        String tensp = txtTenSpNew.getText().trim();
        String dvt = txtDvtSpNew.getText().trim();
        String selectedCategoryName = cbCategoryNew.getValue();
        
        if (tensp.isEmpty() || dvt.isEmpty() || selectedCategoryName == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Tên SP, ĐVT và chọn Danh mục!"); return;
        }

        String madm = "";
        for (Map.Entry<String, String> entry : categoryDictionary.entrySet()) {
            if (entry.getValue().equals(selectedCategoryName)) {
                madm = entry.getKey(); break;
            }
        }

        ObjectNode json = ApiService.mapper.createObjectNode();
        json.put("tensanpham", tensp);
        json.put("madm", madm);
        json.put("dvt", dvt);
        json.put("thanhphan", txtThanhPhanNew.getText().trim());
        json.put("congdung", txtCongDungNew.getText().trim());
        json.put("giaban", 0.0);

        ApiService.post("/api/warehouse/products", json.toString()).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm sản phẩm mới thành công!");
                    modalAddProduct.setVisible(false);
                    loadProductData(); 
                } else {
                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Thêm sản phẩm lỗi: " + response.statusCode());
                }
            });
        });
    }

    @FXML void handleAddProduct(ActionEvent event) { 
        temporaryImportList.clear();
        lblTotalImportValue.setText("Tổng tiền phiếu: 0 VNĐ");
        if (cbNhaCungCapImport != null) cbNhaCungCapImport.getEditor().clear();
        modalNhapKho.setVisible(true); 
    }

    @FXML void onBtnAddImportItem(ActionEvent event) {
        String rawProduct = cbSanPhamImport.getEditor().getText();
        String rawKho = cbKhoImport.getValue();
        String dvt = txtDvtImport.getText().trim();
        String qtyStr = txtSoLuongImport.getText().trim();
        String priceStr = txtGiaNhapImport.getText().trim();
        LocalDate mfg = dpNgaySanXuatImport.getValue();
        LocalDate exp = dpHanSuDungImport.getValue();
        String ghiChu = txtGhiChuImport.getText() != null ? txtGhiChuImport.getText().trim() : "";

        if (rawProduct == null || rawKho == null || dvt.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty() || exp == null) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ các trường bắt buộc (*)"); return;
        }
        
        if (!rawProduct.contains(" - ")) {
            showAlert(Alert.AlertType.WARNING, "Sản phẩm chưa tồn tại", "Sản phẩm bạn nhập chưa có trong hệ thống!\nVui lòng đóng phiếu và nhấn nút '+ Thêm Sản Phẩm' ở ngoài trước khi nhập hàng."); 
            return;
        }

        try {
            String masp = rawProduct.split("-")[0].trim();
            String tensp = rawProduct.split("-")[1].trim();
            String makho = rawKho.split("-")[0].trim();
            int sl = Integer.parseInt(qtyStr);
            double gianhap = Double.parseDouble(priceStr);
            String mfgStr = mfg != null ? mfg.toString() : "";
            
            temporaryImportList.add(new ImportItemRow(masp, tensp, makho, sl, gianhap, dvt, ghiChu, mfgStr, exp.toString()));
            calculateTotalImportValue();

            cbSanPhamImport.getEditor().clear(); txtSoLuongImport.clear(); txtGiaNhapImport.clear();
            txtGhiChuImport.clear(); dpNgaySanXuatImport.setValue(null); dpHanSuDungImport.setValue(null);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Sai định dạng", "Số lượng và Giá nhập phải là ký tự số!");
        }
    }

    private void calculateTotalImportValue() {
        double total = temporaryImportList.stream().mapToDouble(item -> item.getSl() * item.getGianhap()).sum();
        lblTotalImportValue.setText(String.format("Tổng tiền phiếu: %,.0f VNĐ", total));
    }

    @FXML void onBtnXacNhanNhapKhoClick(ActionEvent event) {
        String rawNcc = cbNhaCungCapImport.getEditor().getText();
        
        if (rawNcc == null || rawNcc.isEmpty() || !rawNcc.contains(" - ")) {
            showAlert(Alert.AlertType.WARNING, "Nhà cung cấp chưa tồn tại", "Nhà cung cấp bạn nhập chưa có trong hệ thống!\nVui lòng đóng phiếu và nhấn nút '+ Thêm Nhà Cung Cấp' ở ngoài trước khi lưu phiếu."); 
            return;
        }
        
        if (temporaryImportList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Danh sách trống", "Phiếu nhập của bạn chưa có bất kỳ lô thuốc nào!"); return;
        }

        try {
            ObjectNode rootJson = ApiService.mapper.createObjectNode();
            String manv = Session.getCurrentUser() != null ? Session.getCurrentUser().getManv() : "NV260001";
            rootJson.put("manv", manv);
            rootJson.put("mancc", rawNcc.split("-")[0].trim());

            ArrayNode itemsArray = ApiService.mapper.createArrayNode();
            for (ImportItemRow row : temporaryImportList) {
                ObjectNode itemNode = ApiService.mapper.createObjectNode();
                itemNode.put("masp", row.getMasp()); itemNode.put("makho", row.getMakho());
                itemNode.put("sl", row.getSl()); itemNode.put("gianhap", row.getGianhap());
                itemNode.put("dvt", row.getDvt()); itemNode.put("ghichu", row.getGhichu());
                itemNode.put("ngaysx", row.getNgaysx().isEmpty() ? null : row.getNgaysx() + "T00:00:00.000Z");
                itemNode.put("hsd", row.getHsd() + "T00:00:00.000Z");
                itemsArray.add(itemNode);
            }
            rootJson.set("items", itemsArray);

            ApiService.post("/api/warehouse/import-receipts", rootJson.toString()).thenAccept(response -> {
                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã tạo phiếu nhập kho thành công!");
                        onBtnHideModals(null); loadProductData(); temporaryImportList.clear(); calculateTotalImportValue();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Mã phản hồi: " + response.statusCode());
                    }
                });
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void onBtnHideModals(ActionEvent event) {
        if (modalNhapKho != null) modalNhapKho.setVisible(false);
        if (modalAddProduct != null) {
            modalAddProduct.setVisible(false);
            if (isAiPanelWhOpen) { toggleAiPanelWh(null); }
        }
        if (modalAddSupplier != null) modalAddSupplier.setVisible(false);
    }

    private void setupSearchAndFilter() {
        filteredData = new FilteredList<>(productList, p -> true);
        txtSearch.textProperty().addListener((obs, old, newVal) -> updatePredicate());
        cbCategory.valueProperty().addListener((obs, old, newVal) -> updatePredicate());
        SortedList<Medicine> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableProduct.comparatorProperty());
        tableProduct.setItems(sortedData);
    }

    private void updatePredicate() {
        String searchText = txtSearch.getText().toLowerCase();
        String selectedCat = cbCategory.getValue();
        filteredData.setPredicate(product -> {
            boolean matchesSearch = searchText.isEmpty() || product.getName().toLowerCase().contains(searchText) || product.getId().toLowerCase().contains(searchText);
            boolean matchesCat = selectedCat == null || selectedCat.equals("Tất cả danh mục") || product.getCategory().contains(selectedCat) || selectedCat.contains(product.getCategory());
            return matchesSearch && matchesCat;
        });
        tableBatch.setItems(FXCollections.observableArrayList());
    }

    private void updateStatistics() {
        ApiService.get("/api/warehouse/alerts/low-stock").thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode root = ApiService.mapper.readTree(response.body());
                        if (lblLowStock != null) lblLowStock.setText(String.valueOf(root.get("data").size()));
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
        ApiService.get("/api/warehouse/alerts/expiring-soon").thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode root = ApiService.mapper.readTree(response.body());
                        if (lblExpiring != null) lblExpiring.setText(String.valueOf(root.get("data").size()));
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
    }

    @FXML void onBtnXuatExcelClick(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Đang trích xuất dữ liệu tồn kho ra file Excel...");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}