package com.pharmacy.controller.sales;

import com.fasterxml.jackson.databind.JsonNode;
import com.pharmacy.model.InvoiceListRow;
import com.pharmacy.model.ReturnDetailRow;
import com.pharmacy.util.ApiService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
                
                // Chỉ cho phép duyệt/hủy đối với đơn hàng đang ở trạng thái DAT_TRUOC hoặc DANG_XU_LY
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
        cbStatusFilter.getSelectionModel().selectFirst(); // Mặc định hiển thị các đơn chờ duyệt (DAT_TRUOC)
        cbStatusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> fetchOrders());

        // Tự động tìm kiếm khi xóa chữ ô tìm kiếm
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                fetchOrders();
            }
        });

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
                            
                            // Lọc trạng thái hiển thị
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
                                if (ngay.length() > 16) ngay = ngay.substring(0, 16);

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

    @FXML
    void handleApproveOrder() {
        InvoiceListRow selected = tableOrders.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Duyệt đơn đặt trước");
        confirm.setHeaderText("Xác nhận hoàn tất thanh toán?");
        confirm.setContentText("Bạn có chắc chắn muốn duyệt và hoàn tất hóa đơn " + selected.getMahd() + " không?\nHành động này xác nhận khách đã nhận thuốc và trả tiền.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String url = "/api/sales/invoices/" + selected.getMahd() + "/status?status=HOANTAT";
                ApiService.put(url, "").thenAccept(res -> {
                    Platform.runLater(() -> {
                        if (res.statusCode() == 200) {
                            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã duyệt và hoàn tất đơn hàng " + selected.getMahd() + " thành công!");
                            fetchOrders();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật trạng thái đơn hàng!");
                        }
                    });
                });
            }
        });
    }

    @FXML
    void handleCancelOrder() {
        InvoiceListRow selected = tableOrders.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hủy đơn đặt trước");
        confirm.setHeaderText("Xác nhận hủy đơn hàng?");
        confirm.setContentText("Bạn có chắc chắn muốn hủy đơn hàng " + selected.getMahd() + " không?\nHành động này sẽ giải phóng kho và hoàn trả thuốc về các lô sản phẩm tương ứng.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String url = "/api/sales/invoices/" + selected.getMahd() + "/status?status=HUY";
                ApiService.put(url, "").thenAccept(res -> {
                    Platform.runLater(() -> {
                        if (res.statusCode() == 200) {
                            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã hủy đơn hàng và hoàn kho thành công!");
                            fetchOrders();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể hủy đơn hàng!");
                        }
                    });
                });
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
