package com.pharmacy.controller.admin;

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
    @FXML private PieChart pieChartInventory;

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
        loadGenderPieChart(genderPieChart);
        loadCustomerGenderPieChart();
        loadAgePieChart(agePieChart);
        loadCustomerAgePieChart();
        loadInventoryKPIData();
        initInventoryTable();
        loadInventoryBarChart();
        loadInventoryPieChart();
        loadCustomerKPIs();
        initGrowthChartData();
        loadTopSpendersChart();
        loadCustomerSegmentationChart();
        loadPerformanceKPIs();
        initFilters();
        setActivePerformanceButton(btnRevenue);
        setActiveCustomerButton(btnCustTotal);

         // 2. Dùng Platform.runLater để ĐỢI giao diện vẽ xong mới refresh biểu đồ
        Platform.runLater(() -> {
            refreshPerformanceDashboard();
            refreshCustomerDashboard();
            
            // Nếu bạn muốn inventory cũng mượt luôn lúc mới mở thì thêm dòng này:
            loadInventoryPieChart(); 
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

        trendAreaChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.setName("Doanh thu 2026");

        switch (currentMetric) {

            case REVENUE:

                series.setName("Doanh thu 2026");

                series.getData().add(new XYChart.Data<>("T1", 12000000));
                series.getData().add(new XYChart.Data<>("T2", 15000000));
                series.getData().add(new XYChart.Data<>("T3", 11000000));
                series.getData().add(new XYChart.Data<>("T4", 18000000));
                series.getData().add(new XYChart.Data<>("T5", 22000000));
                series.getData().add(new XYChart.Data<>("T6", 21000000));
                series.getData().add(new XYChart.Data<>("T7", 25000000));
                series.getData().add(new XYChart.Data<>("T8", 27000000));
                series.getData().add(new XYChart.Data<>("T9", 24000000));
                series.getData().add(new XYChart.Data<>("T10", 30000000));
                series.getData().add(new XYChart.Data<>("T11", 32000000));
                series.getData().add(new XYChart.Data<>("T12", 35000000));

                break;

            case PROFIT:

                series.setName("Lợi nhuận 2026");

                series.getData().add(new XYChart.Data<>("T1", 8000000));
                series.getData().add(new XYChart.Data<>("T2", 9500000));
                series.getData().add(new XYChart.Data<>("T3", 7000000));
                series.getData().add(new XYChart.Data<>("T4", 12000000));
                series.getData().add(new XYChart.Data<>("T5", 14500000));
                series.getData().add(new XYChart.Data<>("T6", 15000000));
                series.getData().add(new XYChart.Data<>("T7", 17000000));
                series.getData().add(new XYChart.Data<>("T8", 18000000));
                series.getData().add(new XYChart.Data<>("T9", 16500000));
                series.getData().add(new XYChart.Data<>("T10", 20000000));
                series.getData().add(new XYChart.Data<>("T11", 21500000));
                series.getData().add(new XYChart.Data<>("T12", 24000000));

                break;

            case LOSS:

                series.setName("Thua lỗ 2026");

                series.getData().add(new XYChart.Data<>("T1", 2000000));
                series.getData().add(new XYChart.Data<>("T2", 1800000));
                series.getData().add(new XYChart.Data<>("T3", 2500000));
                series.getData().add(new XYChart.Data<>("T4", 2200000));
                series.getData().add(new XYChart.Data<>("T5", 1900000));
                series.getData().add(new XYChart.Data<>("T6", 1700000));
                series.getData().add(new XYChart.Data<>("T7", 2100000));
                series.getData().add(new XYChart.Data<>("T8", 2400000));
                series.getData().add(new XYChart.Data<>("T9", 2000000));
                series.getData().add(new XYChart.Data<>("T10", 1800000));
                series.getData().add(new XYChart.Data<>("T11", 1600000));
                series.getData().add(new XYChart.Data<>("T12", 1500000));

                break;

            case SPEND:

                series.setName("Chi tiêu 2026");

                series.getData().add(new XYChart.Data<>("T1", 5000000));
                series.getData().add(new XYChart.Data<>("T2", 6000000));
                series.getData().add(new XYChart.Data<>("T3", 5800000));
                series.getData().add(new XYChart.Data<>("T4", 6500000));
                series.getData().add(new XYChart.Data<>("T5", 7000000));
                series.getData().add(new XYChart.Data<>("T6", 7500000));
                series.getData().add(new XYChart.Data<>("T7", 8200000));
                series.getData().add(new XYChart.Data<>("T8", 7900000));
                series.getData().add(new XYChart.Data<>("T9", 8300000));
                series.getData().add(new XYChart.Data<>("T10", 9000000));
                series.getData().add(new XYChart.Data<>("T11", 9500000));
                series.getData().add(new XYChart.Data<>("T12", 9800000));

                break;

            case ORDERS:

                series.setName("Đơn hàng 2026");

                series.getData().add(new XYChart.Data<>("T1", 300));
                series.getData().add(new XYChart.Data<>("T2", 350));
                series.getData().add(new XYChart.Data<>("T3", 320));
                series.getData().add(new XYChart.Data<>("T4", 410));
                series.getData().add(new XYChart.Data<>("T5", 480));
                series.getData().add(new XYChart.Data<>("T6", 510));
                series.getData().add(new XYChart.Data<>("T7", 560));
                series.getData().add(new XYChart.Data<>("T8", 590));
                series.getData().add(new XYChart.Data<>("T9", 570));
                series.getData().add(new XYChart.Data<>("T10", 650));
                series.getData().add(new XYChart.Data<>("T11", 700));
                series.getData().add(new XYChart.Data<>("T12", 760));

                break;
        }


        trendAreaChart.getData().add(series);
        NumberAxis yAxis = (NumberAxis) trendAreaChart.getYAxis();

        configureDynamicAxis(yAxis, series);

        trendAreaChart.applyCss();
        trendAreaChart.layout();

        trendAreaChart.applyCss();
        trendAreaChart.layout();    

        Platform.runLater(() -> {

            String strokeColor = getMetricBarColor();

            String fillColor = getMetricAreaFillColor();

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

            // ===== Marker Color =====
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
                }
            }
        });

    }

    private void loadTopProductsBarChart() {

        topProductsBarChart.getData().clear();

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        switch (currentMetric) {

            case REVENUE:

                series.setName("Doanh thu");

                series.getData().add(new XYChart.Data<>("Panadol", 520));
                series.getData().add(new XYChart.Data<>("Vitamin C", 470));
                series.getData().add(new XYChart.Data<>("Khẩu trang", 390));
                series.getData().add(new XYChart.Data<>("Nước muối", 310));
                series.getData().add(new XYChart.Data<>("Bông y tế", 180));

                break;

            case PROFIT:

                series.setName("Lợi nhuận");

                series.getData().add(new XYChart.Data<>("Panadol", 420));
                series.getData().add(new XYChart.Data<>("Vitamin C", 350));
                series.getData().add(new XYChart.Data<>("Khẩu trang", 280));
                series.getData().add(new XYChart.Data<>("Nước muối", 210));
                series.getData().add(new XYChart.Data<>("Bông y tế", 120));

                break;

            case LOSS:

                series.setName("Thua lỗ");

                series.getData().add(new XYChart.Data<>("Thuốc hết hạn", 90));
                series.getData().add(new XYChart.Data<>("Hàng lỗi", 70));
                series.getData().add(new XYChart.Data<>("Hàng hỏng", 50));
                series.getData().add(new XYChart.Data<>("Trả hàng", 35));
                series.getData().add(new XYChart.Data<>("Mất kho", 20));

                break;

            case SPEND:

                series.setName("Chi tiêu");

                series.getData().add(new XYChart.Data<>("Nhập thuốc", 600));
                series.getData().add(new XYChart.Data<>("Marketing", 350));
                series.getData().add(new XYChart.Data<>("Lương nhân viên", 300));
                series.getData().add(new XYChart.Data<>("Vận chuyển", 180));
                series.getData().add(new XYChart.Data<>("Điện nước", 120));

                break;

            case ORDERS:

                series.setName("Đơn hàng");

                series.getData().add(new XYChart.Data<>("Online", 800));
                series.getData().add(new XYChart.Data<>("Tại quầy", 620));
                series.getData().add(new XYChart.Data<>("Giao nhanh", 410));
                series.getData().add(new XYChart.Data<>("Đặt trước", 260));
                series.getData().add(new XYChart.Data<>("Đơn hủy", 90));

                break;
        }

        topProductsBarChart.getData().add(series);
        NumberAxis yAxis =
                (NumberAxis) topProductsBarChart.getYAxis();

        configureDynamicAxis(yAxis, series);

        trendAreaChart.applyCss();
        trendAreaChart.layout();



        Platform.runLater(() -> {

            String color = getMetricBarColor();

            for (XYChart.Data<String, Number> data : series.getData()) {

                Node node = data.getNode();

                if (node != null) {

                    node.setStyle(
                            "-fx-bar-fill: " + color + ";" +
                            "-fx-border-color: #111111;" +
                            "-fx-border-width: 1.2px;" +
                            "-fx-background-radius: 6 6 0 0;"
                    );
                }
            }
        });
    }
    private void loadGenderPieChart(PieChart chart) {
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data("Nam", 45),
                new PieChart.Data("Nữ", 55)
        );
        chart.setData(pieChartData);

        String[] colors = {"#00bfa5", "#4C15AB"};
        applyPieChartStyles(chart, pieChartData, colors);
    }

    private void loadAgePieChart(PieChart chart) {
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data("18-24", 15),
                new PieChart.Data("25-34", 40),
                new PieChart.Data("35-44", 25),
                new PieChart.Data("45+", 20)
        );
        chart.setData(pieChartData);

        String[] colors = {"#00bfa5", "#4C15AB", "#878A94", "#093287"};
        applyPieChartStyles(chart, pieChartData, colors);
    }
        

private void loadInventoryKPIData() {
        int totalMeds = 450;
        int lowStock = 12;
        int nearExpiry = 8;
        int expiredMeds = 5;

        double totalInvValue = 1250000000.0;
        double valueInMillions = totalInvValue / 1000000.0;

        NumberFormat numF = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        NumberFormat curF =
                NumberFormat.getCurrencyInstance(
                        new Locale("vi", "VN")
                );

        lblInvTotalMeds.setText(numF.format(totalMeds));

        lblInvLowStock.setText(numF.format(lowStock));

        lblInvNearExpiry.setText(numF.format(nearExpiry));

        lblInvExpired.setText(numF.format(expiredMeds));

        lblInvTotalValue.setText(numF.format(valueInMillions) + "M");

        setTrendLabel(lblInvTotalTrend, 4.8);
        setTrendLabel(lblInvLowStockTrend, -2.4);
        setTrendLabel(lblInvNearExpiryTrend, -1.2);
        setTrendLabel(lblInvExpiredTrend, -5.7);
        setTrendLabel(lblInvValueTrend, 8.9);
        }

    private void initInventoryTable() {
        colMedId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colMedCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colMedStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colMedExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colMedStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        ObservableList<Medicine> inventoryList = FXCollections.observableArrayList(
            new Medicine("MED001", "Panadol Extra", "Thuốc giảm đau", 150, "12/12/2028", "Bình thường"),
            new Medicine("MED002", "Amoxicillin 500mg", "Thuốc kháng sinh", 8, "15/08/2026", "Sắp hết hàng"),
            new Medicine("MED003", "Vitamin C Celcon", "Thực phẩm chức năng", 80, "01/09/2026", "Cận hạn sử dụng"),
            new Medicine("MED004", "Paracetamol 500mg", "Thuốc giảm đau", 0, "20/10/2027", "Hết hàng"),
            new Medicine("MED005", "Augmentin 1g", "Thuốc kháng sinh", 45, "05/07/2026", "Cận hạn sử dụng")
        );

        if (tableInventory != null) {
            tableInventory.setItems(inventoryList);
        }
    }

    private void loadInventoryBarChart() {
        if (barChartInventory == null) return;
        barChartInventory.getData().clear();

        // Thêm 4 dòng này để tắt toàn bộ lưới dọc, ngang và nền xám so le
        barChartInventory.setHorizontalGridLinesVisible(false);
        barChartInventory.setVerticalGridLinesVisible(false);
        barChartInventory.setAlternativeRowFillVisible(false);
        barChartInventory.setAlternativeColumnFillVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng tồn (Hộp)");
        series.getData().add(new XYChart.Data<>("Panadol Extra", 150));
        series.getData().add(new XYChart.Data<>("Vitamin C", 80));
        series.getData().add(new XYChart.Data<>("Augmentin 1g", 45));
        series.getData().add(new XYChart.Data<>("Amoxicillin", 8));
        series.getData().add(new XYChart.Data<>("Paracetamol", 0));
        
        barChartInventory.getData().add(series);
    }

    private void loadInventoryPieChart() {
        if (pieChartInventory == null) return;
        pieChartInventory.getData().clear();
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Kháng sinh", 40),
                new PieChart.Data("Thuốc giảm đau", 25),
                new PieChart.Data("Vitamin & TPCN", 20),
                new PieChart.Data("Vật tư y tế", 10),
                new PieChart.Data("Khác", 5)
        );
        pieChartInventory.setData(pieChartData);
        pieChartInventory.setLabelsVisible(false);

        String[] colors = {"#00bfa5", "#4C15AB", "#b60000", "#0984e3", "#878A94"};
        applyPieChartStyles(pieChartInventory, pieChartData, colors);
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

        areaChartCustGrowth.getData().clear();

        seriesTotal = new XYChart.Series<>();
        seriesNew = new XYChart.Series<>();
        seriesReturning = new XYChart.Series<>();

        seriesTotal.setName("Tổng khách");
        seriesNew.setName("Khách mới");
        seriesReturning.setName("Khách quay lại");

        String[] months = {
                "T1","T2","T3","T4","T5","T6",
                "T7","T8","T9","T10","T11","T12"
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

        for (XYChart.Series<String, Number> s :
                areaChartCustGrowth.getData()) {

            for (XYChart.Data<String, Number> d :
                    s.getData()) {

                maxValue = Math.max(
                        maxValue,
                        d.getYValue().doubleValue()
                );
            }
        }

        double upperBound = roundNiceNumber(maxValue * 1.08);

        customerGrowthYAxis.setAutoRanging(false);

        customerGrowthYAxis.setLowerBound(0);

        customerGrowthYAxis.setUpperBound(upperBound);

        customerGrowthYAxis.setTickUnit(
                upperBound / 6
        );
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

        loadGenderPieChart(genderPieChart);

        loadGenderPieChart(customerGenderPieChart);

        loadAgePieChart(agePieChart);

        loadAgePieChart(customerAgePieChart);
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
        cbYear.setItems(FXCollections.observableArrayList(
                "Year",
                "2023",
                "2024",
                "2025",
                "2026"
        ));
        cbYear.setPromptText("Year");

        // ===== QUARTER =====
        cbQuarter.setItems(FXCollections.observableArrayList(
                "All Quarters",
                "Q1",
                "Q2",
                "Q3",
                "Q4"
        ));
        cbQuarter.setPromptText("Quarter");

        // ===== PRODUCT GROUP =====
        cbProductGroup.setItems(FXCollections.observableArrayList(
                "All Product Groups",
                "Antibiotics",
                "Painkillers",
                "Vitamins & Supplements",
                "Medical Supplies",
                "Flu Medicine",
                "Digestive Medicine",
                "Cardiovascular",
                "Diabetes"
        ));
        cbProductGroup.setPromptText("Product Group");

        // ===== CUSTOMER TYPE =====
        cbCustomerType.setItems(FXCollections.observableArrayList(
                "All Customers",
                "VIP",
                "New Customers",
                "Loyal Customers",
                "Elderly",
                "Pregnant Women",
                "Children"
        ));
        cbCustomerType.setPromptText("Customer Type");

        // ===== DEFAULT VALUES =====
        cbYear.setValue("2026");
        cbQuarter.setValue("All Quarters");
        cbProductGroup.setValue("All Product Groups");
        cbCustomerType.setValue("All Customers");
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

    @FXML
    private void handleMetricChange(ActionEvent event) {

        Button clicked =
                (Button) event.getSource();

        currentMetric = MetricType.REVENUE;

        if (clicked == btnProfit) {
            currentMetric = MetricType.PROFIT;
        }
        else if (clicked == btnLoss) {
            currentMetric = MetricType.LOSS;
        }
        else if (clicked == btnSpend) {
            currentMetric = MetricType.SPEND;
        }
        else if (clicked == btnOrders) {
            currentMetric = MetricType.ORDERS;
        }

        setActivePerformanceButton(clicked);

        refreshPerformanceDashboard();
    }
}

    
