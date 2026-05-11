package com.pharmacy.controller.admin;

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
    
    @FXML private TableView<ReturnTicket> tableReturn;
    @FXML private TableColumn<ReturnTicket, String> colTicketId, colDate, colPartner, colReference, colProduct, colReason;
    @FXML private TableColumn<ReturnTicket, Integer> colQuantity;

    // Modals
    @FXML private Pane modalReturnNCC, modalReturnCustomer;
    
    // Form NCC
    @FXML private ComboBox<String> cbSupplier;
    @FXML private TextField txtProductNameNCC, txtBatchNo, txtReturnQtyNCC;
    @FXML private TextArea txtReasonNCC;

    // Form Khách hàng
    @FXML private TextField txtInvoiceNo, txtReturnQtyCustomer, txtRefundAmount;
    @FXML private ComboBox<String> cbProductCustomer;
    @FXML private TextArea txtReasonCustomer;

    private ObservableList<ReturnTicket> ticketList;

    @FXML
    public void initialize() {
        System.out.println("🔄 Đã nạp giao diện Quản Lý Đổi Trả (Chuẩn UI).");

        // Ánh xạ cột
        colTicketId.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colPartner.setCellValueFactory(new PropertyValueFactory<>("partner"));
        colReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));

        // Làm nổi bật cột Số lượng trả
        colQuantity.setCellFactory(column -> new TableCell<>() {
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

        // Setup Dropdowns
        if (cbFilterStatus != null) {
            cbFilterStatus.setItems(FXCollections.observableArrayList("Tất cả trạng thái", "Đã hoàn tất", "Đang xử lý", "Đã hủy"));
            cbFilterStatus.getSelectionModel().selectFirst();
        }
        if (cbSupplier != null) cbSupplier.setItems(FXCollections.observableArrayList("Dược Hậu Giang", "Traphaco", "Hoa Linh", "GSK"));
        if (cbProductCustomer != null) cbProductCustomer.setItems(FXCollections.observableArrayList("-- Chọn sản phẩm từ hóa đơn --", "Panadol Extra", "Vitamin C"));

        hideAllModals();
        loadMockData();
    }

    private void loadMockData() {
        ticketList = FXCollections.observableArrayList(
            new ReturnTicket("RT001", "18/4/2024", "Dược Hậu Giang", "L01-2201", "Paracetamol 500mg", 500, "Hàng cận date"),
            new ReturnTicket("RT002", "15/4/2024", "Traphaco", "L03-2305", "Hoạt huyết dưỡng não", 50, "Bao bì móp méo"),
            new ReturnTicket("RT003", "10/4/2024", "Khách lẻ (A. Tuấn)", "HD-0410-01", "Thuốc ho Bảo Thanh", 2, "Khách mua nhầm"),
            new ReturnTicket("RT004", "25/3/2024", "GSK", "L08-2311", "Panadol Extra", 1000, "Sai quy cách")
        );
        tableReturn.setItems(ticketList);
    }

    @FXML void onBtnHideModals(ActionEvent event) { hideAllModals(); }

    private void hideAllModals() {
        if (modalReturnNCC != null) modalReturnNCC.setVisible(false);
        if (modalReturnCustomer != null) modalReturnCustomer.setVisible(false);
    }

    @FXML void onBtnSubmitReturnNCC(ActionEvent event) {
        showAlert("Thành công", "Đã tạo phiếu trả hàng cho Nhà cung cấp!");
        hideAllModals();
    }

    @FXML void onBtnSubmitReturnCustomer(ActionEvent event) {
        showAlert("Thành công", "Đã hoàn tiền và nhập lại kho!");
        hideAllModals();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class ReturnTicket {
        private final SimpleStringProperty ticketId, date, partner, reference, product, reason;
        private final SimpleIntegerProperty quantity;

        public ReturnTicket(String id, String date, String partner, String ref, String product, int qty, String reason) {
            this.ticketId = new SimpleStringProperty(id);
            this.date = new SimpleStringProperty(date);
            this.partner = new SimpleStringProperty(partner);
            this.reference = new SimpleStringProperty(ref);
            this.product = new SimpleStringProperty(product);
            this.quantity = new SimpleIntegerProperty(qty);
            this.reason = new SimpleStringProperty(reason);
        }

        public String getTicketId() { return ticketId.get(); }
        public String getDate() { return date.get(); }
        public String getPartner() { return partner.get(); }
        public String getReference() { return reference.get(); }
        public String getProduct() { return product.get(); }
        public int getQuantity() { return quantity.get(); }
        public String getReason() { return reason.get(); }
    }
}
