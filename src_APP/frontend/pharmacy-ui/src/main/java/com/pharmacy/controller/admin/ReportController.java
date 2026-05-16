package com.pharmacy.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

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

    // 2. HÀM KHỞI CHẠY (Chạy ngay khi mở Tab Báo cáo)
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTrendAreaChart();
        loadTopProductsBarChart();
        loadGenderPieChart();
        loadAgePieChart();
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