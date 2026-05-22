package com.pharmacy.controller.admin;
import com.pharmacy.util.ReportApiService;
import com.pharmacy.dto.PerformanceReportDTO;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.chart.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.Node;
import javafx.geometry.Pos;

import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import com.pharmacy.model.Medicine;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;




public class ReportController implements Initializable {

    @FXML private AreaChart<String, Number> trendAreaChart;
    @FXML private BarChart<String, Number> topProductsBarChart;
    @FXML private PieChart genderPieChart;
    @FXML private PieChart agePieChart;
    @FXML private PieChart customerGenderPieChart;
    @FXML private PieChart customerAgePieChart;

    @FXML private Button btnRevenue;
    @FXML private Button btnProfit;
    @FXML private Button btnLoss;
    @FXML private Button btnSpend;
    @FXML private Button btnOrders;

    @FXML private AnchorPane panePerformance;
    @FXML private AnchorPane paneInventory;
    @FXML private AnchorPane paneCustomer;

    @FXML private Label lblInvTotalMeds;
    @FXML private Label lblInvLowStock;
    @FXML private Label lblInvNearExpiry;
    @FXML private Label lblInvTotalValue;
    @FXML private Label lblInvExpired;

    @FXML private TableView<Medicine> tableInventory;
    @FXML private TableColumn<Medicine, String> colMedId;
    @FXML private TableColumn<Medicine, String> colMedName;
    @FXML private TableColumn<Medicine, String> colMedCategory;
    @FXML private TableColumn<Medicine, Integer> colMedStock;
    @FXML private TableColumn<Medicine, String> colMedExpiry;
    @FXML private TableColumn<Medicine, String> colMedStatus;

    @FXML private BarChart<String, Number> barChartInventory;
    @FXML private BarChart<String, Number> barChartCategory;

    @FXML private Label lblCustTotal;        
    @FXML private Label lblCustNew;          
    @FXML private Label lblCustReturnRate;   
    @FXML private Label lblCustVip;          
    @FXML private Label lblCustLost;         

    @FXML private AreaChart<String, Number> areaChartCustGrowth;
    @FXML private BarChart<Number, String> barChartTopSpenders;

    @FXML private Label lblOrdersValue;
    @FXML private Label lblProfitValue;
    @FXML private Label lblLossValue;
    @FXML private Label lblSpendValue;
    @FXML private Label lblReturningRateValue;


    @FXML private Label lblOrdersTrend;
    @FXML private Label lblProfitTrend;
    @FXML private Label lblLossTrend;
    @FXML private Label lblSpendTrend;
    @FXML private Label lblReturningRateTrend;

    @FXML private Label lblCustTotalTrend;
    @FXML private Label lblCustNewTrend;
    @FXML private Label lblCustReturnTrend;
    @FXML private Label lblCustVipTrend;
    @FXML private Label lblCustLostTrend;
    

    @FXML private ComboBox<String> cbYear;
    @FXML private ComboBox<String> cbQuarter;
    @FXML private ComboBox<String> cbProductGroup;
    @FXML private ComboBox<String> cbCustomerType;


    // Slicer dành riêng cho tab Inventory
    @FXML private ComboBox<String> cbInvYear;
    @FXML private ComboBox<String> cbInvQuarter;
    @FXML private ComboBox<String> cbInvProductGroup;

    @FXML private ComboBox<String> cbCustYear;
    @FXML private ComboBox<String> cbCustQuarter;
    @FXML private ComboBox<String> cbCustProductGroup;
    @FXML private ComboBox<String> cbCustCustomerType;

    private XYChart.Series<String, Number> seriesTotal = new XYChart.Series<>();
    private XYChart.Series<String, Number> seriesNew = new XYChart.Series<>();
    private XYChart.Series<String, Number> seriesReturning = new XYChart.Series<>();

    @FXML private BarChart<String, Number> barChartCustSeg;

    @FXML private Button btnTabPerformance;
    @FXML private Button btnTabInventory;
    @FXML private Button btnTabCustomer;
    @FXML private NumberAxis customerGrowthYAxis;
    @FXML private Button btnCustTotal;
    @FXML private Button btnCustNew;
    @FXML private Button btnCustReturning;

    @FXML private Label lblInvTotalTrend;
    @FXML private Label lblInvLowStockTrend;
    @FXML private Label lblInvNearExpiryTrend;
    @FXML private Label lblInvExpiredTrend;
    @FXML private Label lblInvValueTrend;
    
    private PerformanceReportDTO currentPerformanceData;
    private com.pharmacy.dto.CustomerReportDTO currentCustomerData;

    private enum MetricType {
        REVENUE,
        PROFIT,
        LOSS,
        SPEND,
        ORDERS
    }
    private enum CustomerMetricType {
        TOTAL,
        NEW,
        RETURNING
    }
    

    private MetricType currentMetric =
        MetricType.REVENUE;
    
    private CustomerMetricType currentCustomerMetric =
        CustomerMetricType.TOTAL;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTrendAreaChart();
        loadTopProductsBarChart();
        loadCustomerGenderPieChart();
        loadCustomerAgePieChart();
        // loadInventoryKPIData();
        initInventoryTable();
        //loadInventoryBarChart();
        //loadInventoryPieChart();
        loadCustomerKPIs();
        initGrowthChartData();
        loadTopSpendersChart();
        loadCustomerSegmentationChart();
        // loadPerformanceKPIs();
        fetchAndLoadPerformanceData();
        fetchAndLoadInventoryData();
        fetchAndLoadCustomerData();
        initFilters();
        setActivePerformanceButton(btnRevenue);
        setActiveCustomerButton(btnCustTotal);

         // 2. Dùng Platform.runLater để ĐỢI giao diện vẽ xong mới refresh biểu đồ
        Platform.runLater(() -> {
            refreshPerformanceDashboard();
            refreshCustomerDashboard();
            
            // Nếu bạn muốn inventory cũng mượt luôn lúc mới mở thì thêm dòng này:
            //loadInventoryPieChart(); 
        });   

        
    }

    
    
    private void setActiveTab(Button activeBtn) {

        btnTabPerformance.getStyleClass()
                .removeAll("dashboard-tab-active");

        btnTabInventory.getStyleClass()
                .removeAll("dashboard-tab-active");

        btnTabCustomer.getStyleClass()
                .removeAll("dashboard-tab-active");

        activeBtn.getStyleClass()
                .add("dashboard-tab-active");
    }

    @FXML
    public void onPerformanceClick(ActionEvent event) {

        panePerformance.toFront();

        setActiveTab(btnTabPerformance);
    }

   
    private void loadTrendAreaChart() {
        if (trendAreaChart == null || currentPerformanceData == null) return;
        trendAreaChart.setAnimated(false); 
        trendAreaChart.setCreateSymbols(false);
        
        trendAreaChart.getData().clear();
        
        trendAreaChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        switch (currentMetric) {
            case REVENUE:
                series.setName("Revenue");
                for (var item : currentPerformanceData.trendData) 
                    series.getData().add(new XYChart.Data<>(item.month, item.revenue));
                break;
            case PROFIT:
                series.setName("Profit");
                for (var item : currentPerformanceData.trendData) 
                    series.getData().add(new XYChart.Data<>(item.month, item.profit));
                break;
            case LOSS:
                series.setName("Loss");
                for (var item : currentPerformanceData.trendData) 
                    series.getData().add(new XYChart.Data<>(item.month, item.loss));
                break;
            case SPEND:
                series.setName("Spend");
                for (var item : currentPerformanceData.trendData) 
                    series.getData().add(new XYChart.Data<>(item.month, item.spend));
                break;
            case ORDERS:
                series.setName("Orders");
                for (var item : currentPerformanceData.trendData) 
                    series.getData().add(new XYChart.Data<>(item.month, item.orders));
                break;
        }

        trendAreaChart.getData().add(series);
        NumberAxis yAxis = (NumberAxis) trendAreaChart.getYAxis();
        configureDynamicAxis(yAxis, series);
        trendAreaChart.applyCss();
        trendAreaChart.layout();

        // Giữ nguyên phần style (màu viền, marker...)
        Platform.runLater(() -> {
            String strokeColor = getMetricBarColor();
            String fillColor = getMetricAreaFillColor();

            Node line = series.getNode().lookup(".chart-series-area-line");
            if (line != null) line.setStyle("-fx-stroke: " + strokeColor + "; -fx-stroke-width: 3px;");

            Node fill = series.getNode().lookup(".chart-series-area-fill");
            if (fill != null) fill.setStyle("-fx-fill: " + fillColor + ";");

            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) node.setStyle("-fx-background-color: white, " + strokeColor + "; -fx-background-insets: 0, 2; -fx-background-radius: 100em; -fx-padding: 5;");
            }
        });
    }
    private void loadTopProductsBarChart() {
        if (topProductsBarChart == null || currentPerformanceData == null) return;

        topProductsBarChart.setAnimated(false);
        CategoryAxis xAxis = (CategoryAxis) topProductsBarChart.getXAxis();
        xAxis.setTickLabelRotation(45);
        
        topProductsBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        switch (currentMetric) {
            case REVENUE:
                series.setName("Revenue");
                for (var item : currentPerformanceData.topProducts) 
                    series.getData().add(new XYChart.Data<>(item.productName, item.revenue));
                break;
            case PROFIT:
                series.setName("Profit");
                for (var item : currentPerformanceData.topProducts) 
                    series.getData().add(new XYChart.Data<>(item.productName, item.profit));
                break;
            case LOSS:
                series.setName("Loss");
                for (var item : currentPerformanceData.topProducts) 
                    series.getData().add(new XYChart.Data<>(item.productName, item.loss));
                break;
            case SPEND:
                series.setName("Spend");
                for (var item : currentPerformanceData.topProducts) 
                    series.getData().add(new XYChart.Data<>(item.productName, item.spend));
                break;
            case ORDERS:
                series.setName("Orders");
                for (var item : currentPerformanceData.topProducts) 
                    series.getData().add(new XYChart.Data<>(item.productName, item.orders));
                break;
        }

        topProductsBarChart.getData().add(series);
        NumberAxis yAxis = (NumberAxis) topProductsBarChart.getYAxis();
        configureDynamicAxis(yAxis, series);

        Platform.runLater(() -> {
            String color = getMetricBarColor();
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setStyle("-fx-bar-fill: " + color + "; -fx-border-color: #111111; -fx-border-width: 1.2px; -fx-background-radius: 6 6 0 0;");
                }
            }
        });
    }

    private void updateGenderPieChart() {
        if (genderPieChart == null || currentPerformanceData == null) return;
        genderPieChart.setLegendSide(javafx.geometry.Side.RIGHT);
        genderPieChart.setPadding(new javafx.geometry.Insets(10, 25, 10, 10)); 
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        for (var item : currentPerformanceData.genderData) {
            pieChartData.add(new PieChart.Data(item.category, item.value));
        }
        
        genderPieChart.setData(pieChartData);
        String[] colors = {"#00bfa5", "#4C15AB"};
        applyPieChartStyles(genderPieChart, pieChartData, colors);
    }

    private void updateAgePieChart() {
        if (agePieChart == null || currentPerformanceData == null) return;

        agePieChart.setLegendSide(javafx.geometry.Side.RIGHT);
        agePieChart.setPadding(new javafx.geometry.Insets(10, 25, 10, 10));
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        for (var item : currentPerformanceData.ageData) {
            pieChartData.add(new PieChart.Data(item.category, item.value));
        }
        
        agePieChart.setData(pieChartData);
        String[] colors = {"#00bfa5", "#4C15AB", "#878A94", "#093287"};
        applyPieChartStyles(agePieChart, pieChartData, colors);
    }   



    private void loadCustomerKPIs() {

        if (lblCustTotal != null)
            lblCustTotal.setText("2,450");

        if (lblCustNew != null)
            lblCustNew.setText("120");

        if (lblCustReturnRate != null)
            lblCustReturnRate.setText("68%");

        if (lblCustVip != null)
            lblCustVip.setText("315");

        if (lblCustLost != null)
            lblCustLost.setText("89");

        // ===== Trend =====

        setTrendLabel(lblCustTotalTrend, 12.4);

        setTrendLabel(lblCustNewTrend, 8.2);

        setTrendLabel(lblCustReturnTrend, 5.8);

        setTrendLabel(lblCustVipTrend, 14.6);

        setTrendLabel(lblCustLostTrend, -6.3);
    }

    private void initGrowthChartData() {
        if (areaChartCustGrowth == null || currentCustomerData == null || currentCustomerData.getCustomerGrowth() == null) return;
        
        // 👇 FIX 1: Tắt Animation và Tắt dấu chấm (Marker) ĐÚNG tên biểu đồ 👇
        areaChartCustGrowth.setAnimated(false);
        areaChartCustGrowth.setCreateSymbols(false);
        CategoryAxis xAxis = (CategoryAxis) areaChartCustGrowth.getXAxis();
        xAxis.setAnimated(false); 
        // 👆 ============================================================== 👆

        areaChartCustGrowth.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        switch (currentCustomerMetric) {
            case TOTAL:
                series.setName("Tổng khách");
                for (var item : currentCustomerData.getCustomerGrowth()) {
                    series.getData().add(new XYChart.Data<>(item.getPeriod(), item.getTotalCustomers()));
                }
                break;
            case NEW:
                series.setName("Khách mới");
                for (var item : currentCustomerData.getCustomerGrowth()) {
                    series.getData().add(new XYChart.Data<>(item.getPeriod(), item.getNewCustomers()));
                }
                break;
            case RETURNING:
                series.setName("Khách quay lại");
                for (var item : currentCustomerData.getCustomerGrowth()) {
                    series.getData().add(new XYChart.Data<>(item.getPeriod(), item.getReturningCustomers()));
                }
                break;
        }

        areaChartCustGrowth.getData().add(series);
        areaChartCustGrowth.applyCss();
        areaChartCustGrowth.layout();

        Platform.runLater(() -> {
            String strokeColor = "#00bfa5";
            String fillColor = "rgba(0,191,165,0.18)";
            styleCustomerSeries(series, strokeColor, fillColor);
            setupCustomerYAxis(); // Gọi hàm chia trục đã được làm gọn ở dưới
        });
    }

    
    private void styleCustomerSeries(
            XYChart.Series<String, Number> series,
            String strokeColor,
            String fillColor
    ) {

        Platform.runLater(() -> {
            if (series.getNode() == null) return;
            Node line = series.getNode()
                    .lookup(".chart-series-area-line");

            if (line != null) {

                line.setStyle(
                        "-fx-stroke: " + strokeColor + ";" +
                        "-fx-stroke-width: 3px;"
                );
            }

            Node fill = series.getNode()
                    .lookup(".chart-series-area-fill");

            if (fill != null) {

                fill.setStyle(
                        "-fx-fill: " + fillColor + ";"
                );
            }

            for (XYChart.Data<String, Number> data : series.getData()) {

                Node node = data.getNode();

                if (node != null) {

                    node.setStyle(
                            "-fx-background-color: white, "
                                    + strokeColor + ";" +

                            "-fx-background-insets: 0, 2;" +

                            "-fx-background-radius: 100em;" +

                            "-fx-padding: 5;"
                    );

                    Tooltip tooltip = new Tooltip(
                            data.getXValue() + "\n"
                                    + data.getYValue()
                    );

                    Tooltip.install(node, tooltip);
                }
            }
        });
    }

    private void setupCustomerYAxis() {
        NumberAxis yAxis = (NumberAxis) areaChartCustGrowth.getYAxis();
        
        // 1. Tự động tính toán lại trục Y dựa trên dữ liệu thật
        yAxis.setAutoRanging(false);
        yAxis.setForceZeroInRange(true);

        double maxValue = 0;
        for (XYChart.Series<String, Number> s : areaChartCustGrowth.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                if (d.getYValue().doubleValue() > maxValue) maxValue = d.getYValue().doubleValue();
            }
        }

        // 2. Làm tròn upperBound lên số đẹp (ví dụ: 100, 150, 200...)
        double upperBound = Math.ceil(maxValue * 1.1 / 10.0) * 10;
        
        // 3. Ép chia đúng 5 khoảng (5 vạch) để số luôn là số chẵn/đẹp
        double tickUnit = upperBound / 5;
        
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(tickUnit);
        
        // 4. Format lại số hiển thị (xóa bỏ phần thập phân .000)
        yAxis.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
            @Override
            public String toString(Number n) {
                return String.format("%.0f", n.doubleValue());
            }
            @Override
            public Number fromString(String s) { return null; }
        });
    }

    private void loadTopSpendersChart() {
        if (barChartTopSpenders == null || currentCustomerData == null || currentCustomerData.getTopSpenders() == null) return;
        barChartTopSpenders.getData().clear();

        barChartTopSpenders.setAnimated(false);
        CategoryAxis yAxis = (CategoryAxis) barChartTopSpenders.getYAxis();
        yAxis.setAnimated(false);

        barChartTopSpenders.getData().clear();

        barChartTopSpenders.setHorizontalGridLinesVisible(false);
        barChartTopSpenders.setVerticalGridLinesVisible(false);
        barChartTopSpenders.setAlternativeRowFillVisible(false);
        barChartTopSpenders.setAlternativeColumnFillVisible(false);

        NumberAxis xAxis = (NumberAxis) barChartTopSpenders.getXAxis();
        xAxis.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object.doubleValue() == 0) return "0.0M";
                return String.format(Locale.US, "%.1fM", object.doubleValue() / 1000000.0); 
            }
            @Override
            public Number fromString(String string) { return null; }
        });

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        series.setName("Tổng chi tiêu");
        
        for (var item : currentCustomerData.getTopSpenders()) {
            series.getData().add(new XYChart.Data<>(item.getValue(), item.getLabel()));
        }

        barChartTopSpenders.getData().add(series);
        barChartTopSpenders.setCategoryGap(12);
        barChartTopSpenders.setBarGap(0);

        Platform.runLater(() -> {
            for (XYChart.Data<Number, String> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setStyle("-fx-bar-fill: #4db6ac; -fx-border-width: 0; -fx-background-insets: 0;");
                }
            }
        });
    }


    private void loadCustomerSegmentationChart() {
        if (barChartCustSeg == null || currentCustomerData == null || currentCustomerData.getCustomerSegmentation() == null) return;
        
        barChartCustSeg.setAnimated(false);
        CategoryAxis xAxis = (CategoryAxis) barChartCustSeg.getXAxis();
        xAxis.setAnimated(false);
        
        barChartCustSeg.getData().clear();

        

        barChartCustSeg.setHorizontalGridLinesVisible(false);
        barChartCustSeg.setVerticalGridLinesVisible(false);
        barChartCustSeg.setAlternativeRowFillVisible(false);
        barChartCustSeg.setAlternativeColumnFillVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng thành viên");
        
        for (var item : currentCustomerData.getCustomerSegmentation()) {
            series.getData().add(new XYChart.Data<>(item.getLabel(), item.getValue()));
        }

        barChartCustSeg.getData().add(series);
        barChartCustSeg.setCategoryGap(40); 

        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setStyle("-fx-bar-fill: #00bfa5; -fx-border-width: 0; -fx-background-insets: 0;");
                }
            }
        });
    }


    private PieChart.Data createPieData(
            String label,
            double value,
            double total
    ) {

        double percent = (value / total) * 100;

        return new PieChart.Data(
                String.format("%s (%.0f%%)", label, percent),
                value
        );
    }

    private void refreshPerformanceDashboard() {
        loadTrendAreaChart();
        loadTopProductsBarChart();
    }

    private String getMetricColor() {
        return "#00bfa5";
    }

    private void setTrendLabel(Label label, double percent) {

        String formatted = String.format(
                "%+.2f%%",
                percent
        );

        label.setText(formatted);

        if (percent >= 0) {

            label.setStyle(
                "-fx-text-fill: #319E21;" +
                "-fx-font-weight: bold;"
            );

        } else {

            label.setStyle(
                "-fx-text-fill: #9E1A00;" +
                "-fx-font-weight: bold;"
            );
        }
    }
    
    private void loadPerformanceKPIs() {

        // ===== VALUE =====
        lblOrdersValue.setText("12,582");
        lblProfitValue.setText("152.4M");
        lblLossValue.setText("18.2M");
        lblSpendValue.setText("64.8M");
        lblReturningRateValue.setText("68%");

        // ===== TREND =====
        setTrendLabel(lblOrdersTrend, 8.42);
        setTrendLabel(lblProfitTrend, 12.15);
        setTrendLabel(lblLossTrend, -4.73);
        setTrendLabel(lblSpendTrend, -2.18);
        setTrendLabel(lblReturningRateTrend, 4.20);
    }

    private void initFilters() {
        // ===== YEAR =====
        cbYear.setItems(FXCollections.observableArrayList("Year", "2023", "2024", "2025", "2026"));
        cbYear.setPromptText("Year");

        // ===== QUARTER =====
        cbQuarter.setItems(FXCollections.observableArrayList("All Quarters", "Q1", "Q2", "Q3", "Q4"));
        cbQuarter.setPromptText("Quarter");

        // ===== PRODUCT GROUP =====
        cbProductGroup.setItems(FXCollections.observableArrayList(
                "All Product Groups", "Thuốc cảm", "Kháng sinh", "Vitamin", 
                "Tiêu hóa", "Tim mạch", "Da liễu", "Xương khớp", "Hô hấp", "Tiểu đường", "Mắt"
        ));
        cbProductGroup.setPromptText("Product Group");

        // ===== CUSTOMER TYPE =====
        cbCustomerType.setItems(FXCollections.observableArrayList(
                "All Customers", "VIP", "New Customers", "Loyal Customers"
        ));
        cbCustomerType.setPromptText("Customer Type");

        // =========================================================
        // NẠP DỮ LIỆU CHO CÁC SLICER CỦA TAB INVENTORY
        // =========================================================
        
        cbInvYear.setItems(FXCollections.observableArrayList("Năm", "2023", "2024", "2025", "2026"));
        cbInvQuarter.setItems(FXCollections.observableArrayList("Quý", "Q1", "Q2", "Q3", "Q4"));
        cbInvProductGroup.setItems(FXCollections.observableArrayList(
                "All Product Groups", "Thuốc cảm", "Kháng sinh", "Vitamin", 
                "Tiêu hóa", "Tim mạch", "Da liễu", "Xương khớp", "Hô hấp", "Tiểu đường", "Mắt"
        ));

        // =========================================================
        // NẠP DỮ LIỆU CHO CÁC SLICER CỦA TAB CUSTOMER
        // =========================================================
        cbCustYear.setItems(FXCollections.observableArrayList("Năm", "2023", "2024", "2025", "2026"));
        cbCustQuarter.setItems(FXCollections.observableArrayList("Quý", "Q1", "Q2", "Q3", "Q4"));
        cbCustProductGroup.setItems(FXCollections.observableArrayList(
                "All Product Groups", "Thuốc cảm", "Kháng sinh", "Vitamin", 
                "Tiêu hóa", "Tim mạch", "Da liễu", "Xương khớp", "Hô hấp", "Tiểu đường", "Mắt"
        ));
        cbCustCustomerType.setItems(FXCollections.observableArrayList(
                "All Customers", "VIP", "New Customers", "Loyal Customers"
        ));

        // Đặt giá trị mặc định khi vừa mở tab
        cbCustYear.setValue("2026");
        cbCustQuarter.setValue("Quý");
        cbCustProductGroup.setValue("All Product Groups");
        cbCustCustomerType.setValue("All Customers");

        // Gắn sự kiện: Chỉ gọi API của Customer khi tương tác với Slicer của Customer
        cbCustYear.setOnAction(event -> fetchAndLoadCustomerData());
        cbCustQuarter.setOnAction(event -> fetchAndLoadCustomerData());
        cbCustProductGroup.setOnAction(event -> fetchAndLoadCustomerData());
        cbCustCustomerType.setOnAction(event -> fetchAndLoadCustomerData());


        // Giá trị mặc định khi vừa mở tab
        cbInvYear.setValue("2026");
        cbInvQuarter.setValue("Quý");
        cbInvProductGroup.setValue("All Product Groups");

        // Đăng ký sự kiện: Chọn Slicer bên tab Inventory thì CHỈ cập nhật dữ liệu Inventory
        cbInvYear.setOnAction(event -> fetchAndLoadInventoryData());
        cbInvQuarter.setOnAction(event -> fetchAndLoadInventoryData());
        cbInvProductGroup.setOnAction(event -> fetchAndLoadInventoryData());

        // ===== DEFAULT VALUES =====
        cbYear.setValue("2026");
        cbQuarter.setValue("All Quarters");
        cbProductGroup.setValue("All Product Groups");
        cbCustomerType.setValue("All Customers");

        cbYear.setOnAction(event -> { fetchAndLoadPerformanceData(); fetchAndLoadInventoryData(); fetchAndLoadCustomerData(); });
        cbQuarter.setOnAction(event -> { fetchAndLoadPerformanceData(); fetchAndLoadInventoryData(); fetchAndLoadCustomerData(); });
        cbProductGroup.setOnAction(event -> { fetchAndLoadPerformanceData(); fetchAndLoadInventoryData(); fetchAndLoadCustomerData(); });
        cbCustomerType.setOnAction(event -> { fetchAndLoadPerformanceData(); fetchAndLoadInventoryData(); fetchAndLoadCustomerData(); });
    }

    private String getMetricBarColor() {
        return "#00bfa5";
    }

    private String getMetricAreaFillColor() {
        return "rgba(0,191,165,0.18)";
    }
    private double roundNiceNumber(double value) {

        double exponent =
                Math.pow(
                        10,
                        Math.floor(
                                Math.log10(value)
                        )
                );

        double fraction = value / exponent;

        double niceFraction;

        if (fraction <= 1) {
            niceFraction = 1;
        }
        else if (fraction <= 1.1) {
            niceFraction = 1.1;
        }
        else if (fraction <= 1.2) {
            niceFraction = 1.2;
        }
        else if (fraction <= 1.25) {
            niceFraction = 1.25;
        }
        else if (fraction <= 1.5) {
            niceFraction = 1.5;
        }
        else if (fraction <= 1.75) {
            niceFraction = 1.75;
        }
        else if (fraction <= 2) {
            niceFraction = 2;
        }
        else if (fraction <= 2.25) {
            niceFraction = 2.25;
        }
        else if (fraction <= 2.5) {
            niceFraction = 2.5;
        }
        else if (fraction <= 3) {
            niceFraction = 3;
        }
        else if (fraction <= 3.5) {
            niceFraction = 3.5;
        }
        else if (fraction <= 4) {
            niceFraction = 4;
        }
        else if (fraction <= 4.5) {
            niceFraction = 4.5;
        }
        else if (fraction <= 5) {
            niceFraction = 5;
        }
        else if (fraction <= 6) {
            niceFraction = 6;
        }
        else if (fraction <= 7) {
            niceFraction = 7;
        }
        else if (fraction <= 7.5) {
            niceFraction = 7.5;
        }
        else if (fraction <= 8) {
            niceFraction = 8;
        }
        else if (fraction <= 9) {
            niceFraction = 9;
        }
        else {
            niceFraction = 10;
        }

        return niceFraction * exponent;
    }

    private void configureDynamicAxis(
            NumberAxis yAxis,
            XYChart.Series<String, Number> series
    ) {

        double maxValue = 0;

        for (XYChart.Data<String, Number> data : series.getData()) {

            double value =
                    data.getYValue().doubleValue();

            if (value > maxValue) {
                maxValue = value;
            }
        }

        // thêm khoảng trống phía trên
        double upperBound = maxValue * 1.03;

        // làm tròn đẹp
        upperBound = roundNiceNumber(upperBound);

        // chia trục thành 5 đoạn
        double tickUnit = upperBound / 5;

        yAxis.setAutoRanging(false);

        yAxis.setLowerBound(0);

        yAxis.setUpperBound(upperBound);

        yAxis.setTickUnit(tickUnit);
    }

    private void loadCustomerGenderPieChart() {
        if (customerGenderPieChart == null || currentCustomerData == null) return;
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        List<com.pharmacy.dto.CustomerReportDTO.ChartData> rawData;

        switch (currentCustomerMetric) {
            case NEW: rawData = currentCustomerData.getGenderNew(); break;
            case RETURNING: rawData = currentCustomerData.getGenderReturning(); break;
            default: rawData = currentCustomerData.getGenderTotal(); break;
        }

        if (rawData != null) {
            for (var item : rawData) {
                pieChartData.add(new PieChart.Data(item.getLabel(), item.getValue()));
            }
        }

        customerGenderPieChart.setData(pieChartData);
        String[] colors = {"#00bfa5", "#4C15AB"};
        applyPieChartStyles(customerGenderPieChart, pieChartData, colors);
    }
    
    private void loadCustomerAgePieChart() {
        if (customerAgePieChart == null || currentCustomerData == null) return;
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        List<com.pharmacy.dto.CustomerReportDTO.ChartData> rawData;

        switch (currentCustomerMetric) {
            case NEW: rawData = currentCustomerData.getAgeNew(); break;
            case RETURNING: rawData = currentCustomerData.getAgeReturning(); break;
            default: rawData = currentCustomerData.getAgeTotal(); break;
        }

        if (rawData != null) {
            for (var item : rawData) {
                pieChartData.add(new PieChart.Data(item.getLabel(), item.getValue()));
            }
        }

        customerAgePieChart.setData(pieChartData);
        String[] colors = {"#00bfa5", "#4C15AB", "#878A94", "#093287"};
        applyPieChartStyles(customerAgePieChart, pieChartData, colors);
    }

    private void refreshCustomerDashboard() {

    initGrowthChartData();

    loadCustomerGenderPieChart();

    loadCustomerAgePieChart();
}

    private void applyPieChartStyles(PieChart chart, ObservableList<PieChart.Data> pieData, String[] colors) {
        chart.applyCss();
        chart.layout();

        // Tính tổng để chia phần trăm
        double total = 0;
        for (PieChart.Data d : pieData) {
            total += d.getPieValue();
        }
        final double finalTotal = total;

        Platform.runLater(() -> {
            int index = 0;
            for (PieChart.Data data : pieData) {
                // 1. Đổi màu miếng bánh (Slice)
                Node node = data.getNode();
                if (node != null && index < colors.length) {
                    node.setStyle("-fx-pie-color: " + colors[index] + ";");
                }

                // 2. Tính và gắn % vào Label (Ví dụ: Nam - 45.00%)
                double percent = (data.getPieValue() / finalTotal) * 100;
                String originalName = data.getName().split(" - ")[0]; // Cắt tên gốc nếu đã format trước đó
                data.setName(String.format("%s (%.1f%%)", originalName, percent).replace(".0%", "%"));

                index++;
            }

            // 3. Đổi màu Chú thích (Legend) - Fix dứt điểm lỗi sai màu
            Set<Node> items = chart.lookupAll("Label.chart-legend-item");
            int legendIndex = 0;
            for (Node item : items) {
                Label label = (Label) item;
                
                // THÊM 2 DÒNG NÀY: Ép size chữ nhỏ lại và cho phép text bẻ dòng nếu thiếu chỗ
                label.setStyle("-fx-font-size: 11px; -fx-text-fill: #444444;");
                label.setWrapText(true);

                Node symbol = label.getGraphic();
                if (symbol != null && legendIndex < colors.length) {
                    symbol.setStyle("-fx-background-color: " + colors[legendIndex] + ";");
                }
                legendIndex++;
            }
        });
    }

    


    
    @FXML
    public void onInventoryClick(ActionEvent event) {
        paneInventory.toFront();
        

        setActiveTab(btnTabInventory);
    }

    @FXML
    public void onCustomerClick(ActionEvent event) {

        paneCustomer.toFront();

        setActiveTab(btnTabCustomer);
    }

    private void setActiveCustomerButton(Button activeBtn) {

        btnCustTotal.getStyleClass()
                .remove("customer-metric-active");

        btnCustNew.getStyleClass()
                .remove("customer-metric-active");

        btnCustReturning.getStyleClass()
                .remove("customer-metric-active");

        activeBtn.getStyleClass()
                .add("customer-metric-active");
    }

    private void setActivePerformanceButton(Button activeBtn) {
        // Xóa class active khỏi tất cả 5 nút của thanh Performance
        btnRevenue.getStyleClass().remove("performance-metric-active");
        btnProfit.getStyleClass().remove("performance-metric-active");
        btnLoss.getStyleClass().remove("performance-metric-active");
        btnSpend.getStyleClass().remove("performance-metric-active");
        btnOrders.getStyleClass().remove("performance-metric-active");

        // Thêm class active vào nút vừa được click
        activeBtn.getStyleClass().add("performance-metric-active");
    }

    private void fetchAndLoadPerformanceData() {
        String year = (cbYear.getValue() != null) ? cbYear.getValue() : "2026";
        String quarter = (cbQuarter.getValue() != null) ? cbQuarter.getValue() : "All Quarters";
        String productGroup = (cbProductGroup.getValue() != null) ? cbProductGroup.getValue() : "All Product Groups";
        String customerType = (cbCustomerType.getValue() != null) ? cbCustomerType.getValue() : "All Customers";
        String metric = (currentMetric != null) ? currentMetric.name() : "REVENUE";
        
        reportApiService.fetchPerformanceData(year, quarter, productGroup, customerType, metric).thenAccept(data -> {
            Platform.runLater(() -> {
                this.currentPerformanceData = data;

                // Cập nhật thẻ KPI
                lblOrdersValue.setText(data.kpis.totalOrders);
                setTrendLabel(lblOrdersTrend, data.kpis.ordersTrend);
                lblProfitValue.setText(data.kpis.profit);
                setTrendLabel(lblProfitTrend, data.kpis.profitTrend);
                lblLossValue.setText(data.kpis.loss);
                setTrendLabel(lblLossTrend, data.kpis.lossTrend);
                lblSpendValue.setText(data.kpis.spend);
                setTrendLabel(lblSpendTrend, data.kpis.spendTrend);
                lblReturningRateValue.setText(data.kpis.returningRate);
                setTrendLabel(lblReturningRateTrend, data.kpis.returningRateTrend);
                
                // Đổ dữ liệu vào tất cả biểu đồ (Cả biểu đồ tròn giờ đã nhận dữ liệu động theo bộ lọc)
                updateGenderPieChart();
                updateAgePieChart();
                loadTrendAreaChart();
                loadTopProductsBarChart();
            });
        }).exceptionally(ex -> {
            System.err.println("Lỗi khi fetch API: " + ex.getMessage());
            return null;
        });
    }

    // 3. Thay thế hàm handleMetricChange() để bắt ép biểu đồ tròn phải load lại dữ liệu từ DB khi đổi nút bấm
    @FXML
    private void handleMetricChange(ActionEvent event) {
        Button clicked = (Button) event.getSource();
        currentMetric = MetricType.REVENUE;

        if (clicked == btnProfit) currentMetric = MetricType.PROFIT;
        else if (clicked == btnLoss) currentMetric = MetricType.LOSS;
        else if (clicked == btnSpend) currentMetric = MetricType.SPEND;
        else if (clicked == btnOrders) currentMetric = MetricType.ORDERS;

        setActivePerformanceButton(clicked);
        
        // Gọi lại hàm nạp dữ liệu toàn diện thay vì vẽ lại cục bộ
        fetchAndLoadPerformanceData(); 
    }
@FXML
private void handleCustomerMetricChange(ActionEvent event) {

    Button clicked = (Button) event.getSource();

    currentCustomerMetric =
            CustomerMetricType.TOTAL;

    if (clicked == btnCustNew) {

        currentCustomerMetric =
                CustomerMetricType.NEW;
    }
    else if (clicked == btnCustReturning) {

        currentCustomerMetric =
                CustomerMetricType.RETURNING;
    }

    setActiveCustomerButton(clicked);

    refreshCustomerDashboard();
}

    private final ReportApiService reportApiService = new ReportApiService();
    private final com.pharmacy.util.InventoryApiService inventoryApiService = new com.pharmacy.util.InventoryApiService();


    // =========================================================================
    // HỆ THỐNG DỮ LIỆU THẬT CHO TAB INVENTORY
    // =========================================================================
    private void fetchAndLoadInventoryData() {
        String year = (cbInvYear.getValue() != null) ? cbInvYear.getValue() : "Năm";
        String quarter = (cbInvQuarter.getValue() != null) ? cbInvQuarter.getValue() : "Quý";
        String productGroup = (cbInvProductGroup.getValue() != null) ? cbInvProductGroup.getValue() : "All Product Groups";

        inventoryApiService.fetchInventoryData(year, quarter, productGroup).thenAccept(data -> {
            Platform.runLater(() -> {
                // 1. Cập nhật các thẻ KPI
                lblInvTotalMeds.setText(data.getTotalMedicines());
                lblInvLowStock.setText(data.getLowStockCount());
                lblInvNearExpiry.setText(data.getNearExpiryCount());
                lblInvExpired.setText(data.getExpiredCount());
                lblInvTotalValue.setText(data.getInventoryValue());

                // 👇 THÊM PHẦN CẬP NHẬT TREND VÀO NGAY ĐÂY 👇
                setTrendLabel(lblInvTotalTrend, data.getTotalMedicinesTrend() != null ? data.getTotalMedicinesTrend() : 0.0);
                setTrendLabel(lblInvLowStockTrend, data.getLowStockTrend() != null ? data.getLowStockTrend() : 0.0);
                setTrendLabel(lblInvNearExpiryTrend, data.getNearExpiryTrend() != null ? data.getNearExpiryTrend() : 0.0);
                setTrendLabel(lblInvExpiredTrend, data.getExpiredTrend() != null ? data.getExpiredTrend() : 0.0);
                setTrendLabel(lblInvValueTrend, data.getInventoryValueTrend() != null ? data.getInventoryValueTrend() : 0.0);
                // 👆 KẾT THÚC PHẦN THÊM MỚI 👆

                // 2. Cập nhật Bảng TableView (Ép kiểu từ DTO sang Model Medicine của giao diện)
                ObservableList<Medicine> inventoryList = FXCollections.observableArrayList();
                for (com.pharmacy.dto.InventoryReportDTO.MedicineRow row : data.getTableData()) {
                    inventoryList.add(new Medicine(
                        row.getMaThuoc(),
                        row.getTenThuoc(),
                        row.getPhanLoai(),
                        row.getTonKho().intValue(),
                        row.getHanSuDung(),
                        row.getTinhTrang()
                    ));
                }
                tableInventory.setItems(inventoryList);

                // 3. Cập nhật Biểu đồ cột danh mục (đã đổi từ Pie Chart)
                updateCategoryBarChart(data);

                // 4. Cập nhật Biểu đồ cột Top Moving
                updateInventoryBarChart(data);
            });
        }).exceptionally(ex -> {
            System.err.println("Lỗi khi fetch API Inventory: " + ex.getMessage());
            return null;
        });
    }


    private void updateCategoryBarChart(com.pharmacy.dto.InventoryReportDTO data) {
        if (barChartCategory == null || data.getCategoryDistribution() == null) return;
        
        barChartCategory.setAnimated(false);
        barChartCategory.getData().clear();
        
        // Dọn dẹp lưới nền cho sạch sẽ
        barChartCategory.setHorizontalGridLinesVisible(false);
        barChartCategory.setVerticalGridLinesVisible(false);
        barChartCategory.setAlternativeRowFillVisible(false);
        barChartCategory.setAlternativeColumnFillVisible(false);
        barChartCategory.setLegendVisible(false); // Ẩn legend vì tên đã nằm ở trục X

        // Xoay label trục X chéo 45 độ để chữ không bị đè nhau
        CategoryAxis xAxis = (CategoryAxis) barChartCategory.getXAxis();
        xAxis.setTickLabelRotation(45);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng");
        
        for (var c : data.getCategoryDistribution()) {
            series.getData().add(new XYChart.Data<>(c.getLabel(), c.getValue()));
        }
        
        barChartCategory.getData().add(series);

        // Đổ bảng màu đa dạng cho các cột (giữ lại các màu cũ của Pie Chart)
        Platform.runLater(() -> {
            String[] colors = {"#00bfa5", "#4C15AB", "#b60000", "#0984e3", "#878A94", "#f39c12", "#8e44ad", "#27ae60", "#d35400", "#2c3e50"};
            int index = 0;
            for (XYChart.Data<String, Number> d : series.getData()) {
                Node node = d.getNode();
                if (node != null) {
                    String color = colors[index % colors.length];
                    // Bo góc trên của cột cho mềm mại
                    node.setStyle("-fx-bar-fill: " + color + "; -fx-border-width: 0; -fx-background-radius: 4 4 0 0;");
                }
                index++;
            }
        });
    }

    private void updateInventoryBarChart(com.pharmacy.dto.InventoryReportDTO data) {
        if (barChartInventory == null || data.getSlowestMoving() == null) return;
        
        barChartInventory.setAnimated(false);
        barChartInventory.getData().clear();
        barChartInventory.setHorizontalGridLinesVisible(false);
        barChartInventory.setVerticalGridLinesVisible(false);
        barChartInventory.setAlternativeRowFillVisible(false);
        barChartInventory.setAlternativeColumnFillVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng tồn (Hộp)");
        
        for (var s : data.getSlowestMoving()) {
            series.getData().add(new XYChart.Data<>(s.getLabel(), s.getValue()));
        }
        
        barChartInventory.getData().add(series);

        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> d : series.getData()) {
                Node node = d.getNode();
                if (node != null) {
                    node.setStyle("-fx-bar-fill: #00bfa5; -fx-border-width: 0;");
                }
            }
        });
    }

    // Dọn dẹp lại hàm khởi tạo bảng (chỉ giữ cấu trúc cột, xóa data giả)
    private void initInventoryTable() {
        colMedId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colMedCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colMedStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colMedExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colMedStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Đã xóa phần ObservableList<Medicine> chứa data giả Panadol, Amoxicillin...
    }

    private void fetchAndLoadCustomerData() {
        String year = (cbCustYear.getValue() != null && !cbCustYear.getValue().equals("Năm")) ? cbCustYear.getValue() : "2026";
        String quarter = (cbCustQuarter.getValue() != null && !cbCustQuarter.getValue().equals("Quý")) ? cbCustQuarter.getValue() : "All Quarters";
        String productGroup = (cbCustProductGroup.getValue() != null) ? cbCustProductGroup.getValue() : "All Product Groups";
        String customerType = (cbCustCustomerType.getValue() != null) ? cbCustCustomerType.getValue() : "All Customers";

        // Giả định hàm này đã được định nghĩa trong ReportApiService giống như Performance
        reportApiService.fetchCustomerData(year, quarter, productGroup, customerType).thenAccept(data -> {
            Platform.runLater(() -> {
                this.currentCustomerData = data;

                // 1. Cập nhật giá trị hiển thị lên 5 thẻ KPI chính
                lblCustTotal.setText(data.getTotalCustomers());
                lblCustNew.setText(data.getNewCustomers());
                lblCustReturnRate.setText(data.getReturnRate());
                lblCustVip.setText(data.getVipCustomers());
                lblCustLost.setText(data.getLostCustomers());

                // 2. Cập nhật phần trăm Trend (+/-) tương ứng kèm màu sắc tự động
                setTrendLabel(lblCustTotalTrend, data.getTotalCustomersTrend() != null ? data.getTotalCustomersTrend() : 0.0);
                setTrendLabel(lblCustNewTrend, data.getNewCustomersTrend() != null ? data.getNewCustomersTrend() : 0.0);
                setTrendLabel(lblCustReturnTrend, data.getReturnRateTrend() != null ? data.getReturnRateTrend() : 0.0);
                setTrendLabel(lblCustVipTrend, data.getVipCustomersTrend() != null ? data.getVipCustomersTrend() : 0.0);
                setTrendLabel(lblCustLostTrend, data.getLostCustomersTrend() != null ? data.getLostCustomersTrend() : 0.0);

                // 3. Đổ dữ liệu vào 2 biểu đồ dạng tĩnh (Không đổi theo nút bấm)
                loadTopSpendersChart();
                loadCustomerSegmentationChart();

                // 4. Đồng bộ hóa lại các biểu đồ phân tích sâu (Growth, Gender, Age)
                refreshCustomerDashboard();
            });
        }).exceptionally(ex -> {
            System.err.println("Lỗi khi fetch API Customer: " + ex.getMessage());
            return null;
        });
    }
}

    
