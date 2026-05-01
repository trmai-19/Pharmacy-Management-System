package com.pharmacy.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import java.time.LocalDate;

public class ReportController {

    // --- BỘ LỌC CHUNG ---
    // @FXML private ComboBox<String> cbTimeFilter;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;

    // --- TAB 1: BÁN HÀNG ---
    @FXML private Label lblTotalRevenue;
    @FXML private Label lblTotalInvoices;
    @FXML private Label lblAvgInvoiceValue;
    @FXML private LineChart<String, Number> revenueChart;

    // --- TAB 2: NHẬP HÀNG ---
    @FXML private Label lblTotalImportValue;
    @FXML private Label lblImportCount;
    @FXML private Label lblTopSupplier;
    @FXML private BarChart<String, Number> supplierChart; 

    // --- TAB 3: ĐỔI TRẢ ---
    @FXML private Label lblTotalReturnValue;
    @FXML private Label lblReturnCount;
    @FXML private Label lblReturnRate;
    @FXML private PieChart returnReasonChart;

    @FXML
    public void initialize() {
        System.out.println("📊 Nạp giao diện Báo Cáo có 3 Tab Con...");

        // Khởi tạo bộ lọc thời gian
        // cbTimeFilter.setItems(FXCollections.observableArrayList("Theo Ngày", "Theo Tuần", "Theo Tháng", "Tùy Chỉnh"));
        // cbTimeFilter.getSelectionModel().select("Theo Tuần");
        dpStartDate.setValue(LocalDate.now().minusDays(7));
        dpEndDate.setValue(LocalDate.now());

        // Chạy hàm nạp dữ liệu cho cả 3 Tab
        loadAllData();
    }

    @FXML
    void handleFilter(ActionEvent event) {
        System.out.println("🔄 Đang truy xuất Database từ " + dpStartDate.getValue() + " đến " + dpEndDate.getValue());
        loadAllData(); // Tái nạp dữ liệu dựa trên ngày
    }

    @FXML
    void handleExport(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Xuất Excel");
        alert.setHeaderText("Tải Báo Cáo Toàn Diện");
        alert.setContentText("Đang tổng hợp dữ liệu Bán Hàng, Nhập Hàng và Đổi Trả thành 3 sheet Excel...");
        alert.showAndWait();
    }

    private void loadAllData() {
        loadSalesTab();
        loadImportTab();
        loadReturnTab();
    }

    // =====================================
    // HÀM NẠP DỮ LIỆU TAB 1 (BÁN HÀNG)
    // =====================================
    private void loadSalesTab() {
        lblTotalRevenue.setText("245.800.000đ");
        lblTotalInvoices.setText("1,452");
        lblAvgInvoiceValue.setText("169.200đ");

        revenueChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu (Triệu VNĐ)");
        
        series.getData().add(new XYChart.Data<>("Thứ 2", 12.5));
        series.getData().add(new XYChart.Data<>("Thứ 3", 18.2));
        series.getData().add(new XYChart.Data<>("Thứ 4", 15.0));
        series.getData().add(new XYChart.Data<>("Thứ 5", 24.5));
        series.getData().add(new XYChart.Data<>("Thứ 6", 21.3));
        series.getData().add(new XYChart.Data<>("Thứ 7", 35.8));
        series.getData().add(new XYChart.Data<>("Chủ Nhật", 28.5));

        revenueChart.getData().add(series);
    }

    // =====================================
    // HÀM NẠP DỮ LIỆU TAB 2 (NHẬP HÀNG)
    // =====================================
    private void loadImportTab() {
        lblTotalImportValue.setText("180.500.000đ");
        lblImportCount.setText("24 Phiếu");
        lblTopSupplier.setText("Dược Hậu Giang");

        supplierChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tiền Nhập (Triệu VNĐ)");

        // Biểu đồ cột đứng: Trục X (Nhà cung cấp), Trục Y (Tiền)
        series.getData().add(new XYChart.Data<>("Dược Hậu Giang", 85.5));
        series.getData().add(new XYChart.Data<>("Sanofi VN", 42.0));
        series.getData().add(new XYChart.Data<>("Traphaco", 28.4));
        series.getData().add(new XYChart.Data<>("AstraZeneca", 15.6));
        series.getData().add(new XYChart.Data<>("Khác", 9.0));

        supplierChart.getData().add(series);
    }

    // =====================================
    // HÀM NẠP DỮ LIỆU TAB 3 (ĐỔI TRẢ)
    // =====================================
    private void loadReturnTab() {
        lblTotalReturnValue.setText("1.250.000đ");
        lblReturnCount.setText("18 Sản Phẩm");
        lblReturnRate.setText("1.2%"); // = (18 / 1452)*100

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Khách dị ứng thuốc (30%)", 30),
                new PieChart.Data("Sản phẩm cận Date (40%)", 40),
                new PieChart.Data("Bán sai toa bác sĩ (15%)", 15),
                new PieChart.Data("Lỗi vỏ hộp/móp méo (15%)", 15)
        );
        returnReasonChart.setData(pieData);
    }
}