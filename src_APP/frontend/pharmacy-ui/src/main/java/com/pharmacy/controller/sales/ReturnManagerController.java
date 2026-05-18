package com.pharmacy.controller.sales;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pharmacy.model.InvoiceListRow;
import com.pharmacy.model.InvoiceProductOption;
import com.pharmacy.model.ReturnCartItem;
import com.pharmacy.model.ReturnDetailRow;
import com.pharmacy.model.ReturnReceiptRow;
import com.pharmacy.util.ApiService;
import com.pharmacy.util.Session;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class ReturnManagerController {

    // --- CHÍNH: BẢNG HÓA ĐƠN ---
    @FXML private TextField txtSearchInvoice;
    @FXML private Button btnCreateReturn;
    @FXML private TableView<InvoiceListRow> tableInvoices;
    @FXML private TableColumn<InvoiceListRow, String> colInvId, colInvDate, colInvCustomer, colInvStatus, colInvTotal;
    @FXML private TableColumn<InvoiceListRow, Integer> colInvPoints;
    @FXML private TableColumn<InvoiceListRow, Void> colInvAction;

    // --- CHÍNH: BẢNG PHIẾU TRẢ ---
    @FXML private Label lblReturnHistoryTitle;
    @FXML private TableView<ReturnReceiptRow> tableReturns;
    @FXML private TableColumn<ReturnReceiptRow, String> colRetId, colRetDate, colRetReason, colRetTotal;
    @FXML private TableColumn<ReturnReceiptRow, Integer> colRetPoints;
    @FXML private TableColumn<ReturnReceiptRow, Void> colRetAction;

    // --- MODAL 1: CHI TIẾT HÓA ĐƠN ---
    @FXML private StackPane modalInvoiceDetail;
    @FXML private Label lblInvoiceDetailTitle;
    @FXML private TableView<ReturnDetailRow> tableInvoiceDetails;
    @FXML private TableColumn<ReturnDetailRow, String> colInvDetTen, colInvDetMalo, colInvDetGia, colInvDetTong;
    @FXML private TableColumn<ReturnDetailRow, Integer> colInvDetSl;

    // --- MODAL 2: CHI TIẾT PHIẾU TRẢ ---
    @FXML private StackPane modalReturnDetail;
    @FXML private Label lblReturnDetailTitle;
    @FXML private TableView<ReturnDetailRow> tableReturnDetails;
    @FXML private TableColumn<ReturnDetailRow, String> colRetDetTen, colRetDetMalo, colRetDetGia, colRetDetTong;
    @FXML private TableColumn<ReturnDetailRow, Integer> colRetDetSl;

    // --- MODAL 3: TẠO PHIẾU TRẢ ---
    @FXML private StackPane modalCreateReturn;
    @FXML private TextField txtCreateMahd, txtReturnQty, txtTotalRefund, txtReturnReason;
    @FXML private ComboBox<InvoiceProductOption> cbInvoiceItems;
    @FXML private TableView<ReturnCartItem> tableReturnCart;
    @FXML private TableColumn<ReturnCartItem, String> colCartTen, colCartMalo, colCartGia, colCartTong;
    @FXML private TableColumn<ReturnCartItem, Integer> colCartSl;
    @FXML private TableColumn<ReturnCartItem, Void> colCartAction; // <-- CỘT NÚT XÓA MỚI THÊM

    // Các danh sách ObservableList nạp vào TableView
    private ObservableList<InvoiceListRow> invoiceList = FXCollections.observableArrayList();
    private ObservableList<ReturnReceiptRow> returnList = FXCollections.observableArrayList();
    private ObservableList<ReturnDetailRow> invoiceDetailList = FXCollections.observableArrayList();
    private ObservableList<ReturnDetailRow> returnDetailList = FXCollections.observableArrayList();
    private ObservableList<ReturnCartItem> cartList = FXCollections.observableArrayList();
    private ObservableList<InvoiceProductOption> cbProductList = FXCollections.observableArrayList();

    private String selectedMakh = ""; 

    @FXML
    public void initialize() {
        // ================= 1. CẤU HÌNH BẢNG HÓA ĐƠN (MẸ) =================
        colInvId.setCellValueFactory(cellData -> cellData.getValue().mahdProperty());
        colInvDate.setCellValueFactory(cellData -> cellData.getValue().ngaybanProperty());
        colInvCustomer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenkh() + " - " + cellData.getValue().getSdt()));
        colInvStatus.setCellValueFactory(cellData -> cellData.getValue().trangthaiProperty());
        colInvTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedTongTien()));
        colInvPoints.setCellValueFactory(cellData -> cellData.getValue().diemsudungProperty().asObject());
        
        colInvAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("👁");
            {
                btn.setStyle("-fx-background-color: #0f766e; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                btn.setOnAction(e -> showInvoiceDetailModal(getTableRow().getItem().getMahd()));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        tableInvoices.setItems(invoiceList);

        tableInvoices.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                btnCreateReturn.setDisable(false);
                lblReturnHistoryTitle.setText("LỊCH SỬ TRẢ HÀNG CỦA HÓA ĐƠN: " + newVal.getMahd());
                fetchReturnsForInvoice(newVal.getMahd());
            } else {
                btnCreateReturn.setDisable(true);
                lblReturnHistoryTitle.setText("LỊCH SỬ TRẢ HÀNG CỦA HÓA ĐƠN: (Chưa chọn)");
                returnList.clear();
            }
        });

        // ================= 2. CẤU HÌNH BẢNG PHIẾU TRẢ (CON) =================
        colRetId.setCellValueFactory(cellData -> cellData.getValue().maptKhProperty());
        colRetDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNgaytra().replace("T", " ").substring(0, 16)));
        colRetReason.setCellValueFactory(cellData -> cellData.getValue().lydotraProperty());
        colRetTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedTongTien()));
        colRetPoints.setCellValueFactory(cellData -> cellData.getValue().diemhoanProperty().asObject());

        colRetAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("👁");
            {
                btn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                btn.setOnAction(e -> showReturnDetailModal(getTableRow().getItem().getMaptKh()));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        tableReturns.setItems(returnList);

        // ================= 3. CẤU HÌNH 2 BẢNG CHI TIẾT MODAL =================
        colInvDetTen.setCellValueFactory(cellData -> cellData.getValue().tensanphamProperty());
        colInvDetMalo.setCellValueFactory(cellData -> cellData.getValue().maloProperty());
        colInvDetSl.setCellValueFactory(cellData -> cellData.getValue().slProperty().asObject());
        colInvDetGia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedDongia()));
        colInvDetTong.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedThanhtien()));
        tableInvoiceDetails.setItems(invoiceDetailList);

        colRetDetTen.setCellValueFactory(cellData -> cellData.getValue().tensanphamProperty());
        colRetDetMalo.setCellValueFactory(cellData -> cellData.getValue().maloProperty());
        colRetDetSl.setCellValueFactory(cellData -> cellData.getValue().slProperty().asObject());
        colRetDetGia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedDongia()));
        colRetDetTong.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedThanhtien()));
        tableReturnDetails.setItems(returnDetailList);

        // ================= 4. CẤU HÌNH BẢNG GIỎ HÀNG TRONG MODAL LẬP PHIẾU =================
        colCartTen.setCellValueFactory(cellData -> cellData.getValue().tensanphamProperty());
        colCartMalo.setCellValueFactory(cellData -> cellData.getValue().maloProperty());
        colCartSl.setCellValueFactory(cellData -> cellData.getValue().slProperty().asObject());
        colCartGia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedDongia()));
        colCartTong.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedThanhtien()));
        
        // --- LOGIC NÚT XÓA SẢN PHẨM KHỎI GIỎ ---
        colCartAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("✖");
            {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc2626; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 14px;");
                btn.setOnAction(e -> {
                    ReturnCartItem item = getTableRow().getItem();
                    if (item != null) {
                        cartList.remove(item);
                        updateTotalRefundUI(); // Tự động cập nhật lại tổng tiền
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        
        tableReturnCart.setItems(cartList);

        // Tự động reset bảng khi xóa chữ trong ô search
        txtSearchInvoice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                fetchInvoices();
            }
        });

        fetchInvoices();
    }

    // ==========================================
    // CÁC HÀM LIÊN KẾT HỆ THỐNG QUA API ENDPOINTS
    // ==========================================

    @FXML
    void fetchInvoices() {
        String search = txtSearchInvoice.getText().trim();
        String url = "/api/sales/invoices" + (search.isEmpty() ? "" : "?search=" + URLEncoder.encode(search, StandardCharsets.UTF_8));
        
        ApiService.get(url).thenAccept(res -> {
            Platform.runLater(() -> {
                try {
                    invoiceList.clear();
                    if (res.statusCode() == 200) {
                        JsonNode arr = ApiService.mapper.readTree(res.body()).path("data");
                        for (JsonNode n : arr) {
                            String ten = n.has("tenkh") && !n.get("tenkh").isNull() ? n.get("tenkh").asText() : "Khách lẻ";
                            String sdt = n.has("sdt") && !n.get("sdt").isNull() ? n.get("sdt").asText() : "";
                            
                            invoiceList.add(new InvoiceListRow(
                                n.path("mahd").asText(),
                                n.has("ngayban") && !n.get("ngayban").isNull() ? n.get("ngayban").asText().replace("T", " ").substring(0, 16) : "",
                                ten, sdt,
                                n.path("tongtien").asDouble(0.0),
                                n.has("trangthai") ? n.get("trangthai").asText() : "HOANTAT",
                                n.path("diemsudung").asInt(0) 
                            ));
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
            });
        });
    }

    private void fetchReturnsForInvoice(String mahd) {
        String url = "/api/sales/return-receipts?search=" + URLEncoder.encode(mahd, StandardCharsets.UTF_8);
        ApiService.get(url).thenAccept(res -> {
            Platform.runLater(() -> {
                try {
                    returnList.clear();
                    if (res.statusCode() == 200) {
                        JsonNode arr = ApiService.mapper.readTree(res.body()).path("data");
                        for (JsonNode n : arr) {
                            if (n.path("mahd").asText().equals(mahd)) {
                                returnList.add(new ReturnReceiptRow(
                                    n.path("maptKh").asText(),
                                    n.has("ngaytra") ? n.path("ngaytra").asText() : "",
                                    n.path("mahd").asText(),
                                    n.path("tenkh").asText(),
                                    n.path("sanPhamTomTat").asText(),
                                    n.path("tongSl").asInt(),
                                    n.path("lydotra").asText(),
                                    n.path("tongtienhoan").asDouble(),
                                    n.path("diemhoan").asInt(0) 
                                ));
                            }
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
            });
        });
    }

    private void showInvoiceDetailModal(String mahd) {
        lblInvoiceDetailTitle.setText("Chi tiết Hóa Đơn: " + mahd);
        invoiceDetailList.clear();
        ApiService.get("/api/sales/invoices/" + mahd).thenAccept(res -> {
            Platform.runLater(() -> {
                try {
                    if (res.statusCode() == 200) {
                        JsonNode items = ApiService.mapper.readTree(res.body()).path("data").path("items");
                        for (JsonNode item : items) {
                            String ten = item.has("tensanpham") && !item.get("tensanpham").isNull() ? item.get("tensanpham").asText() : item.path("masp").asText();
                            invoiceDetailList.add(new ReturnDetailRow(
                                ten, item.path("malo").asText(), item.path("sl").asInt(), 
                                item.path("dongia").asDouble(), item.path("thanhtien").asDouble()
                            ));
                        }
                        modalInvoiceDetail.setVisible(true);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            });
        });
    }

    private void showReturnDetailModal(String maptKh) {
        lblReturnDetailTitle.setText("Chi tiết Phiếu Trả: " + maptKh);
        returnDetailList.clear();
        ApiService.get("/api/sales/return-receipts/" + maptKh).thenAccept(res -> {
            Platform.runLater(() -> {
                try {
                    if (res.statusCode() == 200) {
                        JsonNode items = ApiService.mapper.readTree(res.body()).path("data").path("items");
                        for (JsonNode item : items) {
                            String ten = item.has("tensanpham") && !item.get("tensanpham").isNull() ? item.get("tensanpham").asText() : item.path("masp").asText();
                            returnDetailList.add(new ReturnDetailRow(
                                ten, item.path("malo").asText(), item.path("sl").asInt(), 
                                item.path("dongiahoan").asDouble(), item.path("thanhtien").asDouble()
                            ));
                        }
                        modalReturnDetail.setVisible(true);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            });
        });
    }

    @FXML
    void handleShowCreateModal() {
        InvoiceListRow selectedInv = tableInvoices.getSelectionModel().getSelectedItem();
        if (selectedInv == null) return;
        
        txtCreateMahd.setText(selectedInv.getMahd());
        cbProductList.clear();
        cartList.clear();
        updateTotalRefundUI();
        cbInvoiceItems.setItems(cbProductList);

        ApiService.get("/api/sales/invoices/" + selectedInv.getMahd()).thenAccept(res -> {
            Platform.runLater(() -> {
                try {
                    if (res.statusCode() == 200) {
                        JsonNode data = ApiService.mapper.readTree(res.body()).path("data");
                        selectedMakh = data.has("makh") ? data.get("makh").asText() : "KHACH_LE";
                        double tongtien = data.path("tongtien").asDouble(0.0);
                        double tienthanhtoan = data.path("tienthanhtoan").asDouble(0.0);
                        double ratio = tongtien > 0 ? (tienthanhtoan / tongtien) : 1.0;

                        JsonNode items = data.path("items");
                        for (JsonNode item : items) {
                            String ten = item.has("tensanpham") && !item.get("tensanpham").isNull() ? item.get("tensanpham").asText() : item.path("masp").asText();
                            cbProductList.add(new InvoiceProductOption(
                                item.path("masp").asText(), ten, item.path("malo").asText(),
                                item.path("sl").asInt(), item.path("dongia").asDouble() * ratio
                            ));
                        }
                        if (!cbProductList.isEmpty()) cbInvoiceItems.getSelectionModel().selectFirst();
                        modalCreateReturn.setVisible(true);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            });
        });
    }

    @FXML void handleAddReturnItem() {
        InvoiceProductOption selected = cbInvoiceItems.getValue();
        if (selected == null) return;
        try {
            int slTra = Integer.parseInt(txtReturnQty.getText().trim());
            if (slTra <= 0 || slTra > selected.getSlMua()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi số lượng", "Số lượng phải > 0 và <= SL đã mua (" + selected.getSlMua() + ")");
                return;
            }
            boolean exists = false;
            for (ReturnCartItem c : cartList) {
                if (c.getMalo().equals(selected.getMalo())) {
                    showAlert(Alert.AlertType.WARNING, "Lỗi", "Lô hàng này đã có trong danh sách trả!");
                    exists = true; break;
                }
            }
            if (!exists) {
                cartList.add(new ReturnCartItem(selected.getMasp(), selected.getTensanpham(), selected.getMalo(), slTra, selected.getDongiaHoan()));
                txtReturnQty.clear();
                updateTotalRefundUI();
            }
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập số lượng hợp lệ!"); }
    }

    private void updateTotalRefundUI() {
        double total = cartList.stream().mapToDouble(ReturnCartItem::getThanhtien).sum();
        txtTotalRefund.setText(new DecimalFormat("#,### đ").format(total));
    }

    @FXML void handleSubmitReturn() {
        String mahd = txtCreateMahd.getText().trim();
        String reason = txtReturnReason.getText().trim();

        if (reason.isEmpty() || cartList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập Lý do và chọn ít nhất 1 sản phẩm!");
            return;
        }

        try {
            ObjectNode payload = ApiService.mapper.createObjectNode();
            payload.put("mahd", mahd);
            payload.put("makh", selectedMakh);
            payload.put("manv", Session.getCurrentUser() != null ? Session.getCurrentUser().getManv() : "NV001");
            payload.put("lydotra", reason);

            ArrayNode itemsArr = ApiService.mapper.createArrayNode();
            for (ReturnCartItem item : cartList) {
                ObjectNode node = ApiService.mapper.createObjectNode();
                node.put("malo", item.getMalo());
                node.put("sl", item.getSl());
                itemsArr.add(node);
            }
            payload.set("items", itemsArr);

            ApiService.post("/api/sales/return-receipts", payload.toString()).thenAccept(res -> {
                Platform.runLater(() -> {
                    try {
                        if (res.statusCode() == 200 || res.statusCode() == 201) {
                            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã tạo phiếu hoàn trả tiền thành công!");
                            handleCloseCreateModal();
                            fetchReturnsForInvoice(mahd); 
                        } else {
                            JsonNode err = ApiService.mapper.readTree(res.body());
                            showAlert(Alert.AlertType.ERROR, "Thất bại", err.has("message") ? err.get("message").asText() : "Lỗi hệ thống");
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void handleCloseInvoiceDetailModal() { modalInvoiceDetail.setVisible(false); }
    @FXML void handleCloseReturnDetailModal() { modalReturnDetail.setVisible(false); }
    @FXML void handleCloseCreateModal() { 
        modalCreateReturn.setVisible(false); 
        cartList.clear(); txtReturnQty.clear(); txtReturnReason.clear(); 
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}