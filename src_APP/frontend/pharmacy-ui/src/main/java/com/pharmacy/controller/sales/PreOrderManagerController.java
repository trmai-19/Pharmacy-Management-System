package com.pharmacy.controller.sales;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmacy.model.InvoiceListRow;
import com.pharmacy.model.ReturnDetailRow;
import com.pharmacy.util.ApiService;
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

public class PreOrderManagerController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbStatusFilter;
    @FXML private TableView<InvoiceListRow> tableOrders;
    @FXML private TableColumn<InvoiceListRow, String> colOrderId, colOrderDate, colCustomerName, colCustomerSdt, colStatus, colPayment, colTotal;
    @FXML private TableColumn<InvoiceListRow, Integer> colPoints;

    @FXML private Label lblDetailTitle;
    @FXML private TableView<ReturnDetailRow> tableDetails;
    @FXML private TableColumn<ReturnDetailRow, String> colDetProduct, colDetMalo, colDetPrice, colDetTotal;
    @FXML private TableColumn<ReturnDetailRow, Integer> colDetSl;

    @FXML private Button btnApprove;
    @FXML private Button btnCancel;

    // --- Các thành phần UI của Biên Lai Thanh Toán ---
    @FXML private StackPane summaryModalOverlay;
    @FXML private Label lblSumInvoiceId, lblSumStaffId, lblSumCustomerId, lblSumPhone;
    @FXML private Label lblSumDate, lblSumTotalPoints, lblSumPointsUsed, lblSumSubtotal, lblSumFinalAmount;
    @FXML private ListView<String> listSumProducts;

    // --- Các thành phần UI của Custom Confirm Modal ---
    @FXML private StackPane confirmModalOverlay;
    @FXML private Label lblConfirmTitle;
    @FXML private Label lblConfirmMessage;
    @FXML private Button btnConfirmAction;
    private Runnable pendingConfirmAction;

    private ObservableList<InvoiceListRow> orderList = FXCollections.observableArrayList();
    private ObservableList<ReturnDetailRow> detailList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Cấu hình bảng danh sách đơn đặt trước
        colOrderId.setCellValueFactory(cellData -> cellData.getValue().mahdProperty());
        colOrderDate.setCellValueFactory(cellData -> cellData.getValue().ngaybanProperty());
        colCustomerName.setCellValueFactory(cellData -> cellData.getValue().tenkhProperty());
        colCustomerSdt.setCellValueFactory(cellData -> cellData.getValue().sdtProperty());
        colTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedTongTien()));
        colPoints.setCellValueFactory(cellData -> cellData.getValue().diemsudungProperty().asObject());
        colPayment.setCellValueFactory(cellData -> {
            double payment = cellData.getValue().getTongtien() - cellData.getValue().getDiemsudung();
            return new SimpleStringProperty(new DecimalFormat("#,### đ").format(payment));
        });
        colStatus.setCellValueFactory(cellData -> {
            String status = cellData.getValue().getTrangthai();
            switch (status.toUpperCase()) {
                case "DAT_TRUOC": return new SimpleStringProperty("Chờ duyệt");
                case "DANG_XU_LY": return new SimpleStringProperty("Đang giao");
                case "HOANTAT": return new SimpleStringProperty("Hoàn tất");
                case "HUY": return new SimpleStringProperty("Đã hủy");
                default: return cellData.getValue().trangthaiProperty();
            }
        });

        tableOrders.setItems(orderList);

        // Lắng nghe sự kiện chọn dòng trong bảng đơn hàng
        tableOrders.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblDetailTitle.setText("CHI TIẾT SẢN PHẨM CỦA ĐƠN HÀNG: " + newVal.getMahd());
                fetchOrderDetails(newVal.getMahd());
                
                boolean isPending = "DAT_TRUOC".equalsIgnoreCase(newVal.getTrangthai()) 
                        || "DANG_XU_LY".equalsIgnoreCase(newVal.getTrangthai());
                btnApprove.setDisable(!isPending);
                btnCancel.setDisable(!isPending);
            } else {
                lblDetailTitle.setText("CHI TIẾT SẢN PHẨM CỦA ĐƠN HÀNG: (Chưa chọn)");
                detailList.clear();
                btnApprove.setDisable(true);
                btnCancel.setDisable(true);
            }
        });

        // 2. Cấu hình bảng chi tiết đơn hàng
        colDetProduct.setCellValueFactory(cellData -> cellData.getValue().tensanphamProperty());
        colDetMalo.setCellValueFactory(cellData -> cellData.getValue().maloProperty());
        colDetSl.setCellValueFactory(cellData -> cellData.getValue().slProperty().asObject());
        colDetPrice.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedDongia()));
        colDetTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedThanhtien()));
        tableDetails.setItems(detailList);

        // 3. Cấu hình ComboBox lọc trạng thái
        cbStatusFilter.setItems(FXCollections.observableArrayList(
            "Chờ duyệt (Đặt trước)",
            "Hoàn tất",
            "Đã hủy",
            "Tất cả"
        ));
        cbStatusFilter.getSelectionModel().selectFirst(); 
        cbStatusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> fetchOrders());

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                fetchOrders();
            }
        });

        // Ẩn các modal khi khởi động
        if (summaryModalOverlay != null) summaryModalOverlay.setVisible(false);
        if (confirmModalOverlay != null) confirmModalOverlay.setVisible(false);

        fetchOrders();
    }

    @FXML
    void fetchOrders() {
        String search = txtSearch.getText().trim();
        String url = "/api/sales/invoices" + (search.isEmpty() ? "" : "?search=" + URLEncoder.encode(search, StandardCharsets.UTF_8));

        ApiService.get(url).thenAccept(res -> {
            Platform.runLater(() -> {
                try {
                    orderList.clear();
                    if (res.statusCode() == 200) {
                        JsonNode arr = ApiService.mapper.readTree(res.body()).path("data");
                        String filter = cbStatusFilter.getValue();
                        
                        for (JsonNode n : arr) {
                            String status = n.has("trangthai") ? n.get("trangthai").asText() : "HOANTAT";
                            
                            boolean match = false;
                            if ("Tất cả".equals(filter)) {
                                match = true;
                            } else if ("Chờ duyệt (Đặt trước)".equals(filter) && "DAT_TRUOC".equalsIgnoreCase(status)) {
                                match = true;
                            } else if ("Hoàn tất".equals(filter) && "HOANTAT".equalsIgnoreCase(status)) {
                                match = true;
                            } else if ("Đã hủy".equals(filter) && "HUY".equalsIgnoreCase(status)) {
                                match = true;
                            }

                            if (match) {
                                String ten = n.has("tenkh") && !n.get("tenkh").isNull() ? n.get("tenkh").asText() : "Khách lẻ";
                                String sdt = n.has("sdt") && !n.get("sdt").isNull() ? n.get("sdt").asText() : "";
                                String ngay = n.has("ngayban") && !n.get("ngayban").isNull() 
                                        ? n.get("ngayban").asText().replace("T", " ")
                                        : "";
                                if (ngay.length() > 19) ngay = ngay.substring(0, 19);

                                orderList.add(new InvoiceListRow(
                                    n.path("mahd").asText(),
                                    ngay, ten, sdt,
                                    n.path("tongtien").asDouble(0.0),
                                    status,
                                    n.path("diemsudung").asInt(0)
                                ));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void fetchOrderDetails(String mahd) {
        detailList.clear();
        ApiService.get("/api/sales/invoices/" + mahd).thenAccept(res -> {
            Platform.runLater(() -> {
                try {
                    if (res.statusCode() == 200) {
                        JsonNode items = ApiService.mapper.readTree(res.body()).path("data").path("items");
                        for (JsonNode item : items) {
                            String ten = item.has("tensanpham") && !item.get("tensanpham").isNull() 
                                    ? item.get("tensanpham").asText() 
                                    : item.path("masp").asText();
                            
                            detailList.add(new ReturnDetailRow(
                                ten, 
                                item.path("malo").asText(), 
                                item.path("sl").asInt(), 
                                item.path("dongia").asDouble(), 
                                item.path("thanhtien").asDouble()
                            ));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    // --- HÀM XỬ LÝ MODAL XÁC NHẬN ---
    private void showCustomConfirm(String title, String message, String btnColor, Runnable action) {
        lblConfirmTitle.setText(title);
        lblConfirmMessage.setText(message);
        btnConfirmAction.setStyle("-fx-background-color: " + btnColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        this.pendingConfirmAction = action;
        confirmModalOverlay.setVisible(true);
    }

    @FXML void handleCloseConfirmModal() {
        confirmModalOverlay.setVisible(false);
        pendingConfirmAction = null;
    }

    @FXML void executeConfirmAction() {
        if (pendingConfirmAction != null) {
            pendingConfirmAction.run();
        }
        confirmModalOverlay.setVisible(false);
    }

    // --- XỬ LÝ NÚT DUYỆT VÀ HỦY ---
    @FXML
    void handleApproveOrder() {
        InvoiceListRow selected = tableOrders.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        showCustomConfirm(
            "Duyệt đơn đặt trước",
            "Bạn có chắc chắn muốn duyệt và hoàn tất hóa đơn " + selected.getMahd() + " không?\nHành động này xác nhận khách đã nhận thuốc và thanh toán.",
            "#10b981", // Màu xanh lá
            () -> { 
                String url = "/api/sales/invoices/" + selected.getMahd() + "/status?status=HOANTAT";
                ApiService.put(url, "").thenAccept(res -> {
                    Platform.runLater(() -> {
                        if (res.statusCode() == 200) {
                            showReceiptModal(selected);
                            fetchOrders();
                        } else {
                            showAlert("Lỗi", "Không thể cập nhật trạng thái đơn hàng!");
                        }
                    });
                });
            }
        );
    }

    @FXML
    void handleCancelOrder() {
        InvoiceListRow selected = tableOrders.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        showCustomConfirm(
            "Hủy đơn đặt trước",
            "Bạn có chắc chắn muốn hủy đơn hàng " + selected.getMahd() + " không?\nHành động này sẽ giải phóng kho và hoàn trả thuốc.",
            "#ef4444", // Màu đỏ
            () -> { 
                String url = "/api/sales/invoices/" + selected.getMahd() + "/status?status=HUY";
                ApiService.put(url, "").thenAccept(res -> {
                    Platform.runLater(() -> {
                        if (res.statusCode() == 200) {
                            showAlert("Thành công", "Đã hủy đơn hàng và hoàn kho thành công!");
                            fetchOrders();
                        } else {
                            showAlert("Lỗi", "Không thể hủy đơn hàng!");
                        }
                    });
                });
            }
        );
    }

    // --- HÀM ĐỔ DỮ LIỆU BIÊN LAI ---
    private void showReceiptModal(InvoiceListRow selected) {
        lblSumInvoiceId.setText(selected.getMahd());
        lblSumStaffId.setText("NV001"); 
        
        String phone = selected.getSdt();
        lblSumPhone.setText(phone == null || phone.trim().isEmpty() ? "Không có" : phone);
        lblSumDate.setText(selected.getNgayban());
        
        DecimalFormat formatter = new DecimalFormat("#,### đ");
        int pointsUsed = selected.getDiemsudung();
        double totalAmount = selected.getTongtien();
        double payment = totalAmount - pointsUsed;

        lblSumPointsUsed.setText("- " + formatter.format(pointsUsed));
        lblSumSubtotal.setText(formatter.format(totalAmount));
        lblSumFinalAmount.setText(formatter.format(payment));

        listSumProducts.getItems().clear();
        for (ReturnDetailRow item : detailList) {
            String productDetail = String.format("%d x %s (%s) - %s", 
                item.getSl(), item.getTensanpham(), item.getMalo(), item.getFormattedThanhtien());
            listSumProducts.getItems().add(productDetail);
        }

        if (phone != null && !phone.trim().isEmpty()) {
            ApiService.get("/api/sales/customers?sdt=" + phone).thenAccept(res -> {
                Platform.runLater(() -> {
                    try {
                        if (res.statusCode() == 200) {
                            JsonNode root = ApiService.mapper.readTree(res.body());
                            if (root.has("data") && !root.get("data").isNull()) {
                                JsonNode data = root.get("data");
                                String makh = data.has("makh") ? data.get("makh").asText() : "Khách lẻ";
                                lblSumCustomerId.setText(makh);
                                
                                int totalPoints = data.has("diemtichluy") ? data.get("diemtichluy").asInt() : 0;
                                lblSumTotalPoints.setText(formatter.format(totalPoints));
                            } else {
                                lblSumCustomerId.setText("Khách lẻ");
                                lblSumTotalPoints.setText("0");
                            }
                        } else {
                            lblSumCustomerId.setText("Khách lẻ");
                            lblSumTotalPoints.setText("0");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        lblSumTotalPoints.setText("0");
                    }
                    summaryModalOverlay.setVisible(true);
                });
            });
        } else {
            lblSumCustomerId.setText("Khách lẻ");
            lblSumTotalPoints.setText("0");
            summaryModalOverlay.setVisible(true);
        }
    }

    @FXML 
    void handleCloseSummaryModal(ActionEvent event) { 
        summaryModalOverlay.setVisible(false); 
    }

    @FXML
    void handlePrintInvoice(ActionEvent event) {
        showAlert("Đang in...", "Hệ thống đang kết nối máy in để in hóa đơn: " + lblSumInvoiceId.getText());
        summaryModalOverlay.setVisible(false);
    }

    private void showAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}