package com.pharmacy.controller.admin;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label lblGreeting;
    @FXML private Label lblMonthlyRevenue;
    @FXML private Label lblActiveAccounts;
    @FXML private Label lblMonthlyOrders;

    @FXML private LineChart<String, Number> revenueChart;
    @FXML private BarChart<String, Number> topProductsChart;

    @FXML
    public void initialize() {
        System.out.println("📊 Dashboard chính đang được nạp...");

        // 1. Cập nhật lời chào (Sau này bạn có thể truyền tên từ lúc Login vào đây)
        String adminName = "Nguyễn Văn Phát"; // Tên thật thay cho chữ Admin chung chung
        lblGreeting.setText("Chào mừng quay trở lại, " + adminName + "! 👋");

        // 2. Set dữ liệu cho các thẻ thống kê (Mock data)
        lblMonthlyRevenue.setText("185.450.000đ");
        lblActiveAccounts.setText("2,450");
        lblMonthlyOrders.setText("1,128");

        // 3. Nạp dữ liệu vào Biểu đồ Đường (Xu hướng doanh thu)
        setupRevenueChart();

        // 4. Nạp dữ liệu vào Biểu đồ Cột (Top sản phẩm)
        setupTopProductsChart();
    }

    private void setupRevenueChart() {
        revenueChart.getData().clear();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu (Triệu VNĐ)");

        // Thêm các điểm dữ liệu vào biểu đồ đường
        series.getData().add(new XYChart.Data<>("12/04", 15.2));
        series.getData().add(new XYChart.Data<>("13/04", 18.5));
        series.getData().add(new XYChart.Data<>("14/04", 22.1));
        series.getData().add(new XYChart.Data<>("15/04", 19.8));
        series.getData().add(new XYChart.Data<>("16/04", 26.4));
        series.getData().add(new XYChart.Data<>("17/04", 31.0));
        series.getData().add(new XYChart.Data<>("Hôm nay", 28.5));

        revenueChart.getData().add(series);
    }

    private void setupTopProductsChart() {
        topProductsChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng bán ra (Hộp/Vỉ)");

        // Thêm dữ liệu vào biểu đồ cột
        series.getData().add(new XYChart.Data<>("Panadol", 420));
        series.getData().add(new XYChart.Data<>("Vitamin C", 385));
        series.getData().add(new XYChart.Data<>("Oresol", 310));
        series.getData().add(new XYChart.Data<>("Berberin", 240));
        series.getData().add(new XYChart.Data<>("Khẩu trang", 580));

        topProductsChart.getData().add(series);
    }
}