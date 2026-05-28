package com.pharmacy.controller.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmacy.model.*;
import com.pharmacy.util.ApiService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class ReturnManagerController {

    // ===================== SALES =====================
    @FXML private TextField salesTxtSearchInvoice;
    @FXML private TableView<InvoiceListRow> salesTableInvoices;
    @FXML private TableColumn<InvoiceListRow, String> salesColInvId, salesColInvDate, salesColInvCustomer, salesColInvStatus, salesColInvTotal;
    @FXML private TableColumn<InvoiceListRow, Integer> salesColInvPoints;
    @FXML private TableColumn<InvoiceListRow, Void> salesColInvAction;

    @FXML private Label salesLblReturnHistoryTitle;
    @FXML private TableView<ReturnReceiptRow> salesTableReturns;
    @FXML private TableColumn<ReturnReceiptRow, String> salesColRetId, salesColRetDate, salesColRetReason, salesColRetTotal;
    @FXML private TableColumn<ReturnReceiptRow, Integer> salesColRetPoints;
    @FXML private TableColumn<ReturnReceiptRow, Void> salesColRetAction;

    @FXML private StackPane salesModalInvoiceDetail;
    @FXML private Label salesLblInvoiceDetailTitle;
    @FXML private TableView<ReturnDetailRow> salesTableInvoiceDetails;
    @FXML private TableColumn<ReturnDetailRow, String> salesColInvDetTen, salesColInvDetMalo, salesColInvDetGia, salesColInvDetTong;
    @FXML private TableColumn<ReturnDetailRow, Integer> salesColInvDetSl;

    @FXML private StackPane salesModalReturnDetail;
    @FXML private Label salesLblReturnDetailTitle;
    @FXML private TableView<ReturnDetailRow> salesTableReturnDetails;
    @FXML private TableColumn<ReturnDetailRow, String> salesColRetDetTen, salesColRetDetMalo, salesColRetDetGia, salesColRetDetTong;
    @FXML private TableColumn<ReturnDetailRow, Integer> salesColRetDetSl;

    private ObservableList<InvoiceListRow> salesInvoiceList = FXCollections.observableArrayList();
    private ObservableList<ReturnReceiptRow> salesReturnList = FXCollections.observableArrayList();
    private ObservableList<ReturnDetailRow> salesInvoiceDetailList = FXCollections.observableArrayList();
    private ObservableList<ReturnDetailRow> salesReturnDetailList = FXCollections.observableArrayList();

    // ===================== WAREHOUSE =====================
    @FXML private TextField warehouseTxtSearchImport;
    @FXML private TableView<ImportReceipt> warehouseTableImportReceipts;
    @FXML private TableColumn<ImportReceipt, String> warehouseColImpId, warehouseColImpDate, warehouseColImpNcc, warehouseColImpStatus, warehouseColImpActionView;
    @FXML private TableColumn<ImportReceipt, Number> warehouseColImpTotal;

    @FXML private Label warehouseLblDetailReturn;
    @FXML private TableView<SupplierReturnTicket> warehouseTableReturns;
    @FXML private TableColumn<SupplierReturnTicket, String> warehouseColRetId, warehouseColRetDate, warehouseColRetReason, warehouseColRetActionView;
    @FXML private TableColumn<SupplierReturnTicket, Number> warehouseColRetTotal;

    @FXML private Pane warehouseModalViewImport;
    @FXML private Label warehouseLblViewImpId, warehouseLblViewImpDate, warehouseLblViewImpNcc, warehouseLblViewImpTotal;
    @FXML private TableView<ViewImportDetailRow> warehouseTableViewImportItems;
    @FXML private TableColumn<ViewImportDetailRow, String> warehouseColViewImpProductName, warehouseColViewImpMalo;
    @FXML private TableColumn<ViewImportDetailRow, Integer> warehouseColViewImpQty;
    @FXML private TableColumn<ViewImportDetailRow, Double> warehouseColViewImpPrice, warehouseColViewImpTotal;

    @FXML private Pane warehouseModalViewReturn;
    @FXML private Label warehouseLblViewRetId, warehouseLblViewRetOriginalId, warehouseLblViewRetDate, warehouseLblViewRetReason, warehouseLblViewRetTotal;
    @FXML private TableView<ViewReturnDetailRow> warehouseTableViewReturnItems;
    @FXML private TableColumn<ViewReturnDetailRow, String> warehouseColViewRetProductName, warehouseColViewRetMalo;
    @FXML private TableColumn<ViewReturnDetailRow, Integer> warehouseColViewRetQty;
    @FXML private TableColumn<ViewReturnDetailRow, Double> warehouseColViewRetPrice, warehouseColViewRetTotal;

    private ObservableList<ImportReceipt> warehouseImportList = FXCollections.observableArrayList();
    private ObservableList<SupplierReturnTicket> warehouseDetailReturnList = FXCollections.observableArrayList();

    // Timer debounce cho tìm kiếm
    private Timer salesSearchTimer;
    private Timer warehouseSearchTimer;

    // ===================== INIT =====================
    @FXML
    public void initialize() {
        setupSalesColumns();
        setupWarehouseColumns();

        // Load dữ liệu ban đầu
        salesFetchInvoices();
        warehouseLoadImportReceipts();

        // Sales: chọn hóa đơn -> load phiếu trả
        salesTableInvoices.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                salesLblReturnHistoryTitle.setText("📜 LỊCH SỬ TRẢ HÀNG CỦA HÓA ĐƠN: " + newVal.getMahd());
                salesFetchReturnsForInvoice(newVal.getMahd());
            } else {
                salesLblReturnHistoryTitle.setText("📜 LỊCH SỬ TRẢ HÀNG CỦA HÓA ĐƠN: (Chưa chọn)");
                salesReturnList.clear();
            }
        });

        // Warehouse: chọn phiếu nhập -> load phiếu trả NCC
        warehouseTableImportReceipts.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                warehouseLblDetailReturn.setText("📑 LỊCH SỬ TRẢ HÀNG CỦA PHIẾU: " + newVal.getMapn());
                warehouseLoadReturnHistoryForReceipt(newVal.getMapn());
            } else {
                warehouseLblDetailReturn.setText("📑 LỊCH SỬ TRẢ HÀNG CỦA PHIẾU: ...");
                warehouseDetailReturnList.clear();
            }
        });

        // === Tìm kiếm realtime với debounce 300ms ===
        salesTxtSearchInvoice.textProperty().addListener((obs, oldVal, newVal) -> {
            if (salesSearchTimer != null) salesSearchTimer.cancel();
            salesSearchTimer = new Timer();
            salesSearchTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> salesFetchInvoices());
                }
            }, 300);
        });

        warehouseTxtSearchImport.textProperty().addListener((obs, oldVal, newVal) -> {
            if (warehouseSearchTimer != null) warehouseSearchTimer.cancel();
            warehouseSearchTimer = new Timer();
            warehouseSearchTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> warehouseLoadImportReceipts());
                }
            }, 300);
        });
    }

    // ===================== SALES METHODS =====================
    private void setupSalesColumns() {
        salesColInvId.setCellValueFactory(cellData -> cellData.getValue().mahdProperty());
        salesColInvDate.setCellValueFactory(cellData -> cellData.getValue().ngaybanProperty());
        salesColInvCustomer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenkh() + " - " + cellData.getValue().getSdt()));
        salesColInvStatus.setCellValueFactory(cellData -> cellData.getValue().trangthaiProperty());
        salesColInvTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedTongTien()));
        salesColInvPoints.setCellValueFactory(cellData -> cellData.getValue().diemsudungProperty().asObject());

        salesColInvAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("👁");
            { btn.setStyle("-fx-background-color: #0f766e; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
              btn.setOnAction(e -> salesShowInvoiceDetailModal(getTableRow().getItem().getMahd())); }
            @Override protected void updateItem(Void item, boolean empty) { setGraphic(empty ? null : btn); }
        });
        salesTableInvoices.setItems(salesInvoiceList);

        salesColRetId.setCellValueFactory(cellData -> cellData.getValue().maptKhProperty());
        salesColRetDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNgaytra().replace("T", " ").substring(0, 16)));
        salesColRetReason.setCellValueFactory(cellData -> cellData.getValue().lydotraProperty());
        salesColRetTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedTongTien()));
        salesColRetPoints.setCellValueFactory(cellData -> cellData.getValue().diemhoanProperty().asObject());

        salesColRetAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("👁");
            { btn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
              btn.setOnAction(e -> salesShowReturnDetailModal(getTableRow().getItem().getMaptKh())); }
            @Override protected void updateItem(Void item, boolean empty) { setGraphic(empty ? null : btn); }
        });
        salesTableReturns.setItems(salesReturnList);

        // Chi tiết modal
        salesColInvDetTen.setCellValueFactory(cellData -> cellData.getValue().tensanphamProperty());
        salesColInvDetMalo.setCellValueFactory(cellData -> cellData.getValue().maloProperty());
        salesColInvDetSl.setCellValueFactory(cellData -> cellData.getValue().slProperty().asObject());
        salesColInvDetGia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedDongia()));
        salesColInvDetTong.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedThanhtien()));
        salesTableInvoiceDetails.setItems(salesInvoiceDetailList);

        salesColRetDetTen.setCellValueFactory(cellData -> cellData.getValue().tensanphamProperty());
        salesColRetDetMalo.setCellValueFactory(cellData -> cellData.getValue().maloProperty());
        salesColRetDetSl.setCellValueFactory(cellData -> cellData.getValue().slProperty().asObject());
        salesColRetDetGia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedDongia()));
        salesColRetDetTong.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedThanhtien()));
        salesTableReturnDetails.setItems(salesReturnDetailList);
    }

    private void salesFetchInvoices() {
        String search = salesTxtSearchInvoice.getText().trim();
        String url = "/api/sales/invoices" + (search.isEmpty() ? "" : "?search=" + URLEncoder.encode(search, StandardCharsets.UTF_8));
        ApiService.get(url).thenAccept(res -> Platform.runLater(() -> {
            try {
                salesInvoiceList.clear();
                if (res.statusCode() == 200) {
                    JsonNode arr = ApiService.mapper.readTree(res.body()).path("data");
                    for (JsonNode n : arr) {
                        String ten = n.has("tenkh") && !n.get("tenkh").isNull() ? n.get("tenkh").asText() : "Khách lẻ";
                        String sdt = n.has("sdt") && !n.get("sdt").isNull() ? n.get("sdt").asText() : "";
                        salesInvoiceList.add(new InvoiceListRow(
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
        }));
    }

    private void salesFetchReturnsForInvoice(String mahd) {
        String url = "/api/sales/return-receipts?search=" + URLEncoder.encode(mahd, StandardCharsets.UTF_8);
        ApiService.get(url).thenAccept(res -> Platform.runLater(() -> {
            try {
                salesReturnList.clear();
                if (res.statusCode() == 200) {
                    JsonNode arr = ApiService.mapper.readTree(res.body()).path("data");
                    for (JsonNode n : arr) {
                        if (n.path("mahd").asText().equals(mahd)) {
                            salesReturnList.add(new ReturnReceiptRow(
                                n.path("maptKh").asText(), n.has("ngaytra") ? n.path("ngaytra").asText() : "",
                                n.path("mahd").asText(), n.path("tenkh").asText(), n.path("sanPhamTomTat").asText(),
                                n.path("tongSl").asInt(), n.path("lydotra").asText(), n.path("tongtienhoan").asDouble(),
                                n.path("diemhoan").asInt(0)
                            ));
                        }
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }));
    }

    private void salesShowInvoiceDetailModal(String mahd) {
        salesLblInvoiceDetailTitle.setText("Chi tiết Hóa Đơn: " + mahd);
        salesInvoiceDetailList.clear();
        ApiService.get("/api/sales/invoices/" + mahd).thenAccept(res -> Platform.runLater(() -> {
            try {
                if (res.statusCode() == 200) {
                    JsonNode items = ApiService.mapper.readTree(res.body()).path("data").path("items");
                    for (JsonNode item : items) {
                        String ten = item.has("tensanpham") && !item.get("tensanpham").isNull() ? item.get("tensanpham").asText() : item.path("masp").asText();
                        salesInvoiceDetailList.add(new ReturnDetailRow(
                            ten, item.path("malo").asText(), item.path("sl").asInt(),
                            item.path("dongia").asDouble(), item.path("thanhtien").asDouble()
                        ));
                    }
                    salesModalInvoiceDetail.setVisible(true);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }));
    }

    private void salesShowReturnDetailModal(String maptKh) {
        salesLblReturnDetailTitle.setText("Chi tiết Phiếu Trả: " + maptKh);
        salesReturnDetailList.clear();
        ApiService.get("/api/sales/return-receipts/" + maptKh).thenAccept(res -> Platform.runLater(() -> {
            try {
                if (res.statusCode() == 200) {
                    JsonNode items = ApiService.mapper.readTree(res.body()).path("data").path("items");
                    for (JsonNode item : items) {
                        String ten = item.has("tensanpham") && !item.get("tensanpham").isNull() ? item.get("tensanpham").asText() : item.path("masp").asText();
                        salesReturnDetailList.add(new ReturnDetailRow(
                            ten, item.path("malo").asText(), item.path("sl").asInt(),
                            item.path("dongiahoan").asDouble(), item.path("thanhtien").asDouble()
                        ));
                    }
                    salesModalReturnDetail.setVisible(true);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }));
    }

    @FXML void handleSalesFetchInvoices() { salesFetchInvoices(); }
    @FXML void handleSalesCloseInvoiceDetailModal() { salesModalInvoiceDetail.setVisible(false); }
    @FXML void handleSalesCloseReturnDetailModal() { salesModalReturnDetail.setVisible(false); }

    // ===================== WAREHOUSE METHODS =====================
    private void setupWarehouseColumns() {
        warehouseColImpId.setCellValueFactory(new PropertyValueFactory<>("mapn"));
        warehouseColImpDate.setCellValueFactory(new PropertyValueFactory<>("ngaynhap"));
        warehouseColImpNcc.setCellValueFactory(new PropertyValueFactory<>("mancc"));
        warehouseColImpTotal.setCellValueFactory(new PropertyValueFactory<>("tongtien"));
        warehouseColImpStatus.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
        warehouseColImpTotal.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) { setText(empty || item == null ? null : String.format("%,.0f VNĐ", item.doubleValue())); }
        });
        warehouseColImpActionView.setCellValueFactory(param -> new javafx.beans.property.ReadOnlyObjectWrapper<>("Xem"));
        warehouseColImpActionView.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("👁");
            { btn.setStyle("-fx-background-color: #0f766e; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
              btn.setOnAction(e -> warehouseShowImportDetail(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(String item, boolean empty) { setGraphic(empty ? null : btn); }
        });
        warehouseTableImportReceipts.setItems(warehouseImportList);

        warehouseColRetId.setCellValueFactory(new PropertyValueFactory<>("maptNcc"));
        warehouseColRetDate.setCellValueFactory(new PropertyValueFactory<>("ngaytra"));
        warehouseColRetReason.setCellValueFactory(new PropertyValueFactory<>("lydotra"));
        warehouseColRetTotal.setCellValueFactory(new PropertyValueFactory<>("tongtien"));
        warehouseColRetTotal.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) { setText(empty || item == null ? null : String.format("%,.0f VNĐ", item.doubleValue())); }
        });
        warehouseColRetActionView.setCellValueFactory(param -> new javafx.beans.property.ReadOnlyObjectWrapper<>("Xem"));
        warehouseColRetActionView.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("👁");
            { btn.setStyle("-fx-background-color: #c2410c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
              btn.setOnAction(e -> warehouseShowReturnDetail(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(String item, boolean empty) { setGraphic(empty ? null : btn); }
        });
        warehouseTableReturns.setItems(warehouseDetailReturnList);

        // Modal xem import details
        warehouseColViewImpProductName.setCellValueFactory(new PropertyValueFactory<>("tensanpham"));
        warehouseColViewImpMalo.setCellValueFactory(new PropertyValueFactory<>("malo"));
        warehouseColViewImpQty.setCellValueFactory(new PropertyValueFactory<>("sl"));
        warehouseColViewImpPrice.setCellValueFactory(new PropertyValueFactory<>("gianhap"));
        warehouseColViewImpTotal.setCellValueFactory(new PropertyValueFactory<>("thanhtien"));
        warehouseColViewImpPrice.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) { setText(empty || item == null ? null : String.format("%,.0f VNĐ", item)); }
        });
        warehouseColViewImpTotal.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) { setText(empty || item == null ? null : String.format("%,.0f VNĐ", item)); }
        });

        // Modal xem return details
        warehouseColViewRetProductName.setCellValueFactory(new PropertyValueFactory<>("tensanpham"));
        warehouseColViewRetMalo.setCellValueFactory(new PropertyValueFactory<>("malo"));
        warehouseColViewRetQty.setCellValueFactory(new PropertyValueFactory<>("sl"));
        warehouseColViewRetPrice.setCellValueFactory(new PropertyValueFactory<>("dongiatra"));
        warehouseColViewRetTotal.setCellValueFactory(new PropertyValueFactory<>("thanhtien"));
        warehouseColViewRetPrice.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) { setText(empty || item == null ? null : String.format("%,.0f VNĐ", item)); }
        });
        warehouseColViewRetTotal.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) { setText(empty || item == null ? null : String.format("%,.0f VNĐ", item)); }
        });
    }

    private void warehouseLoadImportReceipts() {
        String search = warehouseTxtSearchImport.getText() != null ? warehouseTxtSearchImport.getText().trim() : "";
        String url = "/api/warehouse/import-receipts" + (search.isEmpty() ? "" : "?search=" + URLEncoder.encode(search, StandardCharsets.UTF_8));
        ApiService.get(url).thenAccept(res -> Platform.runLater(() -> {
            try {
                warehouseImportList.clear();
                if (res.statusCode() == 200) {
                    JsonNode dataNode = ApiService.mapper.readTree(res.body()).get("data");
                    for (JsonNode node : dataNode) {
                        warehouseImportList.add(new ImportReceipt(
                            node.path("mapn").asText(),
                            node.has("ngaynhap") && node.get("ngaynhap") != null ? node.get("ngaynhap").asText().split("T")[0] : "",
                            node.path("mancc").asText(),
                            node.path("tongtien").asDouble(),
                            node.path("trangthai").asText()
                        ));
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }));
    }

    private void warehouseLoadReturnHistoryForReceipt(String mapn) {
        warehouseDetailReturnList.clear();
        ApiService.get("/api/warehouse/supplier-returns/receipt/" + mapn).thenAccept(res -> Platform.runLater(() -> {
            try {
                if (res.statusCode() == 200) {
                    JsonNode dataNode = ApiService.mapper.readTree(res.body()).get("data");
                    for (JsonNode node : dataNode) {
                        warehouseDetailReturnList.add(new SupplierReturnTicket(
                            node.path("maptNcc").asText(),
                            node.has("ngaytra") && node.get("ngaytra") != null ? node.get("ngaytra").asText().split("T")[0] : "",
                            node.path("lydotra").asText(),
                            node.path("tongtien").asDouble()
                        ));
                    }
                    warehouseTableReturns.setItems(warehouseDetailReturnList);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }));
    }

    private void warehouseShowImportDetail(ImportReceipt receipt) {
        warehouseLblViewImpId.setText(receipt.getMapn());
        warehouseLblViewImpDate.setText(receipt.getNgaynhap());
        warehouseLblViewImpNcc.setText(receipt.getMancc());
        warehouseLblViewImpTotal.setText(String.format("%,.0f VNĐ", receipt.getTongtien()));
        ObservableList<ViewImportDetailRow> details = FXCollections.observableArrayList();
        warehouseTableViewImportItems.setItems(details);
        ApiService.get("/api/warehouse/import-receipts/" + receipt.getMapn() + "/details").thenAccept(res -> Platform.runLater(() -> {
            try {
                if (res.statusCode() == 200) {
                    JsonNode dataNode = ApiService.mapper.readTree(res.body()).get("data");
                    for (JsonNode node : dataNode) {
                        String tensanpham = node.has("tensanpham") && !node.get("tensanpham").isNull() ? node.get("tensanpham").asText() : "Chưa cập nhật";
                        String malo = node.path("malo").asText();
                        int sl = node.path("sl").asInt();
                        double gia = node.path("gianhap").asDouble();
                        details.add(new ViewImportDetailRow(tensanpham, malo, sl, gia, sl * gia));
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }));
        warehouseModalViewImport.setVisible(true);
    }

    private void warehouseShowReturnDetail(SupplierReturnTicket ticket) {
        ImportReceipt selected = warehouseTableImportReceipts.getSelectionModel().getSelectedItem();
        warehouseLblViewRetId.setText(ticket.getMaptNcc());
        warehouseLblViewRetOriginalId.setText(selected != null ? selected.getMapn() : "...");
        warehouseLblViewRetDate.setText(ticket.getNgaytra());
        warehouseLblViewRetReason.setText(ticket.getLydotra());
        warehouseLblViewRetTotal.setText(String.format("%,.0f VNĐ", ticket.getTongtien()));
        ObservableList<ViewReturnDetailRow> details = FXCollections.observableArrayList();
        warehouseTableViewReturnItems.setItems(details);
        if (selected != null) {
            ApiService.get("/api/warehouse/supplier-returns/receipt/" + selected.getMapn()).thenAccept(res -> Platform.runLater(() -> {
                try {
                    if (res.statusCode() == 200) {
                        JsonNode dataNode = ApiService.mapper.readTree(res.body()).get("data");
                        for (JsonNode node : dataNode) {
                            if (node.get("maptNcc").asText().equals(ticket.getMaptNcc())) {
                                JsonNode items = node.get("items");
                                for (JsonNode it : items) {
                                    String tensanpham = it.has("tensanpham") && !it.get("tensanpham").isNull() ? it.get("tensanpham").asText() : "Chưa cập nhật";
                                    details.add(new ViewReturnDetailRow(
                                        tensanpham,
                                        it.path("malo").asText(),
                                        it.path("sl").asInt(),
                                        it.path("dongiatra").asDouble(),
                                        it.path("thanhtien").asDouble()
                                    ));
                                }
                                break;
                            }
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }));
        }
        warehouseModalViewReturn.setVisible(true);
    }

    @FXML void handleWarehouseLoadImportReceipts() { warehouseLoadImportReceipts(); }
    @FXML void handleWarehouseHideModals() {
        if (warehouseModalViewImport != null) warehouseModalViewImport.setVisible(false);
        if (warehouseModalViewReturn != null) warehouseModalViewReturn.setVisible(false);
    }
}