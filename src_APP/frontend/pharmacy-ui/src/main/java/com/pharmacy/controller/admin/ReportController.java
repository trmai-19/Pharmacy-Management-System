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
    @FXML private ComboBox<String> cbInvCustomerType;

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

        if (areaChartCustGrowth == null) return;
        trendAreaChart.setCreateSymbols(false);

        areaChartCustGrowth.getData().clear();

        seriesTotal = new XYChart.Series<>();
        seriesNew = new XYChart.Series<>();
        seriesReturning = new XYChart.Series<>();

        seriesTotal.setName("Tổng khách");
        seriesNew.setName("Khách mới");
        seriesReturning.setName("Khách quay lại");

        String[] months = {
                "Jan","Feb","Mar","Apr","May","Jun",
                "Jul","Aug","Sep","Oct","Nov","Dec"
        };

        int[] totalData = {
                1000,1200,1150,1400,1600,1800,
                1900,2100,2300,2500,2700,3000
        };

        int[] newData = {
                200,300,150,400,350,500,
                520,610,700,760,820,950
        };

        int[] returnData = {
                800,900,1000,1000,1250,1300,
                1380,1490,1600,1740,1880,2050
        };

        for (int i = 0; i < months.length; i++) {

            seriesTotal.getData().add(
                    new XYChart.Data<>(months[i], totalData[i])
            );

            seriesNew.getData().add(
                    new XYChart.Data<>(months[i], newData[i])
            );

            seriesReturning.getData().add(
                    new XYChart.Data<>(months[i], returnData[i])
            );
        }

        switch (currentCustomerMetric) {

            case TOTAL:

                areaChartCustGrowth.getData().add(seriesTotal);

                break;

            case NEW:

                areaChartCustGrowth.getData().add(seriesNew);

                break;

            case RETURNING:

                areaChartCustGrowth.getData().add(seriesReturning);

                break;
        }

        areaChartCustGrowth.applyCss();
        areaChartCustGrowth.layout();

       Platform.runLater(() -> {
            // Dùng chung màu xanh ngọc chủ đạo cho tất cả các line
            String strokeColor = "#00bfa5";
            String fillColor = "rgba(0,191,165,0.18)";

            styleCustomerSeries(seriesTotal, strokeColor, fillColor);
            styleCustomerSeries(seriesNew, strokeColor, fillColor);
            styleCustomerSeries(seriesReturning, strokeColor, fillColor);

            setupCustomerYAxis();
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
        double maxValue = 0;
        for (XYChart.Series<String, Number> s : areaChartCustGrowth.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                maxValue = Math.max(maxValue, d.getYValue().doubleValue());
            }
        }

        double upperBound = roundNiceNumber(maxValue * 1.08);

        // Lấy trục Y trực tiếp từ biểu đồ thay vì dùng biến FXML
        NumberAxis yAxis = (NumberAxis) areaChartCustGrowth.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(upperBound / 6);
    }

    private void loadTopSpendersChart() {
        if (barChartTopSpenders == null) return;
        barChartTopSpenders.getData().clear();

        // 1. Dọn dẹp nền: Ẩn lưới dọc, ngang và các sọc nền so le
        barChartTopSpenders.setHorizontalGridLinesVisible(false);
        barChartTopSpenders.setVerticalGridLinesVisible(false);
        barChartTopSpenders.setAlternativeRowFillVisible(false);
        barChartTopSpenders.setAlternativeColumnFillVisible(false);

        // 2. Format trục X rút gọn thành Triệu (M)
        NumberAxis xAxis = (NumberAxis) barChartTopSpenders.getXAxis();
        xAxis.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object.doubleValue() == 0) return "0.0M";
                double val = object.doubleValue() / 1000000.0;
                // Dùng Locale.US để ra dấu chấm thập phân (ví dụ: 1.5M)
                return String.format(Locale.US, "%.1fM", val); 
            }
            @Override
            public Number fromString(String string) { return null; }
        });

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        series.setName("Tổng chi tiêu");
        series.getData().add(new XYChart.Data<>(1500000, "Lê Văn C"));
        series.getData().add(new XYChart.Data<>(2100000, "Phạm Thị D"));
        series.getData().add(new XYChart.Data<>(2800000, "Hoàng Văn E"));
        series.getData().add(new XYChart.Data<>(3500000, "Vũ Thị F"));
        series.getData().add(new XYChart.Data<>(4200000, "Đặng Văn G"));
        series.getData().add(new XYChart.Data<>(5100000, "Bùi Thị H"));
        series.getData().add(new XYChart.Data<>(6500000, "Ngô Văn I"));
        series.getData().add(new XYChart.Data<>(8200000, "Trần Thị B"));
        series.getData().add(new XYChart.Data<>(10500000, "Nguyễn Văn A"));
        series.getData().add(new XYChart.Data<>(15200000, "Khách VIP 001"));

        barChartTopSpenders.getData().add(series);

        // 3. Tăng độ dày cột một cách tự nhiên
        barChartTopSpenders.setCategoryGap(12); // Khoảng cách giữa các khách hàng (càng nhỏ cột càng to)
        barChartTopSpenders.setBarGap(0);

        Platform.runLater(() -> {
            for (XYChart.Data<Number, String> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setStyle(
                            "-fx-bar-fill: #4db6ac;" +   // Màu xanh dịu mắt giống ảnh mẫu (bạn có thể đổi về #00bfa5 nếu thích)
                            "-fx-border-width: 0;" +     // Xóa bỏ hoàn toàn viền đen
                            "-fx-background-insets: 0;"  // Làm phẳng hoàn toàn
                    );
                }
            }
        });
    }
    private void loadCustomerSegmentationChart() {
        if (barChartCustSeg == null) return;
        barChartCustSeg.getData().clear();

        // Dọn dẹp nền: Ẩn lưới và các sọc nền so le
        barChartCustSeg.setHorizontalGridLinesVisible(false);
        barChartCustSeg.setVerticalGridLinesVisible(false);
        barChartCustSeg.setAlternativeRowFillVisible(false);
        barChartCustSeg.setAlternativeColumnFillVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng thành viên");
        series.getData().add(new XYChart.Data<>("Loyal (Thân thiết)", 1470));
        series.getData().add(new XYChart.Data<>("New (Mới)", 680));
        series.getData().add(new XYChart.Data<>("Lost (Rời bỏ)", 300));

        barChartCustSeg.getData().add(series);

        // Chỉnh độ dày của cột một cách tự nhiên (Số càng nhỏ cột càng to)
        barChartCustSeg.setCategoryGap(40); 

        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setStyle(
                            "-fx-bar-fill: #00bfa5;" +       // Xanh ngọc
                            "-fx-border-width: 0;" +         // Xóa sạch viền đen
                            "-fx-background-insets: 0;"      // Làm phẳng hoàn toàn
                    );
                    // Lệnh node.setScaleX(2.5) đã bị tiễn vong
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
        cbInvCustomerType.setItems(FXCollections.observableArrayList(
                "All Customers", "VIP", "New Customers", "Loyal Customers"
        ));

        // Giá trị mặc định khi vừa mở tab
        cbInvYear.setValue("2026");
        cbInvQuarter.setValue("Quý");
        cbInvProductGroup.setValue("All Product Groups");
        cbInvCustomerType.setValue("All Customers");

        // Đăng ký sự kiện: Chọn Slicer bên tab Inventory thì CHỈ cập nhật dữ liệu Inventory
        cbInvYear.setOnAction(event -> fetchAndLoadInventoryData());
        cbInvQuarter.setOnAction(event -> fetchAndLoadInventoryData());
        cbInvProductGroup.setOnAction(event -> fetchAndLoadInventoryData());
        cbInvCustomerType.setOnAction(event -> fetchAndLoadInventoryData());

        // ===== DEFAULT VALUES =====
        cbYear.setValue("2026");
        cbQuarter.setValue("All Quarters");
        cbProductGroup.setValue("All Product Groups");
        cbCustomerType.setValue("All Customers");

        cbYear.setOnAction(event -> { fetchAndLoadPerformanceData(); fetchAndLoadInventoryData(); });
        cbQuarter.setOnAction(event -> { fetchAndLoadPerformanceData(); fetchAndLoadInventoryData(); });
        cbProductGroup.setOnAction(event -> { fetchAndLoadPerformanceData(); fetchAndLoadInventoryData(); });
        cbCustomerType.setOnAction(event -> { fetchAndLoadPerformanceData(); fetchAndLoadInventoryData(); });
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

        ObservableList<PieChart.Data> pieChartData;

        switch (currentCustomerMetric) {

            case NEW:

                pieChartData =
                        FXCollections.observableArrayList(

                        new PieChart.Data("Nam", 35),
                        new PieChart.Data("Nữ", 65)
                );

                break;

            case RETURNING:

                pieChartData =
                        FXCollections.observableArrayList(

                        new PieChart.Data("Nam", 52),
                        new PieChart.Data("Nữ", 48)
                );

                break;

            default:

                pieChartData =
                        FXCollections.observableArrayList(

                        new PieChart.Data("Nam", 45),
                        new PieChart.Data("Nữ", 55)
                );
        }

        customerGenderPieChart.setData(pieChartData);

        customerGenderPieChart.applyCss();
        customerGenderPieChart.layout();

        String[] colors = {"#00bfa5", "#4C15AB"}; // Dùng màu xanh ngọc chủ đạo
        applyPieChartStyles(customerGenderPieChart, pieChartData, colors);
    }

    private void loadCustomerAgePieChart() {

        ObservableList<PieChart.Data> pieChartData;

        switch (currentCustomerMetric) {

            case NEW:

                pieChartData =
                        FXCollections.observableArrayList(

                        new PieChart.Data("18-24", 40),
                        new PieChart.Data("25-34", 35),
                        new PieChart.Data("35-44", 15),
                        new PieChart.Data("45+", 10)
                );

                break;

            case RETURNING:

                pieChartData =
                        FXCollections.observableArrayList(

                        new PieChart.Data("18-24", 10),
                        new PieChart.Data("25-34", 30),
                        new PieChart.Data("35-44", 40),
                        new PieChart.Data("45+", 20)
                );

                break;

            default:

                pieChartData =
                        FXCollections.observableArrayList(

                        new PieChart.Data("18-24", 15),
                        new PieChart.Data("25-34", 40),
                        new PieChart.Data("35-44", 25),
                        new PieChart.Data("45+", 20)
                );
        }

        customerAgePieChart.setData(pieChartData);

        customerAgePieChart.applyCss();
        customerAgePieChart.layout();

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
        String customerType = (cbInvCustomerType.getValue() != null) ? cbInvCustomerType.getValue() : "All Customers";

        inventoryApiService.fetchInventoryData(year, quarter, productGroup, customerType).thenAccept(data -> {
            Platform.runLater(() -> {
                // 1. Cập nhật các thẻ KPI
                lblInvTotalMeds.setText(data.getTotalMedicines());
                lblInvLowStock.setText(data.getLowStockCount());
                lblInvNearExpiry.setText(data.getNearExpiryCount());
                lblInvExpired.setText(data.getExpiredCount());
                lblInvTotalValue.setText(data.getInventoryValue());

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

                // 3. Cập nhật Biểu đồ tròn
                updateCategoryBarChart(data);

                // 4. Cập nhật Biểu đồ cột
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
}

    
