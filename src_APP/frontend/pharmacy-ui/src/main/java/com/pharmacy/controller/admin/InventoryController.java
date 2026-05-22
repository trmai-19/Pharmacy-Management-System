package com.pharmacy.controller.admin;



import com.pharmacy.dto.InventoryReportDTO;
import com.pharmacy.util.InventoryApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class InventoryController {

    // ComboBox Bộ lọc (Slicer) - Cậu nhớ kiểm tra đúng fx:id trong file FXML nhé
    @FXML private ComboBox<String> cbYear;
    @FXML private ComboBox<String> cbQuarter;
    @FXML private ComboBox<String> cbProductGroup;
    @FXML private ComboBox<String> cbCustomerType;

    // Thẻ KPI hiển thị số liệu
    @FXML private Label lblTotalMedicines;
    @FXML private Label lblLowStock;
    @FXML private Label lblNearExpiry;
    @FXML private Label lblExpiredMedicines;
    @FXML private Label lblInventoryValue;

    // Bảng danh sách tồn kho thực tế
    @FXML private TableView<InventoryReportDTO.MedicineRow> tvInventory;
    @FXML private TableColumn<InventoryReportDTO.MedicineRow, String> colMaThuoc;
    @FXML private TableColumn<InventoryReportDTO.MedicineRow, String> colTenThuoc;
    @FXML private TableColumn<InventoryReportDTO.MedicineRow, String> colPhanLoai;
    @FXML private TableColumn<InventoryReportDTO.MedicineRow, Long> colTonKho;
    @FXML private TableColumn<InventoryReportDTO.MedicineRow, String> colHanSuDung;
    @FXML private TableColumn<InventoryReportDTO.MedicineRow, String> colTinhTrang;

    // Biểu đồ thống kê kho
    @FXML private PieChart pieCategoryDistribution;
    @FXML private BarChart<String, Number> barSlowestMoving;

    private final InventoryApiService apiService = new InventoryApiService();

    @FXML
    public void initialize() {
        // 1. Khởi tạo cấu trúc các cột cho TableView
        colMaThuoc.setCellValueFactory(new PropertyValueFactory<>("maThuoc"));
        colTenThuoc.setCellValueFactory(new PropertyValueFactory<>("tenThuoc"));
        colPhanLoai.setCellValueFactory(new PropertyValueFactory<>("phanLoai"));
        colTonKho.setCellValueFactory(new PropertyValueFactory<>("tonKho"));
        colHanSuDung.setCellValueFactory(new PropertyValueFactory<>("hanSuDung"));
        colTinhTrang.setCellValueFactory(new PropertyValueFactory<>("tinhTrang"));

        // 2. Nạp dữ liệu ban đầu cho các Slicer ComboBox
        initFilterComboBoxes();

        // 3. Đăng ký bộ lắng nghe sự kiện: Slicer thay đổi là gọi API cập nhật ngay tắp lự
        cbYear.setOnAction(e -> fetchAndLoadInventoryData());
        cbQuarter.setOnAction(e -> fetchAndLoadInventoryData());
        cbProductGroup.setOnAction(e -> fetchAndLoadInventoryData());
        cbCustomerType.setOnAction(e -> fetchAndLoadInventoryData());

        // 4. Kéo dữ liệu mặc định lên giao diện lần đầu tiên
        fetchAndLoadInventoryData();
    }

    private void initFilterComboBoxes() {
        cbYear.setItems(FXCollections.observableArrayList("Năm", "2023", "2024", "2025", "2026"));
        cbQuarter.setItems(FXCollections.observableArrayList("Quý", "Q1", "Q2", "Q3", "Q4"));
        cbProductGroup.setItems(FXCollections.observableArrayList(
                "All Product Groups", "Thuốc cảm", "Kháng sinh", "Vitamin", 
                "Tiêu hóa", "Tim mạch", "Da liễu", "Xương khớp", "Hô hấp", "Tiểu đường", "Mắt"
        ));
        cbCustomerType.setItems(FXCollections.observableArrayList("All Customers", "VIP", "New Customers", "Loyal Customers"));

        // Đặt giá trị mặc định lúc khởi chạy giống hệt như thiết kế của cậu
        cbYear.setValue("2024");
        cbQuarter.setValue("Quý");
        cbProductGroup.setValue("All Product Groups");
        cbCustomerType.setValue("All Customers");
    }

    private void fetchAndLoadInventoryData() {
        String year = cbYear.getValue();
        String quarter = cbQuarter.getValue();
        String productGroup = cbProductGroup.getValue();
        String customerType = cbCustomerType.getValue();

        apiService.fetchInventoryData(year, quarter, productGroup, customerType).thenAccept(data -> {
            Platform.runLater(() -> {
                // Cập nhật giá trị lên các thẻ KPI
                lblTotalMedicines.setText(data.getTotalMedicines());
                lblLowStock.setText(data.getLowStockCount());
                lblNearExpiry.setText(data.getNearExpiryCount());
                lblExpiredMedicines.setText(data.getExpiredCount());
                lblInventoryValue.setText(data.getInventoryValue());

                // Đổ dữ liệu thật vào TableView
                ObservableList<InventoryReportDTO.MedicineRow> rows = FXCollections.observableArrayList(data.getTableData());
                tvInventory.setItems(rows);

                // Vẽ lại biểu đồ tròn phân bổ danh mục thuốc
                updateCategoryPieChart(data);

                // Vẽ lại biểu đồ cột sản phẩm ứ đọng nhiều nhất
                updateSlowMovingBarChart(data);
            });
        }).exceptionally(ex -> {
            System.err.println("Gặp sự cố khi kết nối dữ liệu kho: " + ex.getMessage());
            return null;
        });
    }

    private void updateCategoryPieChart(InventoryReportDTO data) {
        if (pieCategoryDistribution == null) return;
        pieCategoryDistribution.getData().clear();
        
        for (InventoryReportDTO.ChartData c : data.getCategoryDistribution()) {
            pieCategoryDistribution.getData().add(new PieChart.Data(c.getLabel(), c.getValue()));
        }
    }

    private void updateSlowMovingBarChart(InventoryReportDTO data) {
        if (barSlowestMoving == null) return;
        barSlowestMoving.setAnimated(false);
        barSlowestMoving.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tồn kho thực tế");
        
        for (InventoryReportDTO.ChartData s : data.getSlowestMoving()) {
            series.getData().add(new XYChart.Data<>(s.getLabel(), s.getValue()));
        }
        
        barSlowestMoving.getData().add(series);
    }
}