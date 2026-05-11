package com.pharmacy.controller.sales;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.converter.IntegerStringConverter;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class POSController {

    // --- CÁC THÀNH PHẦN GIAO DIỆN CHÍNH ---
    @FXML private TextField txtSearch, txtCustomerPhone, txtModalCustomerName, txtModalAvailablePoints, txtPointsToUse;
    @FXML private TextArea txtOrderNote;
    
    // Bảng và các cột
    @FXML private TableView<CartItem> posTable;
    @FXML private TableColumn<CartItem, String> colProductName, colPrice, colTotal;
    @FXML private TableColumn<CartItem, Integer> colQuantity;
    @FXML private TableColumn<CartItem, Void> colAction;

    // Tóm tắt thanh toán
    @FXML private Label lblSubtotal, lblDiscount, lblTotalAmount;
    
    // Modal Khách hàng
    @FXML private StackPane modalOverlay;

    // Modal Tổng Kết Hóa Đơn
    @FXML private StackPane summaryModalOverlay;
    @FXML private Label lblSumInvoiceId, lblSumStaffId, lblSumCustomerId, lblSumPhone;
    @FXML private Label lblSumDate, lblSumTotalPoints, lblSumPointsUsed, lblSumSubtotal, lblSumFinalAmount;
    @FXML private ListView<String> listSumProducts;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private final DecimalFormat formatter = new DecimalFormat("#,### đ");

    private int currentCustomerPoints = 0;
    private int pointsUsed = 0;

    @FXML
    public void initialize() {
        System.out.println("✅ POS System Full Version Ready!");

        // Cho phép chỉnh sửa số lượng trực tiếp trên bảng
        posTable.setEditable(true);

        // 1. Ánh xạ dữ liệu cột
        colProductName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        colPrice.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedPrice()));
        colTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedTotal()));

        // 2. Xử lý chỉnh sửa số lượng
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

        // 3. Setup cột Xóa
        setupActionColumn();

        // 4. Data Demo ban đầu
        cartItems.addAll(
            new CartItem("Panadol Extra 500mg", 2, 35000),
            new CartItem("Vitamin C 1000mg", 1, 95000)
        );

        posTable.setItems(cartItems);
        updateTotals();
        
        if (modalOverlay != null) modalOverlay.setVisible(false);
        if (summaryModalOverlay != null) summaryModalOverlay.setVisible(false);
    }

    // ====================== LOGIC SẢN PHẨM ======================

    @FXML
    void handleSearchProduct(ActionEvent event) {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) return;

        CartItem newItem = switch (keyword) {
            case "panadol" -> new CartItem("Panadol Extra", 1, 35000);
            case "vitc" -> new CartItem("Vitamin C 1000mg", 1, 95000);
            case "khẩu trang" -> new CartItem("Khẩu trang Y tế 4 lớp", 1, 50000);
            default -> new CartItem("Thuốc: " + keyword, 1, 20000);
        };

        cartItems.stream()
            .filter(item -> item.getProductName().equalsIgnoreCase(newItem.getProductName()))
            .findFirst()
            .ifPresentOrElse(
                item -> item.setQuantity(item.getQuantity() + 1),
                () -> cartItems.add(newItem)
            );

        posTable.refresh();
        txtSearch.clear();
        updateTotals();
    }

    @FXML
    void handleClearOrder(ActionEvent event) {
        cartItems.clear();
        if (txtOrderNote != null) txtOrderNote.clear();
        if (txtCustomerPhone != null) txtCustomerPhone.clear();
        pointsUsed = 0;
        currentCustomerPoints = 0;
        updateTotals();
        System.out.println("🗑️ Đã xóa đơn hàng.");
    }

    // ====================== LOGIC KHÁCH HÀNG & ĐIỂM ======================

    @FXML
    void handleSearchCustomer(ActionEvent event) {
        String phone = txtCustomerPhone.getText().trim();
        if (phone.isEmpty()) {
            showAlert("Nhắc nhở", "Chưa nhập SĐT", Alert.AlertType.INFORMATION);
            return;
        }

        // Demo tìm kiếm khách hàng
        if (phone.equals("0988123456")) {
            txtModalCustomerName.setText("Nguyễn Thu Hà");
            currentCustomerPoints = 150000; 
        } else {
            txtModalCustomerName.setText("Khách hàng mới");
            currentCustomerPoints = 0;
        }

        txtModalAvailablePoints.setText(formatter.format(currentCustomerPoints));
        txtPointsToUse.clear();
        modalOverlay.setVisible(true);
    }

    @FXML
    void handleConfirmPoints(ActionEvent event) {
        try {
            String input = txtPointsToUse.getText().replaceAll("[^0-9]", "");
            int points = input.isEmpty() ? 0 : Integer.parseInt(input);

            if (points > currentCustomerPoints) {
                showAlert("Lỗi", "Khách không đủ điểm! Tối đa: " + currentCustomerPoints, Alert.AlertType.ERROR);
                return;
            }

            pointsUsed = points;
            modalOverlay.setVisible(false);
            updateTotals();
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Vui lòng nhập số điểm hợp lệ!", Alert.AlertType.ERROR);
        }
    }

    @FXML void handleCloseCustomerModal(ActionEvent event) { modalOverlay.setVisible(false); }

    // ====================== TÍNH TOÁN & THANH TOÁN ======================

    private void updateTotals() {
        double subtotal = cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();

        // Điểm sử dụng không được vượt quá tổng tiền hàng
        if (pointsUsed > subtotal) {
            pointsUsed = (int) subtotal;
        }

        double total = subtotal - pointsUsed;

        lblSubtotal.setText(formatter.format(subtotal));
        lblDiscount.setText("- " + formatter.format(pointsUsed)); // Hiển thị số điểm sử dụng
        lblTotalAmount.setText(formatter.format(total));
    }

    @FXML
    void handleCheckout(ActionEvent event) {
        if (cartItems.isEmpty()) {
            showAlert("Thông báo", "Giỏ hàng đang trống!", Alert.AlertType.WARNING);
            return;
        }
        
        // --- Chuẩn bị dữ liệu hiển thị lên Bảng Tổng Kết ---
        
        // Tạo mã ID random
        lblSumInvoiceId.setText("HD" + System.currentTimeMillis());
        lblSumStaffId.setText("NV001");
        
        // Logic Khách hàng
        String phone = txtCustomerPhone.getText().trim();
        if (phone.isEmpty()) {
            lblSumCustomerId.setText("Khách lẻ");
            lblSumPhone.setText("Không có");
        } else {
            lblSumCustomerId.setText("KH" + phone.substring(Math.max(0, phone.length() - 4)));
            lblSumPhone.setText(phone);
        }

        // Lấy ngày hiện tại
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        lblSumDate.setText(dtf.format(LocalDateTime.now()));
        
        // Gán tiền và điểm
        double subtotal = cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
        double finalAmount = subtotal - pointsUsed;
        
        lblSumTotalPoints.setText(formatter.format(currentCustomerPoints));
        lblSumPointsUsed.setText("- " + formatter.format(pointsUsed));
        lblSumSubtotal.setText(formatter.format(subtotal));
        lblSumFinalAmount.setText(formatter.format(finalAmount));
        
        // Đổ danh sách sản phẩm vào ListView
        listSumProducts.getItems().clear();
        for (CartItem item : cartItems) {
            String productDetail = String.format("%d x %s - %s", 
                item.getQuantity(), 
                item.getProductName(), 
                item.getFormattedTotal());
            listSumProducts.getItems().add(productDetail);
        }

        // Hiện modal
        summaryModalOverlay.setVisible(true);
    }

    @FXML
    void handleCloseSummaryModal(ActionEvent event) {
        // Đóng bảng nếu muốn kiểm tra lại đơn
        summaryModalOverlay.setVisible(false);
    }

    @FXML
    void handleConfirmFinalize(ActionEvent event) {
        // Xử lý lưu hóa đơn xuống Database ở đây
        System.out.println("💰 Lưu hóa đơn thành công. Tổng tiền: " + lblSumFinalAmount.getText());
        
        summaryModalOverlay.setVisible(false);
        showAlert("Thành công", "Đã xuất hóa đơn thành công!", Alert.AlertType.INFORMATION);
        handleClearOrder(null); // Reset lại trạng thái
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

    // Inner Class Model
    public static class CartItem {
        private final SimpleStringProperty productName;
        private final SimpleIntegerProperty quantity;
        private final double price;

        public CartItem(String name, int qty, double price) {
            this.productName = new SimpleStringProperty(name);
            this.quantity = new SimpleIntegerProperty(qty);
            this.price = price;
        }

        public String getProductName() { return productName.get(); }
        public SimpleStringProperty productNameProperty() { return productName; }
        public int getQuantity() { return quantity.get(); }
        public void setQuantity(int qty) { this.quantity.set(qty); }
        public SimpleIntegerProperty quantityProperty() { return quantity; }
        public double getPrice() { return price; }
        public double getTotalPrice() { return price * getQuantity(); }
        public String getFormattedPrice() { return new DecimalFormat("#,### đ").format(price); }
        public String getFormattedTotal() { return new DecimalFormat("#,### đ").format(getTotalPrice()); }
    }
}