package com.pharmacy.controller.sales;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pharmacy.model.BatchSelectRow;
import com.pharmacy.model.CartItem;
import com.pharmacy.util.ApiService;
import com.pharmacy.util.Session;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class POSController {

    // --- CÁC THÀNH PHẦN GIAO DIỆN CHÍNH ---
    @FXML private ComboBox<String> cbSearchProduct; 
    @FXML private TextField txtCustomerPhone;
    @FXML private TextArea txtOrderNote;
    
    // Bảng Giỏ Hàng
    @FXML private TableView<CartItem> posTable;
    @FXML private TableColumn<CartItem, String> colProductName, colPrice, colTotal;
    @FXML private TableColumn<CartItem, Integer> colQuantity;
    @FXML private TableColumn<CartItem, Void> colAction;

    // Khu vực Khách Hàng (Right Panel)
    @FXML private VBox boxCustomerInfo;
    @FXML private Label lblCustomerName, lblAvailablePoints;
    @FXML private TextField txtPointsToUse;
    @FXML private Label lblCartSubtotal;
    
    // Modal CHỌN LÔ HÀNG
    @FXML private StackPane modalBatchSelection;
    @FXML private Label lblSelectProductName;
    @FXML private TableView<BatchSelectRow> tableBatchSelection;
    @FXML private TableColumn<BatchSelectRow, String> colBatchMalo, colBatchNsx, colBatchHsd, colBatchStatus;
    @FXML private TableColumn<BatchSelectRow, Integer> colBatchStock;
    @FXML private TextField txtSelectQuantity;
    private String tempSelectedMasp = "";
    private String tempSelectedTensp = "";
    private double tempSelectedPrice = 0.0;

    // Modal Khách hàng MỚI (Tạo nhanh)
    @FXML private StackPane modalQuickCreate;
    @FXML private TextField txtQuickCreatePhone, txtQuickCreateName;
    @FXML private ComboBox<String> cbQuickCreateGender;

    // Modal Tổng Kết Hóa Đơn
    @FXML private StackPane summaryModalOverlay;
    @FXML private Label lblSumInvoiceId, lblSumStaffId, lblSumCustomerId, lblSumPhone;
    @FXML private Label lblSumDate, lblSumTotalPoints, lblSumPointsUsed, lblSumSubtotal, lblSumFinalAmount;
    @FXML private ListView<String> listSumProducts;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private ObservableList<BatchSelectRow> batchList = FXCollections.observableArrayList();
    private final DecimalFormat formatter = new DecimalFormat("#,### đ");

    private String currentCustomerId = "KHACH_LE";
    private String currentCustomerName = "Khách lẻ";
    private String currentCustomerPhone = "";
    private int currentCustomerPoints = 0;

    @FXML
    public void initialize() {
        System.out.println("✅ POS System: Khởi động với Code chuẩn MVC (Đã tách Model)!");

        if (cbQuickCreateGender != null) {
            cbQuickCreateGender.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
            cbQuickCreateGender.getSelectionModel().selectFirst();
        }

        posTable.setEditable(true);

        // Map cột Giỏ hàng
        colProductName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        colPrice.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedPrice()));
        colTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedTotal()));
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        colQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colQuantity.setOnEditCommit(event -> {
            CartItem item = event.getRowValue();
            Integer newValue = event.getNewValue();
            if (newValue != null && newValue > 0) {
                item.setQuantity(newValue);
            } else {
                showAlert("Lỗi", "Số lượng phải lớn hơn 0!", Alert.AlertType.WARNING);
                posTable.refresh();
            }
            updateTotals();
        });
        setupActionColumn();
        posTable.setItems(cartItems);

        // Map cột Modal Bảng chọn Lô
        colBatchMalo.setCellValueFactory(new PropertyValueFactory<>("malo"));
        colBatchNsx.setCellValueFactory(new PropertyValueFactory<>("nsx"));
        colBatchHsd.setCellValueFactory(new PropertyValueFactory<>("hsd"));
        colBatchStock.setCellValueFactory(new PropertyValueFactory<>("sl"));
        colBatchStatus.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
        tableBatchSelection.setItems(batchList);

        // Lắng nghe gõ tìm kiếm Thuốc
        setupProductSearch();

        // Ẩn tất cả Modal và Box Customer
        modalBatchSelection.setVisible(false);
        modalQuickCreate.setVisible(false);
        summaryModalOverlay.setVisible(false);
        boxCustomerInfo.setVisible(false);
        boxCustomerInfo.setManaged(false);

        // Ràng buộc ô nhập điểm chỉ nhận số
        if (txtPointsToUse != null) {
            txtPointsToUse.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    txtPointsToUse.setText(newVal.replaceAll("[^\\d]", ""));
                }
            });
        }
    }

    // ====================== TÌM SẢN PHẨM & CHỌN LÔ ======================

    private void setupProductSearch() {
        cbSearchProduct.getEditor().setOnKeyReleased(event -> {
            String keyword = cbSearchProduct.getEditor().getText().trim();
            if (keyword.length() >= 2) {
                ApiService.get("/api/products?search=" + keyword).thenAccept(res -> {
                    Platform.runLater(() -> {
                        try {
                            if (res.statusCode() == 200) {
                                JsonNode dataNode = ApiService.mapper.readTree(res.body()).get("data");
                                ObservableList<String> list = FXCollections.observableArrayList();
                                for (JsonNode node : dataNode) {
                                    list.add(node.get("masp").asText() + " - " + node.get("tensanpham").asText() + " - " + node.get("giaban").asText());
                                }
                                String current = cbSearchProduct.getEditor().getText();
                                cbSearchProduct.setItems(list);
                                cbSearchProduct.getEditor().setText(current);
                                cbSearchProduct.getEditor().positionCaret(current.length());
                                cbSearchProduct.show();
                            }
                        } catch (Exception e) { e.printStackTrace(); }
                    });
                });
            }
        });
    }

    @FXML
    void handleSelectProduct(ActionEvent event) {
        String selected = cbSearchProduct.getValue();
        if (selected == null || !selected.contains(" - ")) return;

        String[] parts = selected.split(" - ");
        tempSelectedMasp = parts[0].trim();
        tempSelectedTensp = parts[1].trim();
        tempSelectedPrice = Double.parseDouble(parts[2].trim());

        lblSelectProductName.setText("Sản phẩm: " + tempSelectedTensp);
        txtSelectQuantity.clear();

        // Kéo lô hàng lên Modal
        batchList.clear();
        ApiService.get("/api/sales/batches/" + tempSelectedMasp).thenAccept(res -> {
            Platform.runLater(() -> {
                try {
                    if (res.statusCode() == 200) {
                        JsonNode dataNode = ApiService.mapper.readTree(res.body()).get("data");
                        for (JsonNode node : dataNode) {
                            String nsxStr = node.has("nsx") && !node.get("nsx").isNull() ? node.get("nsx").asText().split("T")[0] : "";
                            String hsdStr = node.has("hsd") && !node.get("hsd").isNull() ? node.get("hsd").asText().split("T")[0] : "";
                            batchList.add(new BatchSelectRow(
                                node.get("malo").asText(),
                                nsxStr,
                                hsdStr,
                                node.get("sl").asInt(),
                                node.get("trangthai").asText()
                            ));
                        }
                        modalBatchSelection.setVisible(true);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            });
        });
    }

    @FXML void handleCloseBatchSelection() { 
        modalBatchSelection.setVisible(false); 
        cbSearchProduct.getSelectionModel().clearSelection();
        cbSearchProduct.getEditor().clear();
    }

    @FXML
    void handleConfirmBatchSelection() {
        BatchSelectRow selectedBatch = tableBatchSelection.getSelectionModel().getSelectedItem();
        if (selectedBatch == null) {
            showAlert("Nhắc nhở", "Vui lòng chọn 1 Lô hàng trong bảng để bán!", Alert.AlertType.WARNING);
            return;
        }

        String qtyStr = txtSelectQuantity.getText().trim();
        if (qtyStr.isEmpty()) {
            showAlert("Nhắc nhở", "Vui lòng nhập số lượng bán!", Alert.AlertType.WARNING);
            return;
        }

        int qty = 0;
        try { qty = Integer.parseInt(qtyStr); } 
        catch (Exception e) { showAlert("Lỗi", "Số lượng không hợp lệ!", Alert.AlertType.ERROR); return; }

        if (qty <= 0) {
            showAlert("Lỗi", "Số lượng phải lớn hơn 0!", Alert.AlertType.ERROR); return;
        }
        if (qty > selectedBatch.getSl()) {
            showAlert("Lỗi", "Vượt quá Tồn kho của lô này (" + selectedBatch.getSl() + ")!", Alert.AlertType.ERROR); return;
        }

        final String finalMalo = selectedBatch.getMalo();
        final int finalQty = qty;

        cartItems.stream()
            .filter(item -> item.getMalo().equals(finalMalo))
            .findFirst()
            .ifPresentOrElse(
                item -> item.setQuantity(item.getQuantity() + finalQty),
                () -> cartItems.add(new CartItem(tempSelectedMasp, finalMalo, tempSelectedTensp, finalQty, tempSelectedPrice))
            );
        
        posTable.refresh();
        updateTotals();
        handleCloseBatchSelection();
    }

    // ====================== LOGIC KHÁCH HÀNG ======================

    @FXML
    void handleSearchCustomer(ActionEvent event) {
        String phone = txtCustomerPhone.getText().trim();
        if (phone.isEmpty()) {
            showAlert("Nhắc nhở", "Vui lòng nhập Số điện thoại khách hàng!", Alert.AlertType.INFORMATION);
            return;
        }

        ApiService.get("/api/sales/customers?sdt=" + phone).thenAccept(response -> {
            Platform.runLater(() -> {
                try {
                    if (response.statusCode() == 200) {
                        JsonNode root = ApiService.mapper.readTree(response.body());
                        if (root.has("data") && !root.get("data").isNull()) {
                            JsonNode data = root.get("data");
                            currentCustomerId = data.has("makh") ? data.get("makh").asText() : "";
                            currentCustomerName = data.has("tenkh") ? data.get("tenkh").asText() : "";
                            currentCustomerPhone = phone;
                            currentCustomerPoints = data.has("diemtichluy") ? data.get("diemtichluy").asInt() : 0;
                            
                            lblCustomerName.setText(currentCustomerName);
                            lblAvailablePoints.setText(formatter.format(currentCustomerPoints));
                            txtPointsToUse.clear();
                            
                            boxCustomerInfo.setVisible(true);
                            boxCustomerInfo.setManaged(true);
                        } else {
                            openQuickCreateModal(phone);
                        }
                    } else {
                        openQuickCreateModal(phone);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Lỗi", "Không thể lấy thông tin khách hàng!", Alert.AlertType.ERROR);
                }
            });
        });
    }

    private void openQuickCreateModal(String phone) {
        txtQuickCreatePhone.setText(phone); 
        txtQuickCreateName.clear();         
        if(cbQuickCreateGender != null) cbQuickCreateGender.getSelectionModel().selectFirst();
        modalQuickCreate.setVisible(true);
    }

    @FXML void handleCloseQuickCreate(ActionEvent event) { modalQuickCreate.setVisible(false); }

    @FXML
    void handleSubmitQuickCreate(ActionEvent event) {
        String phone = txtQuickCreatePhone.getText().trim();
        String name = txtQuickCreateName.getText().trim();
        String gender = cbQuickCreateGender.getValue();

        if (name.isEmpty()) {
            showAlert("Cảnh báo", "Vui lòng không để trống Tên khách hàng!", Alert.AlertType.WARNING);
            return;
        }

        try {
            ObjectNode json = ApiService.mapper.createObjectNode();
            json.put("tenkh", name);
            json.put("sdt", phone);
            json.put("gioitinh", gender);

            ApiService.post("/api/sales/customers/quick-create", json.toString()).thenAccept(response -> {
                Platform.runLater(() -> {
                    try {
                        if (response.statusCode() == 200 || response.statusCode() == 201) {
                            JsonNode data = ApiService.mapper.readTree(response.body()).get("data");
                            currentCustomerId = data.has("makh") ? data.get("makh").asText() : "";
                            currentCustomerName = name;
                            currentCustomerPhone = phone;
                            currentCustomerPoints = data.has("diemtichluy") ? data.get("diemtichluy").asInt() : 0;
                            
                            modalQuickCreate.setVisible(false); 
                            
                            lblCustomerName.setText(currentCustomerName);
                            lblAvailablePoints.setText(formatter.format(currentCustomerPoints));
                            txtPointsToUse.clear();
                            boxCustomerInfo.setVisible(true);
                            boxCustomerInfo.setManaged(true);
                            
                        } else {
                            showAlert("Thất bại", "Không thể tạo nhanh khách hàng. Mã lỗi: " + response.statusCode(), Alert.AlertType.ERROR);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void handleClearOrder(ActionEvent event) {
        cartItems.clear();
        if (txtOrderNote != null) txtOrderNote.clear();
        if (txtCustomerPhone != null) txtCustomerPhone.clear();
        
        currentCustomerId = "KHACH_LE";
        currentCustomerName = "Khách lẻ";
        currentCustomerPhone = "";
        currentCustomerPoints = 0;
        
        boxCustomerInfo.setVisible(false);
        boxCustomerInfo.setManaged(false);
        
        if(txtPointsToUse != null) txtPointsToUse.clear();
        updateTotals();
    }

    // ====================== TÍNH TOÁN & THANH TOÁN ======================

    private void updateTotals() {
        double subtotal = cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
        lblCartSubtotal.setText(formatter.format(subtotal));
    }

    @FXML
    void handleCheckout(ActionEvent event) {
        if (cartItems.isEmpty()) {
            showAlert("Thông báo", "Giỏ hàng đang trống!", Alert.AlertType.WARNING);
            return;
        }

        // 1. Tính toán điểm sử dụng
        int points = 0;
        if (boxCustomerInfo.isVisible() && txtPointsToUse.getText() != null && !txtPointsToUse.getText().isEmpty()) {
            try { points = Integer.parseInt(txtPointsToUse.getText().trim()); } 
            catch (Exception ignored) {}
        }

        if (points > currentCustomerPoints) {
            showAlert("Lỗi Điểm", "Khách hàng không đủ điểm! Tối đa: " + currentCustomerPoints, Alert.AlertType.ERROR);
            return;
        }

        double subtotal = cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
        if (points > subtotal) { points = (int) subtotal; }
        int finalPointsUsed = points;

        // 2. GỌI API LƯU XUỐNG DATABASE TRƯỚC TIÊN
        try {
            ObjectNode rootJson = ApiService.mapper.createObjectNode();
            
            String manv = Session.getCurrentUser() != null ? Session.getCurrentUser().getManv() : "NV001";
            rootJson.put("manv", manv);
            
            if (currentCustomerId != null && !currentCustomerId.isEmpty() && !currentCustomerId.equals("KHACH_LE")) {
                rootJson.put("makh", currentCustomerId);
            }
            rootJson.put("diemsudung", finalPointsUsed);

            ArrayNode itemsArray = ApiService.mapper.createArrayNode();
            for (CartItem item : cartItems) {
                ObjectNode itemNode = ApiService.mapper.createObjectNode();
                itemNode.put("masp", item.getMasp());
                itemNode.put("malo", item.getMalo());
                itemNode.put("sl", item.getQuantity());
                itemsArray.add(itemNode);
            }
            rootJson.set("items", itemsArray);

            ApiService.post("/api/sales/invoices", rootJson.toString()).thenAccept(response -> {
                Platform.runLater(() -> {
                    try {
                        if (response.statusCode() == 200) {
                            JsonNode resRoot = ApiService.mapper.readTree(response.body());
                            
                            // BẮT MÃ HÓA ĐƠN THẬT TỪ DATABASE TRẢ VỀ
                            String mahd = resRoot.path("data").path("mahd").asText();
                            String ngayban = resRoot.path("data").path("ngayban").asText(); 
                            if (ngayban != null && ngayban.contains("T")) {
                                ngayban = ngayban.replace("T", " ").substring(0, 19);
                            }
                            
                            // 3. ĐIỀN DỮ LIỆU LÊN BIÊN LAI THANH TOÁN
                            lblSumInvoiceId.setText(mahd);
                            lblSumStaffId.setText(manv);
                            
                            if (currentCustomerPhone.isEmpty()) {
                                lblSumCustomerId.setText("Khách lẻ");
                                lblSumPhone.setText("Không có");
                            } else {
                                lblSumCustomerId.setText(currentCustomerId.isEmpty() ? currentCustomerPhone : currentCustomerId);
                                lblSumPhone.setText(currentCustomerPhone);
                            }

                            lblSumDate.setText(ngayban != null && !ngayban.isEmpty() ? ngayban : DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
                            
                            double finalAmount = subtotal - finalPointsUsed;
                            
                            lblSumTotalPoints.setText(formatter.format(currentCustomerPoints));
                            lblSumPointsUsed.setText("- " + formatter.format(finalPointsUsed));
                            lblSumSubtotal.setText(formatter.format(subtotal));
                            lblSumFinalAmount.setText(formatter.format(finalAmount));
                            
                            listSumProducts.getItems().clear();
                            for (CartItem item : cartItems) {
                                String productDetail = String.format("%d x %s - %s", 
                                    item.getQuantity(), item.getProductName(), item.getFormattedTotal());
                                listSumProducts.getItems().add(productDetail);
                            }

                            // 4. HIỆN BIÊN LAI LÊN MÀN HÌNH
                            summaryModalOverlay.setVisible(true);
                            
                        } else {
                            JsonNode errNode = ApiService.mapper.readTree(response.body());
                            String errMsg = errNode.has("message") ? errNode.get("message").asText() : "Lỗi hệ thống";
                            showAlert("Thất bại", "Lỗi tạo hóa đơn: " + errMsg, Alert.AlertType.ERROR);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            });

        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML 
    void handleCloseSummaryModal(ActionEvent event) { 
        summaryModalOverlay.setVisible(false); 
        handleClearOrder(null); 
    }

    @FXML
    void handlePrintInvoice(ActionEvent event) {
        showAlert("Đang in...", "Hệ thống đang kết nối máy in để in hóa đơn: " + lblSumInvoiceId.getText(), Alert.AlertType.INFORMATION);
        summaryModalOverlay.setVisible(false);
        handleClearOrder(null); 
    }

    // ====================== HELPER ======================

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDel = new Button("✕");
            {
                btnDel.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-cursor: hand; -fx-font-weight: bold; -fx-background-radius: 5;");
                btnDel.setOnAction(e -> {
                    cartItems.remove(getTableRow().getItem());
                    updateTotals();
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDel);
            }
        });
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}