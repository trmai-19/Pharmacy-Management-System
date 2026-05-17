package com.pharmacy.controller.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pharmacy.model.ImportReceipt;
import com.pharmacy.model.ReturnItemRow;
import com.pharmacy.model.SupplierReturnTicket;
import com.pharmacy.util.ApiService;
import com.pharmacy.util.Session;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

public class ReturnWarehouseController {

    @FXML private Button btnCreateReturn;
    
    // BẢNG 1: MASTER - LỊCH SỬ PHIẾU NHẬP
    @FXML private TextField txtSearchImport;
    @FXML private TableView<ImportReceipt> tableImportReceipts;
    @FXML private TableColumn<ImportReceipt, String> colImpId, colImpDate, colImpNcc, colImpStatus, colImpActionView;
    @FXML private TableColumn<ImportReceipt, Number> colImpTotal;

    // BẢNG 2: DETAIL - LỊCH SỬ TRẢ HÀNG
    @FXML private Label lblDetailReturn;
    @FXML private TableView<SupplierReturnTicket> tableReturn;
    @FXML private TableColumn<SupplierReturnTicket, String> colRetId, colRetDate, colRetReason, colRetActionView;
    @FXML private TableColumn<SupplierReturnTicket, Number> colRetTotal;

    // MODAL LẬP PHIẾU TRẢ
    @FXML private Pane modalReturnNCC;
    @FXML private TextField txtMaPhieuNhapReturn, txtLyDoReturn, txtSoLuongReturn;
    @FXML private ComboBox<String> cbMaLoReturn; 
    @FXML private TableView<ReturnItemRow> tableReturnItems;
    @FXML private TableColumn<ReturnItemRow, String> colRetMaLo, colRetAction;
    @FXML private TableColumn<ReturnItemRow, Number> colRetQty;

    // MODAL XEM CHI TIẾT PHIẾU NHẬP
    @FXML private Pane modalViewImport;
    @FXML private Label lblViewImpId, lblViewImpDate, lblViewImpNcc, lblViewImpTotal;
    @FXML private TableView<ViewImportDetailRow> tableViewImportItems;
    @FXML private TableColumn<ViewImportDetailRow, String> colViewImpMalo;
    @FXML private TableColumn<ViewImportDetailRow, Integer> colViewImpQty;
    @FXML private TableColumn<ViewImportDetailRow, Double> colViewImpPrice, colViewImpTotal;

    // MODAL XEM CHI TIẾT PHIẾU TRẢ
    @FXML private Pane modalViewReturn;
    @FXML private Label lblViewRetId, lblViewRetOriginalId, lblViewRetDate, lblViewRetReason, lblViewRetTotal;
    @FXML private TableView<ViewReturnDetailRow> tableViewReturnItems;
    @FXML private TableColumn<ViewReturnDetailRow, String> colViewRetMalo;
    @FXML private TableColumn<ViewReturnDetailRow, Integer> colViewRetQty;
    @FXML private TableColumn<ViewReturnDetailRow, Double> colViewRetPrice, colViewRetTotal;

    private ObservableList<ImportReceipt> importList = FXCollections.observableArrayList();
    private ObservableList<SupplierReturnTicket> detailReturnList = FXCollections.observableArrayList();
    private ObservableList<ReturnItemRow> temporaryReturnList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("🔄 Đã nạp giao diện Quản Lý Đổi Trả - Bổ sung tính năng View Chi Tiết (Mắt 👁)");

        setupTableColumns();
        hideAllModals();
        loadImportReceipts();

        tableImportReceipts.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                btnCreateReturn.setDisable(false);
                lblDetailReturn.setText("LỊCH SỬ TRẢ HÀNG CỦA PHIẾU: " + newVal.getMapn());
                loadReturnHistoryForReceipt(newVal.getMapn());
            } else {
                btnCreateReturn.setDisable(true);
                lblDetailReturn.setText("LỊCH SỬ TRẢ HÀNG CỦA PHIẾU: ...");
                tableReturn.setItems(FXCollections.observableArrayList());
            }
        });
    }

    private void setupTableColumns() {
        // --- Cột Master ---
        colImpId.setCellValueFactory(new PropertyValueFactory<>("mapn"));
        colImpDate.setCellValueFactory(new PropertyValueFactory<>("ngaynhap"));
        colImpNcc.setCellValueFactory(new PropertyValueFactory<>("mancc"));
        colImpTotal.setCellValueFactory(new PropertyValueFactory<>("tongtien"));
        colImpStatus.setCellValueFactory(new PropertyValueFactory<>("trangthai"));

        colImpTotal.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });

        colImpActionView.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>("Xem"));
        colImpActionView.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁");
            {
                viewBtn.setStyle("-fx-background-color: #0f766e; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 14px;");
                viewBtn.setOnAction(e -> handleViewImportReceipt(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });

        // --- Cột Detail ---
        colRetId.setCellValueFactory(new PropertyValueFactory<>("maptNcc"));
        colRetDate.setCellValueFactory(new PropertyValueFactory<>("ngaytra"));
        colRetReason.setCellValueFactory(new PropertyValueFactory<>("lydotra"));
        colRetTotal.setCellValueFactory(new PropertyValueFactory<>("tongtien"));

        colRetTotal.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%,.0f VNĐ", item.doubleValue()));
            }
        });

        colRetActionView.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>("Xem"));
        colRetActionView.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁");
            {
                viewBtn.setStyle("-fx-background-color: #c2410c; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 14px;");
                viewBtn.setOnAction(e -> handleViewReturnTicket(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });

        // --- Cột Bảng Tạm Trả Hàng ---
        colRetMaLo.setCellValueFactory(new PropertyValueFactory<>("malo"));
        colRetQty.setCellValueFactory(new PropertyValueFactory<>("sl"));
        colRetAction.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>("Xóa"));
        colRetAction.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("✕");
            {
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand;");
                deleteBtn.setOnAction(e -> temporaryReturnList.remove(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
        tableReturnItems.setItems(temporaryReturnList);

        // --- Cột Modal Xem Lịch Sử Phiếu Nhập ---
        colViewImpMalo.setCellValueFactory(new PropertyValueFactory<>("malo"));
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

        // --- Cột Modal Xem Lịch Sử Phiếu Trả ---
        colViewRetMalo.setCellValueFactory(new PropertyValueFactory<>("malo"));
        colViewRetQty.setCellValueFactory(new PropertyValueFactory<>("sl"));
        colViewRetPrice.setCellValueFactory(new PropertyValueFactory<>("dongiatra"));
        colViewRetTotal.setCellValueFactory(new PropertyValueFactory<>("thanhtien"));

        colViewRetPrice.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%,.0f VNĐ", item));
            }
        });
        colViewRetTotal.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%,.0f VNĐ", item));
            }
        });
    }

    private void loadImportReceipts() {
        importList.clear();
        ApiService.get("/api/warehouse/import-receipts").thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                        for (JsonNode node : dataNode) {
                            String mapn = node.has("mapn") ? node.get("mapn").asText() : "";
                            String ngaynhap = node.has("ngaynhap") && node.get("ngaynhap") != null ? node.get("ngaynhap").asText().split("T")[0] : "";
                            String mancc = node.has("mancc") ? node.get("mancc").asText() : "";
                            double tongtien = node.has("tongtien") ? node.get("tongtien").asDouble() : 0.0;
                            String trangthai = node.has("trangthai") ? node.get("trangthai").asText() : "";
                            importList.add(new ImportReceipt(mapn, ngaynhap, mancc, tongtien, trangthai));
                        }
                        tableImportReceipts.setItems(importList);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
    }

    private void loadReturnHistoryForReceipt(String mapn) {
        detailReturnList.clear();
        ApiService.get("/api/warehouse/supplier-returns/receipt/" + mapn).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                        for (JsonNode node : dataNode) {
                            String maptNcc = node.has("maptNcc") ? node.get("maptNcc").asText() : "";
                            String ngaytra = node.has("ngaytra") && node.get("ngaytra") != null ? node.get("ngaytra").asText().split("T")[0] : "";
                            String lydotra = node.has("lydotra") ? node.get("lydotra").asText() : "";
                            double tongtien = node.has("tongtien") ? node.get("tongtien").asDouble() : 0.0;
                            detailReturnList.add(new SupplierReturnTicket(maptNcc, ngaytra, lydotra, tongtien));
                        }
                        tableReturn.setItems(detailReturnList);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
    }

    // ===============================================
    // HÀM XỬ LÝ CLICK NÚT MẮT 👁 (VIEW DETAIL)
    // ===============================================

    private void handleViewImportReceipt(ImportReceipt receipt) {
        lblViewImpId.setText(receipt.getMapn());
        lblViewImpDate.setText(receipt.getNgaynhap());
        lblViewImpNcc.setText(receipt.getMancc());
        lblViewImpTotal.setText(String.format("%,.0f VNĐ", receipt.getTongtien()));

        ObservableList<ViewImportDetailRow> details = FXCollections.observableArrayList();
        tableViewImportItems.setItems(details);

        ApiService.get("/api/warehouse/import-receipts/" + receipt.getMapn() + "/details").thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                        for (JsonNode node : dataNode) {
                            String malo = node.has("malo") ? node.get("malo").asText() : "";
                            int sl = node.has("sl") ? node.get("sl").asInt() : 0;
                            double gianhap = node.has("gianhap") ? node.get("gianhap").asDouble() : 0.0;
                            double thanhtien = sl * gianhap;
                            details.add(new ViewImportDetailRow(malo, sl, gianhap, thanhtien));
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
        modalViewImport.setVisible(true);
    }

    private void handleViewReturnTicket(SupplierReturnTicket ticket) {
        ImportReceipt selectedReceipt = tableImportReceipts.getSelectionModel().getSelectedItem();
        String mapnGoc = selectedReceipt != null ? selectedReceipt.getMapn() : "...";

        lblViewRetId.setText(ticket.getMaptNcc());
        lblViewRetOriginalId.setText(mapnGoc);
        lblViewRetDate.setText(ticket.getNgaytra());
        lblViewRetReason.setText(ticket.getLydotra());
        lblViewRetTotal.setText(String.format("%,.0f VNĐ", ticket.getTongtien()));

        ObservableList<ViewReturnDetailRow> details = FXCollections.observableArrayList();
        tableViewReturnItems.setItems(details);

        ApiService.get("/api/warehouse/supplier-returns/receipt/" + mapnGoc).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                        for (JsonNode node : dataNode) {
                            if (node.get("maptNcc").asText().equals(ticket.getMaptNcc())) {
                                JsonNode itemsArray = node.get("items");
                                for (JsonNode item : itemsArray) {
                                    String malo = item.has("malo") ? item.get("malo").asText() : "";
                                    int sl = item.has("sl") ? item.get("sl").asInt() : 0;
                                    double dongia = item.has("dongiatra") ? item.get("dongiatra").asDouble() : 0.0;
                                    double thanhTien = item.has("thanhtien") ? item.get("thanhtien").asDouble() : (sl * dongia);
                                    details.add(new ViewReturnDetailRow(malo, sl, dongia, thanhTien));
                                }
                                break; 
                            }
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
        modalViewReturn.setVisible(true);
    }

    // ===============================================
    // CÁC HÀM XỬ LÝ LẬP PHIẾU
    // ===============================================

    @FXML
    void onBtnShowReturnNCC(ActionEvent event) {
        ImportReceipt selected = tableImportReceipts.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        temporaryReturnList.clear();
        txtLyDoReturn.clear();
        txtSoLuongReturn.clear();
        txtMaPhieuNhapReturn.setText(selected.getMapn().trim());

        cbMaLoReturn.getItems().clear();
        ApiService.get("/api/warehouse/import-receipts/" + selected.getMapn().trim() + "/details").thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        JsonNode dataNode = ApiService.mapper.readTree(response.body()).get("data");
                        ObservableList<String> list = FXCollections.observableArrayList();
                        for (JsonNode node : dataNode) {
                            String malo = node.has("malo") ? node.get("malo").asText() : "";
                            String sl = node.has("sl") ? node.get("sl").asText() : "0";
                            list.add(malo + " (Đã nhập: " + sl + ")");
                        }
                        cbMaLoReturn.setItems(list);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        });
        modalReturnNCC.setVisible(true); 
    }

    @FXML
    void onBtnAddReturnItem(ActionEvent event) {
        String rawMalo = cbMaLoReturn.getValue(); 
        String slStr = txtSoLuongReturn.getText().trim();

        if (rawMalo == null || rawMalo.isEmpty() || slStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng chọn Mã lô và nhập Số lượng trả!"); return;
        }
        try {
            int sl = Integer.parseInt(slStr);
            if (sl <= 0) {
                showAlert(Alert.AlertType.WARNING, "Sai số lượng", "Số lượng hàng trả bắt buộc phải lớn hơn 0!"); return;
            }
            String malo = rawMalo.split(" ")[0].trim();
            temporaryReturnList.add(new ReturnItemRow(malo, sl));
            cbMaLoReturn.getSelectionModel().clearSelection(); 
            txtSoLuongReturn.clear();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Sai định dạng", "Trường số lượng trả chỉ được nhập số nguyên!");
        }
    }

    @FXML
    void onBtnSubmitReturnNCC(ActionEvent event) {
        String mapn = txtMaPhieuNhapReturn.getText().trim();
        String lydo = txtLyDoReturn.getText().trim();

        if (lydo.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền Lý do xuất trả hàng!"); return;
        }
        if (temporaryReturnList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Danh sách trống", "Vui lòng thêm ít nhất một Mã lô vào bảng danh sách cần trả!"); return;
        }

        try {
            ObjectNode rootJson = ApiService.mapper.createObjectNode();
            String manv = Session.getCurrentUser() != null ? Session.getCurrentUser().getManv() : "NV260001";
            
            rootJson.put("mapn", mapn);
            rootJson.put("manv", manv);
            rootJson.put("lydotra", lydo);

            ArrayNode itemsArray = ApiService.mapper.createArrayNode();
            for (ReturnItemRow row : temporaryReturnList) {
                ObjectNode itemNode = ApiService.mapper.createObjectNode();
                itemNode.put("malo", row.getMalo());
                itemNode.put("sl", row.getSl());
                itemsArray.add(itemNode);
            }
            rootJson.set("items", itemsArray);

            ApiService.post("/api/warehouse/supplier-returns", rootJson.toString()).thenAccept(response -> {
                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã tạo phiếu xuất trả nhà cung cấp và cập nhật số lượng tồn lô hàng!");
                        hideAllModals();
                        loadReturnHistoryForReceipt(mapn); 
                    } else {
                        try {
                            JsonNode errNode = ApiService.mapper.readTree(response.body());
                            String errMsg = errNode.has("message") ? errNode.get("message").asText() : "Lưu phiếu thất bại.";
                            showAlert(Alert.AlertType.ERROR, "Lỗi Nghiệp Vụ", errMsg);
                        } catch (Exception ex) {
                            showAlert(Alert.AlertType.ERROR, "Lỗi Hệ Thống", "Mã phản hồi từ máy chủ: " + response.statusCode());
                        }
                    }
                });
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void onBtnHideModals(ActionEvent event) { hideAllModals(); }

    private void hideAllModals() {
        if (modalReturnNCC != null) modalReturnNCC.setVisible(false);
        if (modalViewImport != null) modalViewImport.setVisible(false);
        if (modalViewReturn != null) modalViewReturn.setVisible(false);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ===============================================
    // LỚP DỮ LIỆU TẠM THỜI CHO MODAL XEM CHI TIẾT 
    // ===============================================
    public static class ViewImportDetailRow {
        private String malo; private int sl; private double gianhap; private double thanhtien;
        public ViewImportDetailRow(String malo, int sl, double gianhap, double thanhtien) {
            this.malo = malo; this.sl = sl; this.gianhap = gianhap; this.thanhtien = thanhtien;
        }
        public String getMalo() { return malo; }
        public int getSl() { return sl; }
        public double getGianhap() { return gianhap; }
        public double getThanhtien() { return thanhtien; }
    }

    public static class ViewReturnDetailRow {
        private String malo; private int sl; private double dongiatra; private double thanhtien;
        public ViewReturnDetailRow(String malo, int sl, double dongiatra, double thanhtien) {
            this.malo = malo; this.sl = sl; this.dongiatra = dongiatra; this.thanhtien = thanhtien;
        }
        public String getMalo() { return malo; }
        public int getSl() { return sl; }
        public double getDongiatra() { return dongiatra; }
        public double getThanhtien() { return thanhtien; }
    }
}