package com.pharmacy.controller.sales;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pharmacy.model.CustomerListRow;
import com.pharmacy.model.InvoiceHistoryRow;
import com.pharmacy.model.InvoiceItemRow;
import com.pharmacy.util.ApiService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class CustomerManagerController {

    // Các nhãn thống kê
    @FXML private Label lblTotalCustomers, lblDiamondCustomers, lblGoldCustomers, lblSilverCustomers;

    // Thanh công cụ
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbTier;

    // Bảng và Cột Khách Hàng
    @FXML private TableView<CustomerListRow> tableCustomer;
    @FXML private TableColumn<CustomerListRow, String> colId, colName, colPhone, colDob, colTier, colPoints, colTotalSpent;
    @FXML private TableColumn<CustomerListRow, Void> colActionView;

    // Form Tạo Hồ Sơ
    @FXML private StackPane modalOverlay;
    @FXML private TextField txtNewName, txtNewPhone;
    @FXML private ComboBox<String> cbNewGender;
    @FXML private DatePicker dpNewDOB;

    // Form Lịch Sử Hóa Đơn (Bảng Trên)
    @FXML private StackPane modalInvoiceHistory;
    @FXML private Label lblHistoryCustomerName;
    @FXML private TableView<InvoiceHistoryRow> tableInvoiceHistory;
    @FXML private TableColumn<InvoiceHistoryRow, String> colInvId, colInvDate, colInvStatus, colInvTotal, colInvAmount;
    @FXML private TableColumn<InvoiceHistoryRow, Integer> colInvPoints;
    
    // Form Chi Tiết Hóa Đơn (Bảng Dưới)
    @FXML private TableView<InvoiceItemRow> tableInvoiceDetails;
    @FXML private TableColumn<InvoiceItemRow, String> colDetailMasp, colDetailMalo, colDetailPrice, colDetailTotal;
    @FXML private TableColumn<InvoiceItemRow, Integer> colDetailSl;

    private ObservableList<CustomerListRow> customerList = FXCollections.observableArrayList();
    private ObservableList<InvoiceHistoryRow> invoiceHistoryList = FXCollections.observableArrayList();
    private ObservableList<InvoiceItemRow> invoiceDetailsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbTier.setItems(FXCollections.observableArrayList("Tất cả hạng mức", "Thành viên", "Bạc", "Vàng", "Kim Cương"));
        cbTier.getSelectionModel().selectFirst();
        cbNewGender.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));

        // ================= MAP CÁC CỘT DỮ LIỆU =================
        // Bảng Khách Hàng
        colId.setCellValueFactory(new PropertyValueFactory<>("makh"));
        colName.setCellValueFactory(new PropertyValueFactory<>("tenkh"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("ngaysinh")); 
        colTier.setCellValueFactory(new PropertyValueFactory<>("hangtv"));
        colPoints.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedDiem()));
        colTotalSpent.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedDoanhThu()));

        colActionView.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(null));
        colActionView.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁");
            {
                viewBtn.setStyle("-fx-background-color: #0f766e; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 14px; -fx-background-radius: 5;");
                viewBtn.setOnAction(e -> showInvoiceHistory(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });
        tableCustomer.setItems(customerList);

        // Bảng Lịch Sử Hóa Đơn
        colInvId.setCellValueFactory(new PropertyValueFactory<>("mahd"));
        colInvDate.setCellValueFactory(new PropertyValueFactory<>("ngayban"));
        colInvStatus.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
        colInvPoints.setCellValueFactory(new PropertyValueFactory<>("diemsudung"));
        colInvTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedTongTien()));
        colInvAmount.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedTienThanhToan()));
        tableInvoiceHistory.setItems(invoiceHistoryList);

        // Bảng Chi Tiết Hóa Đơn 
        colDetailMasp.setCellValueFactory(new PropertyValueFactory<>("masp"));
        colDetailMalo.setCellValueFactory(new PropertyValueFactory<>("malo"));
        colDetailSl.setCellValueFactory(new PropertyValueFactory<>("sl"));
        colDetailPrice.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedDongia()));
        colDetailTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedThanhtien()));
        tableInvoiceDetails.setItems(invoiceDetailsList);

        // ================= SỰ KIỆN CLICK DÒNG HÓA ĐƠN =================
        // Lazy Loading: Click dòng nào gọi API chi tiết dòng đó
        tableInvoiceHistory.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            invoiceDetailsList.clear();
            if (newSelection != null) {
                String mahd = newSelection.getMahd();
                
                // Gọi API lấy chi tiết Hóa đơn
                ApiService.get("/api/sales/invoices/" + mahd).thenAccept(res -> {
                    Platform.runLater(() -> {
                        try {
                            if (res.statusCode() == 200) {
                                JsonNode root = ApiService.mapper.readTree(res.body());
                                JsonNode dataNode = root.get("data");
                                
                                if (dataNode.has("items") && dataNode.get("items").isArray()) {
                                    for (JsonNode itemNode : dataNode.get("items")) {
                                        // Ưu tiên hiển thị tên sản phẩm, không có thì xài mã
                                        String tenHienThi = itemNode.has("tensanpham") && !itemNode.get("tensanpham").isNull() 
                                                            ? itemNode.get("tensanpham").asText() 
                                                            : (itemNode.has("masp") ? itemNode.get("masp").asText() : "Chưa rõ");
                                        
                                        invoiceDetailsList.add(new InvoiceItemRow(
                                            tenHienThi,
                                            itemNode.has("malo") ? itemNode.get("malo").asText() : "",
                                            itemNode.has("sl") ? itemNode.get("sl").asInt() : 0,
                                            itemNode.has("dongia") ? itemNode.get("dongia").asDouble() : 0.0,
                                            itemNode.has("thanhtien") ? itemNode.get("thanhtien").asDouble() : 0.0
                                        ));
                                    }
                                }
                            }
                        } catch (Exception e) { e.printStackTrace(); }
                    });
                });
            }
        });

        // Load dữ liệu lần đầu
        loadStats();
        handleSearchData(null);
    }

    // ==========================================
    // API: THỐNG KÊ & DANH SÁCH (TÌM KIẾM/LỌC)
    // ==========================================

    private void loadStats() {
        ApiService.get("/api/sales/customers/stats").thenAccept(res -> {
            Platform.runLater(() -> {
                try {
                    if (res.statusCode() == 200) {
                        JsonNode data = ApiService.mapper.readTree(res.body()).get("data");
                        lblTotalCustomers.setText(String.format("%,d", data.get("totalCustomers").asLong()));
                        lblDiamondCustomers.setText(String.format("%,d", data.get("diamondCustomers").asLong()));
                        lblGoldCustomers.setText(String.format("%,d", data.get("goldCustomers").asLong()));
                        lblSilverCustomers.setText(String.format("%,d", data.get("silverCustomers").asLong()));
                    }
                } catch (Exception e) { e.printStackTrace(); }
            });
        });
    }

    @FXML
    void handleSearchData(ActionEvent event) {
        String search = txtSearch.getText().trim();
        String tier = cbTier.getValue();
        
        // "Phiên dịch" chữ trước khi gọi API để khớp với Database
        if ("Tất cả hạng mức".equals(tier)) {
            tier = "";
        } else if ("Thành viên".equals(tier)) {
            tier = "THANH VIEN"; 
        }

        try {
            String url = "/api/sales/customers/list?search=" + URLEncoder.encode(search, StandardCharsets.UTF_8)
                         + "&tier=" + URLEncoder.encode(tier, StandardCharsets.UTF_8);
            
            ApiService.get(url).thenAccept(res -> {
                Platform.runLater(() -> {
                    try {
                        if (res.statusCode() == 200) {
                            customerList.clear();
                            JsonNode dataArray = ApiService.mapper.readTree(res.body()).get("data");
                            for (JsonNode node : dataArray) {
                                String ngaysinhStr = node.has("ngaysinh") && !node.get("ngaysinh").isNull() 
                                                     ? node.get("ngaysinh").asText().split("T")[0] 
                                                     : "Chưa cập nhật";

                                customerList.add(new CustomerListRow(
                                    node.get("makh").asText(),
                                    node.get("tenkh").asText(),
                                    node.get("sdt").asText(),
                                    ngaysinhStr,
                                    node.get("hangtv").asText(),
                                    node.has("tongdoanhthu") && !node.get("tongdoanhthu").isNull() ? node.get("tongdoanhthu").asDouble() : 0.0,
                                    node.has("diemtichluy") && !node.get("diemtichluy").isNull() ? node.get("diemtichluy").asDouble() : 0.0
                                ));
                            }
                            tableCustomer.refresh();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ==========================================
    // API: XEM LỊCH SỬ HÓA ĐƠN
    // ==========================================

    private void showInvoiceHistory(CustomerListRow customer) {
        lblHistoryCustomerName.setText("📋 Lịch Sử Mua Hàng: " + customer.getTenkh() + " (" + customer.getSdt() + ")");
        invoiceHistoryList.clear();
        invoiceDetailsList.clear();

        ApiService.get("/api/sales/customers/" + customer.getMakh() + "/invoices").thenAccept(res -> {
            Platform.runLater(() -> {
                try {
                    if (res.statusCode() == 200) {
                        JsonNode dataArray = ApiService.mapper.readTree(res.body()).get("data");
                        for (JsonNode node : dataArray) {
                            String ngayban = node.get("ngayban").asText();
                            if (ngayban != null && ngayban.contains("T")) {
                                ngayban = ngayban.replace("T", " ").substring(0, 19);
                            }

                            // Truyền null cho tham số cuối (items) vì giờ đã Lazy Loading
                            invoiceHistoryList.add(new InvoiceHistoryRow(
                                node.get("mahd").asText(),
                                ngayban,
                                node.get("tongtien").asDouble(),
                                node.get("diemsudung").asInt(),
                                node.get("tienthanhtoan").asDouble(),
                                node.get("trangthai").asText(),
                                null 
                            ));
                        }
                        modalInvoiceHistory.setVisible(true);
                        
                        // Tự động chọn dòng HĐ đầu tiên để nó nhảy API nạp chi tiết
                        if (!invoiceHistoryList.isEmpty()) {
                            tableInvoiceHistory.getSelectionModel().selectFirst();
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải lịch sử mua hàng!");
                    }
                } catch (Exception e) { e.printStackTrace(); }
            });
        });
    }

    @FXML void handleCloseHistory(ActionEvent event) { modalInvoiceHistory.setVisible(false); }

    // ==========================================
    // TẠO HỒ SƠ KHÁCH HÀNG MỚI
    // ==========================================

    @FXML void handleShowCreateForm(ActionEvent event) { modalOverlay.setVisible(true); }

    @FXML
    void handleCloseCreateForm(ActionEvent event) {
        modalOverlay.setVisible(false);
        clearCreateForm();
    }

    @FXML
    void handleSaveCustomer(ActionEvent event) {
        String name = txtNewName.getText().trim();
        String phone = txtNewPhone.getText().trim();
        String gender = cbNewGender.getValue();
        LocalDate dob = dpNewDOB.getValue();

        if (name.isEmpty() || phone.isEmpty() || gender == null || dob == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        LocalDate today = LocalDate.now();
        if (dob.isAfter(today) || today.getYear() - dob.getYear() > 120) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Ngày sinh không hợp lệ!");
            return;
        }

        try {
            ObjectNode json = ApiService.mapper.createObjectNode();
            json.put("tenkh", name);
            json.put("sdt", phone);
            json.put("gioitinh", gender);
            json.put("ngaysinh", dob.toString());

            ApiService.post("/api/sales/customers", json.toString()).thenAccept(res -> {
                Platform.runLater(() -> {
                    try {
                        if (res.statusCode() == 200 || res.statusCode() == 201) {
                            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã tạo hồ sơ: " + name);
                            modalOverlay.setVisible(false);
                            clearCreateForm();
                            
                            // Load lại giao diện sau khi tạo thành công
                            loadStats();
                            handleSearchData(null);
                        } else {
                            JsonNode errNode = ApiService.mapper.readTree(res.body());
                            String errMsg = errNode.has("message") ? errNode.get("message").asText() : "Lỗi hệ thống";
                            showAlert(Alert.AlertType.ERROR, "Thất bại", errMsg);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void clearCreateForm() {
        txtNewName.clear();
        txtNewPhone.clear();
        cbNewGender.getSelectionModel().clearSelection();
        dpNewDOB.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}