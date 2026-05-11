package com.pharmacy.controller.sales;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

public class ReturnManagerController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbFilterStatus;
    
    // Table và Columns
    @FXML private TableView<ReturnTicket> tableReturn;
    @FXML private TableColumn<ReturnTicket, String> colTicketId, colDate, colCustomer, colInvoice, colProduct, colReason;
    @FXML private TableColumn<ReturnTicket, Integer> colQuantity;

    // Modals
    @FXML private Pane modalReturnCustomer;

    // Form Khách hàng (Hỗ trợ nhiều sản phẩm)
    @FXML private TextField txtInvoiceNo, txtReturnQtyCustomer, txtRefundAmount;
    @FXML private ComboBox<String> cbProductCustomer;
    @FXML private TextArea txtReasonCustomer;
    @FXML private ListView<String> lvReturnProducts; // Danh sách sản phẩm trả

    private ObservableList<ReturnTicket> ticketList;
    private ObservableList<String> currentReturnItems;

    @FXML
    public void initialize() {
        System.out.println("🔄 Đã nạp giao diện Quản Lý Đổi Trả (Sales).");

        // Ánh xạ cột
        colTicketId.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
        colInvoice.setCellValueFactory(new PropertyValueFactory<>("invoice"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));

        // Làm nổi bật cột Số lượng trả
        colQuantity.setCellFactory(column -> new TableCell<ReturnTicket, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-alignment: CENTER;");
                }
            }
        });

        // Setup Dropdowns trạng thái
        if (cbFilterStatus != null) {
            cbFilterStatus.setItems(FXCollections.observableArrayList("Tất cả trạng thái", "Đã hoàn tiền", "Đang xử lý", "Từ chối"));
            cbFilterStatus.getSelectionModel().selectFirst();
        }
        
        // Setup ComboBox chọn sản phẩm (Dữ liệu mẫu)
        if (cbProductCustomer != null) {
            cbProductCustomer.setItems(FXCollections.observableArrayList("Panadol Extra", "Vitamin C", "Thuốc ho Bảo Thanh", "Berberin"));
        }

        // Khởi tạo list view cho các sản phẩm khách muốn trả
        currentReturnItems = FXCollections.observableArrayList();
        if (lvReturnProducts != null) {
            lvReturnProducts.setItems(currentReturnItems);
        }

        // Gọi hàm ẩn các modal lúc khởi tạo
        hideAllModals();
        loadMockData();
    }

    private void loadMockData() {
        ticketList = FXCollections.observableArrayList(
            new ReturnTicket("RT003", "10/4/2024", "Khách lẻ (A. Tuấn)", "HD-0410-01", "Thuốc ho Bảo Thanh, Vitamin C", 3, "Khách mua nhầm"),
            new ReturnTicket("RT004", "12/4/2024", "Nguyễn Thị Mai", "HD-0412-05", "Panadol Extra", 1, "Dị ứng thành phần thuốc")
        );
        tableReturn.setItems(ticketList);
    }

    @FXML 
    void onBtnShowReturnCustomer(ActionEvent event) { 
        // Xóa sạch dữ liệu form cũ trước khi mở lên
        currentReturnItems.clear(); 
        if (txtInvoiceNo != null) txtInvoiceNo.clear();
        if (txtReturnQtyCustomer != null) txtReturnQtyCustomer.clear();
        if (cbProductCustomer != null) cbProductCustomer.getSelectionModel().clearSelection();
        if (txtRefundAmount != null) txtRefundAmount.clear();
        if (txtReasonCustomer != null) txtReasonCustomer.clear();
        
        modalReturnCustomer.setVisible(true); 
    }

    @FXML 
    void onBtnHideModals(ActionEvent event) { 
        hideAllModals();
    }

    // ĐÂY LÀ HÀM BỊ THIẾU MÀ MÌNH VỪA BỔ SUNG VÀO
    private void hideAllModals() {
        if (modalReturnCustomer != null) {
            modalReturnCustomer.setVisible(false);
        }
    }

    // Nút "Thêm" sản phẩm vào danh sách đổi trả
    @FXML 
    void onBtnAddProduct(ActionEvent event) {
        String product = cbProductCustomer.getValue();
        String qtyStr = txtReturnQtyCustomer.getText();

        if (product == null || product.isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn sản phẩm cần trả!");
            return;
        }

        if (qtyStr == null || qtyStr.trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập số lượng!");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) throw new NumberFormatException();
            
            // Thêm vào danh sách hiển thị
            currentReturnItems.add(product + " - SL: " + qty);
            
            // Xóa input để nhập tiếp
            txtReturnQtyCustomer.clear();
            cbProductCustomer.getSelectionModel().clearSelection();
            
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Số lượng phải là số nguyên dương hợp lệ!");
        }
    }

    // Nút "Hoàn tiền & Lưu phiếu"
    @FXML 
    void onBtnSubmitReturnCustomer(ActionEvent event) {
        if (currentReturnItems.isEmpty()) {
            showAlert("Lỗi", "Phiếu trả hàng phải có ít nhất 1 sản phẩm! Vui lòng chọn sản phẩm và bấm Thêm.");
            return;
        }
        
        if (txtInvoiceNo.getText().trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập mã hóa đơn gốc!");
            return;
        }
        
        showAlert("Thành công", "Đã lưu phiếu trả hàng, hoàn tiền và nhập lại kho!");
        hideAllModals();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Model dữ liệu hiển thị trên bảng
    public static class ReturnTicket {
        private final SimpleStringProperty ticketId, date, customer, invoice, product, reason;
        private final SimpleIntegerProperty quantity;

        public ReturnTicket(String id, String date, String customer, String invoice, String product, int qty, String reason) {
            this.ticketId = new SimpleStringProperty(id);
            this.date = new SimpleStringProperty(date);
            this.customer = new SimpleStringProperty(customer);
            this.invoice = new SimpleStringProperty(invoice);
            this.product = new SimpleStringProperty(product);
            this.quantity = new SimpleIntegerProperty(qty);
            this.reason = new SimpleStringProperty(reason);
        }

        public String getTicketId() { return ticketId.get(); }
        public String getDate() { return date.get(); }
        public String getCustomer() { return customer.get(); }
        public String getInvoice() { return invoice.get(); }
        public String getProduct() { return product.get(); }
        public int getQuantity() { return quantity.get(); }
        public String getReason() { return reason.get(); }
    }
}