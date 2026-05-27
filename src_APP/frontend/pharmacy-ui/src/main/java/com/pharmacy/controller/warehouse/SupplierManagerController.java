package com.pharmacy.controller.warehouse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.pharmacy.model.ImportReceipt;
import com.pharmacy.model.Supplier;
import com.pharmacy.model.ViewImportDetailRow;
import com.pharmacy.util.ApiService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SupplierManagerController {

    // ===== BẢNG MASTER: NHÀ CUNG CẤP =====
    @FXML private TextField txtSearch;
    @FXML private TableView<Supplier> tblSuppliers;
    @FXML private TableColumn<Supplier, String> colMaNCC, colTenNCC, colSDT, colEmail, colDiaChi;

    // ===== BẢNG DETAIL: PHIẾU NHẬP =====
    @FXML private Label lblReceiptHistory;
    @FXML private TableView<ImportReceipt> tblReceipts;
    @FXML private TableColumn<ImportReceipt, String> colMaPhieu, colNgayNhap, colTrangThai;
    @FXML private TableColumn<ImportReceipt, Number> colTongTien;
    @FXML private TableColumn<ImportReceipt, Void> colChiTiet;

    // ===== MODAL THÊM/SỬA NCC (Đã bỏ txtMaNccModal) =====
    @FXML private Pane modalSupplier;
    @FXML private Label lblModalTitle;
    @FXML private TextField txtTenNccModal, txtSdtModal, txtEmailModal, txtDiaChiModal;
    private boolean isEditMode = false;

    // ===== MODAL XEM CHI TIẾT PHIẾU NHẬP =====
    @FXML private Pane modalViewImport;
    @FXML private Label lblViewImpTitle, lblViewImpTotal;
    @FXML private TableView<ViewImportDetailRow> tableViewImportItems;
    @FXML private TableColumn<ViewImportDetailRow, String> colViewImpProductName, colViewImpMalo;
    @FXML private TableColumn<ViewImportDetailRow, Integer> colViewImpQty;
    @FXML private TableColumn<ViewImportDetailRow, Double> colViewImpPrice, colViewImpTotal;

    // ===== DATA =====
    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();
    private ObservableList<ImportReceipt> receiptList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tblSuppliers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblReceipts.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setupTables();
        handleHideModals(); 
        loadSuppliers("");

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            loadSuppliers(newVal != null ? newVal.trim() : "");
        });

        tblSuppliers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblReceiptHistory.setText("LỊCH SỬ PHIẾU NHẬP CỦA: " + newVal.getTenncc().toUpperCase());
                loadReceiptsForSupplier(newVal.getMancc());
            } else {
                lblReceiptHistory.setText("LỊCH SỬ PHIẾU NHẬP CỦA NHÀ CUNG CẤP: ...");
                receiptList.clear();
            }
        });
    }

    private void setupTables() {
        // MAP CỘT THỦ CÔNG QUA LAMBDA (Vượt mọi lỗi không nhận dữ liệu của propertyFactory)
        colMaNCC.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMancc() != null ? cell.getValue().getMancc() : ""));
        colTenNCC.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenncc() != null ? cell.getValue().getTenncc() : ""));
        colSDT.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSdt() != null ? cell.getValue().getSdt() : ""));
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail() != null ? cell.getValue().getEmail() : ""));
        colDiaChi.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDiachi() != null ? cell.getValue().getDiachi() : ""));
        tblSuppliers.setItems(supplierList);

        colMaPhieu.setCellValueFactory(new PropertyValueFactory<>("mapn"));
        colNgayNhap.setCellValueFactory(new PropertyValueFactory<>("ngaynhap"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
        colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongtien"));
        colTongTien.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });
        tblReceipts.setItems(receiptList);

        colChiTiet.setCellFactory(param -> new TableCell<>() {
            private final Button btnView = new Button("👁");
            {
                btnView.setStyle("-fx-background-color: #0f766e; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-background-radius: 5;");
                btnView.setOnAction(event -> handleViewImportReceipt(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnView);
            }
        });

        colViewImpProductName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTensanpham()));
        colViewImpMalo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMalo()));
        colViewImpQty.setCellValueFactory(new PropertyValueFactory<>("sl"));
        colViewImpPrice.setCellValueFactory(new PropertyValueFactory<>("gianhap"));
        colViewImpTotal.setCellValueFactory(new PropertyValueFactory<>("thanhtien"));

        colViewImpPrice.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%,.0f VNĐ", item));
            }
        });
        colViewImpTotal.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%,.0f VNĐ", item));
            }
        });
    }

    // ================== API CALLS ==================

    @FXML private void handleSearch() {
        loadSuppliers(txtSearch.getText().trim());
    }

    private void loadSuppliers(String search) {
        String endpoint = "/api/warehouse/suppliers" + (search.isEmpty() ? "" : "?search=" + search.replace(" ", "%20"));
        ApiService.get(endpoint).thenAccept(response -> {
            try {
                if (response.statusCode() == 200) {
                    JsonNode root = ApiService.mapper.readTree(response.body());
                    List<Supplier> list = ApiService.mapper.readValue(root.get("data").traverse(), new TypeReference<>() {});
                    Platform.runLater(() -> supplierList.setAll(list));
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void loadReceiptsForSupplier(String maNcc) {
        receiptList.clear();
        String url = "/api/warehouse/import-receipts?search=" + URLEncoder.encode(maNcc, StandardCharsets.UTF_8);
        ApiService.get(url).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                        for (JsonNode node : dataNode) {
                            String mapn = node.has("mapn") ? node.get("mapn").asText() : "";
                            String mancc = node.has("mancc") ? node.get("mancc").asText() : "";
                            if (mancc.equalsIgnoreCase(maNcc)) {
                                String ngaynhap = node.has("ngaynhap") && node.get("ngaynhap") != null ? node.get("ngaynhap").asText().split("T")[0] : "";
                                double tongtien = node.has("tongtien") ? node.get("tongtien").asDouble() : 0.0;
                                String trangthai = node.has("trangthai") ? node.get("trangthai").asText() : "";
                                receiptList.add(new ImportReceipt(mapn, ngaynhap, mancc, tongtien, trangthai));
                            }
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
    }

    private void handleViewImportReceipt(ImportReceipt receipt) {
        lblViewImpTitle.setText("CHI TIẾT PHIẾU NHẬP: " + receipt.getMapn());
        lblViewImpTotal.setText(String.format("Tổng tiền: %,.0f VNĐ", receipt.getTongtien()));
        
        ObservableList<ViewImportDetailRow> details = FXCollections.observableArrayList();
        tableViewImportItems.setItems(details);

        ApiService.get("/api/warehouse/import-receipts/" + receipt.getMapn() + "/details").thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                        for (JsonNode node : dataNode) {
                            String malo = node.has("malo") ? node.get("malo").asText() : "";
                            String tensanpham = node.has("tensanpham") ? node.get("tensanpham").asText() : "Chưa cập nhật";
                            int sl = node.has("sl") ? node.get("sl").asInt() : 0;
                            double gianhap = node.has("gianhap") ? node.get("gianhap").asDouble() : 0.0;
                            details.add(new ViewImportDetailRow(tensanpham, malo, sl, gianhap, (sl * gianhap)));
                        }
                        modalViewImport.setVisible(true);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
    }

    // ================== THAO TÁC NGHIỆP VỤ (THÊM / SỬA / XÓA) ==================
    @FXML private void handleShowAddModal() {
        isEditMode = false;
        lblModalTitle.setText("THÊM NHÀ CUNG CẤP MỚI");
        txtTenNccModal.clear(); 
        txtSdtModal.clear(); 
        txtEmailModal.clear(); 
        txtDiaChiModal.clear();
        modalSupplier.setVisible(true);
    }

    @FXML private void handleShowEditModal() {
        Supplier selected = tblSuppliers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Cảnh báo", "Vui lòng chọn nhà cung cấp để sửa!");
            return;
        }
        isEditMode = true;
        lblModalTitle.setText("SỬA THÔNG TIN NHÀ CUNG CẤP");
        txtTenNccModal.setText(selected.getTenncc());
        txtSdtModal.setText(selected.getSdt());
        txtEmailModal.setText(selected.getEmail());
        txtDiaChiModal.setText(selected.getDiachi());
        modalSupplier.setVisible(true);
    }

    @FXML private void handleSaveSupplier() {
        String ten = txtTenNccModal.getText().trim();
        if (ten.isEmpty()) {
            showAlert("Lỗi", "Tên nhà cung cấp không được để trống!");
            return;
        }

        // Nếu là edit thì lấy mã từ dòng đang được chọn, nếu tạo mới thì chuỗi rỗng
        String ma = "";
        if (isEditMode) {
            Supplier selected = tblSuppliers.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ma = selected.getMancc();
            }
        }

        Supplier supplier = new Supplier(ma, ten, txtSdtModal.getText().trim(), txtEmailModal.getText().trim(), txtDiaChiModal.getText().trim());
        
        try {
            String jsonBody = ApiService.mapper.writeValueAsString(supplier);
            if (!isEditMode) {
                ApiService.post("/api/warehouse/suppliers", jsonBody).thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            handleHideModals();
                            loadSuppliers(""); 
                        } else showAlert("Lỗi", "Thêm thất bại: " + response.body());
                    });
                });
            } else {
                ApiService.put("/api/warehouse/suppliers/" + ma, jsonBody).thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            handleHideModals();
                            loadSuppliers(txtSearch.getText().trim());
                        } else showAlert("Lỗi", "Cập nhật thất bại: " + response.body());
                    });
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleDeleteSupplier() {
        Supplier selected = tblSuppliers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Cảnh báo", "Vui lòng chọn 1 nhà cung cấp để xoá!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Có chắc muốn xoá nhà cung cấp " + selected.getTenncc() + " không?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                ApiService.delete("/api/warehouse/suppliers/" + selected.getMancc()).thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) loadSuppliers(txtSearch.getText().trim());
                        else showAlert("Lỗi", "Xoá thất bại, có thể NCC này đã có dữ liệu phiếu nhập!");
                    });
                });
            }
        });
    }

    @FXML private void handleHideModals() {
        if (modalSupplier != null) modalSupplier.setVisible(false);
        if (modalViewImport != null) modalViewImport.setVisible(false);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}