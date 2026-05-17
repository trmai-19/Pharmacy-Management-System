package com.pharmacy.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
// Nhớ kiểm tra xem có import cái này chưa nhé
import javafx.scene.chart.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import com.pharmacy.model.Medicine;

import javafx.scene.control.TableView; // Thêm import
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReportController implements Initializable {

    // 1. KHAI BÁO CÁC BIỂU ĐỒ TỪ SCENE BUILDER (Tên biến phải khớp 100% với fx:id)
    @FXML private AreaChart<String, Number> trendAreaChart;
    @FXML private BarChart<String, Number> topProductsBarChart;
    @FXML private PieChart genderPieChart;
    @FXML private PieChart agePieChart;

    // 1. Khai báo 3 cái "lá bài" (3 AnchorPane)
    @FXML private AnchorPane panePerformance;
    @FXML private AnchorPane paneInventory;
    @FXML private AnchorPane paneCustomer;

    @FXML private Label lblInvTotalMeds;
    @FXML private Label lblInvLowStock;
    @FXML private Label lblInvNearExpiry;
    @FXML private Label lblInvTotalValue;

    // 1. KHAI BÁO TABLEVIEW VÀ CÁC CỘT (Kiểu dữ liệu là <Medicine, Loại_Dữ_Liệu_Của_Cột>)
    @FXML private TableView<Medicine> tableInventory;
    @FXML private TableColumn<Medicine, String> colMedId;
    @FXML private TableColumn<Medicine, String> colMedName;
    @FXML private TableColumn<Medicine, String> colMedCategory;
    @FXML private TableColumn<Medicine, Integer> colMedStock;
    @FXML private TableColumn<Medicine, String> colMedExpiry;
    @FXML private TableColumn<Medicine, String> colMedStatus;

    @FXML private BarChart<String, Number> barChartInventory;

    @FXML private PieChart pieChartInventory;


    // ============ KHU VỰC 5 KPI KHÁCH HÀNG ============
    @FXML private Label lblCustTotal;        // Tổng khách hàng
    @FXML private Label lblCustNew;          // Khách hàng mới
    @FXML private Label lblCustReturnRate;   // Tỉ lệ quay lại (%)
    @FXML private Label lblCustVip;          // Khách VIP
    @FXML private Label lblCustLost;         // Khách rời bỏ
    


    // 2. HÀM KHỞI CHẠY (Chạy ngay khi mở Tab Báo cáo)
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTrendAreaChart();
        loadTopProductsBarChart();
        loadGenderPieChart();
        loadAgePieChart();
        loadInventoryKPIData();
        initInventoryTable();
        loadInventoryBarChart();
        loadInventoryPieChart();
        loadCustomerKPIs();
    }

    // 2. Viết sự kiện khi bấm nút Performance
    @FXML
    public void onPerformanceClick(ActionEvent event) {
        // Lôi pane Performance lên trên cùng
        panePerformance.toFront();
    }

    // 3. CÁC HÀM ĐỔ DỮ LIỆU CHO TỪNG BIỂU ĐỒ
    
    private void loadTrendAreaChart() {
        trendAreaChart.getData().clear(); // Xóa dữ liệu cũ (nếu có)
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu 2026");

        // Thêm dữ liệu giả vào biểu đồ đường/vùng
        series.getData().add(new XYChart.Data<>("Tháng 1", 12000000));
        series.getData().add(new XYChart.Data<>("Tháng 2", 15000000));
        series.getData().add(new XYChart.Data<>("Tháng 3", 11000000));
        series.getData().add(new XYChart.Data<>("Tháng 4", 18000000));
        series.getData().add(new XYChart.Data<>("Tháng 5", 22000000));

        trendAreaChart.getData().add(series);
    }

    private void loadTopProductsBarChart() {
        topProductsBarChart.getData().clear();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng bán");

        // Thêm dữ liệu giả vào biểu đồ cột
        series.getData().add(new XYChart.Data<>("Panadol", 500));
        series.getData().add(new XYChart.Data<>("Vitamin C", 420));
        series.getData().add(new XYChart.Data<>("Khẩu trang", 350));
        series.getData().add(new XYChart.Data<>("Nước muối", 300));
        series.getData().add(new XYChart.Data<>("Bông y tế", 150));

        topProductsBarChart.getData().add(series);
    }

    private void loadGenderPieChart() {
        // Dữ liệu cho biểu đồ tròn
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Nam (45%)", 45),
                new PieChart.Data("Nữ (55%)", 55)
        );
        genderPieChart.setData(pieChartData);
    }

    private void loadAgePieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("18-24", 15),
                new PieChart.Data("25-34", 40),
                new PieChart.Data("35-44", 25),
                new PieChart.Data("45+", 20)
        );
        agePieChart.setData(pieChartData);
    }

        private void loadInventoryKPIData() {
        // Data giả
        int totalMeds = 450;            // 450 loại thuốc
        int lowStock = 12;              // 12 loại sắp hết
        int nearExpiry = 8;             // 8 loại sắp hết hạn
        double totalInvValue = 1250000000.0; // 1.25 tỷ VNĐ

        NumberFormat numF = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        NumberFormat curF = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Đổ dữ liệu vào Label
        if(lblInvTotalMeds != null) lblInvTotalMeds.setText(numF.format(totalMeds));
        if(lblInvLowStock != null) lblInvLowStock.setText(numF.format(lowStock));
        if(lblInvNearExpiry != null) lblInvNearExpiry.setText(numF.format(nearExpiry));
        if(lblInvTotalValue != null) lblInvTotalValue.setText(curF.format(totalInvValue).replace("₫", "VNĐ"));
    }

    private void initInventoryTable() {
        // A. KẾT NỐI CÁC CỘT VỚI THUỘC TÍNH TRONG CLASS MEDICINE
        // Chữ trong ngoặc kép phải trùng khớp 100% với tên biến ở class Medicine ở Bước 1
        colMedId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colMedCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colMedStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colMedExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colMedStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // B. TẠO DATA GIẢ
        ObservableList<Medicine> inventoryList = FXCollections.observableArrayList(
            new Medicine("MED001", "Panadol Extra", "Thuốc giảm đau", 150, "12/12/2028", "Bình thường"),
            new Medicine("MED002", "Amoxicillin 500mg", "Thuốc kháng sinh", 8, "15/08/2026", "Sắp hết hàng"),
            new Medicine("MED003", "Vitamin C Celcon", "Thực phẩm chức năng", 80, "01/09/2026", "Cận hạn sử dụng"),
            new Medicine("MED004", "Paracetamol 500mg", "Thuốc giảm đau", 0, "20/10/2027", "Hết hàng"),
            new Medicine("MED005", "Augmentin 1g", "Thuốc kháng sinh", 45, "05/07/2026", "Cận hạn sử dụng")
        );

        // C. ĐẨY DATA VÀO BẢNG
        if (tableInventory != null) {
            tableInventory.setItems(inventoryList);
        }
    }

    private void loadInventoryBarChart() {
        // Kiểm tra an toàn kẻo dính NullPointerException
        if (barChartInventory == null) return;

        barChartInventory.getData().clear(); // Xóa sạch dữ liệu cũ nếu có

        // Tạo 1 chuỗi dữ liệu (Series)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng tồn (Hộp)");

        // Nhét dữ liệu giả vào (Tên thuốc, Số lượng)
        series.getData().add(new XYChart.Data<>("Panadol Extra", 150));
        series.getData().add(new XYChart.Data<>("Vitamin C", 80));
        series.getData().add(new XYChart.Data<>("Augmentin 1g", 45));
        series.getData().add(new XYChart.Data<>("Amoxicillin", 8));
        series.getData().add(new XYChart.Data<>("Paracetamol", 0));

        // Ném chuỗi dữ liệu vào biểu đồ
        barChartInventory.getData().add(series);
    }

    private void loadInventoryPieChart() {
        if (pieChartInventory == null) return;

        pieChartInventory.getData().clear();

        // Tạo dữ liệu giả: Tên danh mục + Tỉ lệ %
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Kháng sinh", 40),
                new PieChart.Data("Thuốc giảm đau", 25),
                new PieChart.Data("Vitamin & TPCN", 20),
                new PieChart.Data("Vật tư y tế", 10),
                new PieChart.Data("Khác", 5)
        );

        // Ném dữ liệu vào biểu đồ
        pieChartInventory.setData(pieChartData);
    }

    private void loadCustomerKPIs() {
        if (lblCustTotal != null) lblCustTotal.setText("2,450");
        if (lblCustNew != null) lblCustNew.setText("120");
        if (lblCustReturnRate != null) lblCustReturnRate.setText("68%");
        if (lblCustVip != null) lblCustVip.setText("315");
        if (lblCustLost != null) lblCustLost.setText("89");
    }





    // 3. Sự kiện khi bấm nút Inventory
    @FXML
    public void onInventoryClick(ActionEvent event) {
        // Lôi pane Inventory lên trên cùng
        paneInventory.toFront();
    }

    // 4. Sự kiện khi bấm nút Customer
    @FXML
    public void onCustomerClick(ActionEvent event) {
        // Lôi pane Customer lên trên cùng
        paneCustomer.toFront();
    }
}
