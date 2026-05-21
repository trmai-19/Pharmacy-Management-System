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
    @FXML private ComboBox<String> cbMonth;
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

    chart.applyCss();
    chart.layout();

    Platform.runLater(() -> {

        String[] colors = {
                "#23B07E",
                "#4C15AB"
        };

        // ===== PIE SLICE =====
        for (int i = 0; i < pieChartData.size(); i++) {

            PieChart.Data data = pieChartData.get(i);

            Node node = data.getNode();

            if (node != null) {

                node.setStyle(
                        "-fx-pie-color: " + colors[i] + ";"
                );
            }
        }

        // ===== LEGEND COLORS =====
        Set<Node> items =
                chart.lookupAll(
                        "Label.chart-legend-item"
                );

        int index = 0;

        for (Node item : items) {

            Label label = (Label) item;

            Node symbol = label.getGraphic();

            if (symbol != null && index < colors.length) {

                symbol.setStyle(
                        "-fx-background-color: "
                                + colors[index] + ";"
                );
            }

            index++;
        }
    });
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

        chart.applyCss();
        chart.layout();

        Platform.runLater(() -> {

            String[] colors = {
                    "#23B07E",
                    "#4C15AB",
                    "#878A94",
                    "#093287"
            };

            // ===== PIE SLICE =====
            for (int i = 0; i < pieChartData.size(); i++) {

                PieChart.Data data = pieChartData.get(i);

                Node node = data.getNode();

                if (node != null) {

                    node.setStyle(
                            "-fx-pie-color: " + colors[i] + ";"
                    );
                }
            }

            // ===== LEGEND COLORS =====
            Set<Node> items =
                    chart.lookupAll(
                            "Label.chart-legend-item"
                    );

            int index = 0;

            for (Node item : items) {

                Label label = (Label) item;

                Node symbol = label.getGraphic();

                if (symbol != null && index < colors.length) {

                    symbol.setStyle(
                            "-fx-background-color: "
                                    + colors[index] + ";"
                    );
                }

                index++;
            }
        });
    }
        

    private void loadInventoryKPIData() {
        int totalMeds = 450;
        int lowStock = 12;
        int nearExpiry = 8;
        double totalInvValue = 1250000000.0;

        NumberFormat numF = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        NumberFormat curF = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        if(lblInvTotalMeds != null) lblInvTotalMeds.setText(numF.format(totalMeds));
        if(lblInvLowStock != null) lblInvLowStock.setText(numF.format(lowStock));
        if(lblInvNearExpiry != null) lblInvNearExpiry.setText(numF.format(nearExpiry));
        if(lblInvTotalValue != null) lblInvTotalValue.setText(curF.format(totalInvValue).replace("₫", "VNĐ"));
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

            styleCustomerSeries(
                    seriesTotal,
                    "#F5B700",
                    "rgba(245,183,0,0.18)"
            );

            styleCustomerSeries(
                    seriesNew,
                    "#00B894",
                    "rgba(0,184,148,0.18)"
            );

            styleCustomerSeries(
                    seriesReturning,
                    "#6C5CE7",
                    "rgba(108,92,231,0.18)"
            );

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
        XYChart.Series<Number, String> series = new XYChart.Series<>();
        series.setName("Tổng chi tiêu (VNĐ)");
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

        barChartTopSpenders.setCategoryGap(12);
        barChartTopSpenders.setBarGap(2);  
        
        Platform.runLater(() -> {
        for (XYChart.Data<Number, String> data : series.getData()) {

            Node node = data.getNode();

            if (node != null) {

                node.setStyle(
                        "-fx-bar-fill: #14B8A6;" +
                        "-fx-background-radius: 8;"
                );

                // tăng chiều cao bar
                node.setScaleY(2.5);
            }
        }
    });
    }

    private void loadCustomerSegmentationChart() {
        if (barChartCustSeg == null) 
            return;

        barChartCustSeg.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng thành viên");
        series.getData().add(new XYChart.Data<>("Loyal (Thân thiết)", 1470));
        series.getData().add(new XYChart.Data<>("New (Mới)", 680));
        series.getData().add(new XYChart.Data<>("Lost (Rời bỏ)", 300));

        barChartCustSeg.getData().add(series);
        barChartCustSeg.setCategoryGap(60);
        barChartCustSeg.setBarGap(18);

        Platform.runLater(() -> {
            String[] colors = {
                    "#14B8A6", // Loyal
                    "#EAB308", // New
                    "#DC2626"  // Lost
            };

            int index = 0;

            for (XYChart.Data<String, Number> data : series.getData()) {

                Node node = data.getNode();

                if (node != null) {

                    node.setStyle(
                            "-fx-bar-fill: " + colors[index] + ";" +
                            "-fx-background-radius: 0 0 0 0;"
                    );

                    node.setScaleX(2.5);
                }

                index++;
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

        switch (currentMetric) {

            case PROFIT:
                return "#00b894";

            case LOSS:
                return "#d63031";

            case SPEND:
                return "#e17055";

            case ORDERS:
                return "#0984e3";

            default:
                return "#00bfa5";
        }
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
                "2023",
                "2024",
                "2025",
                "2026"
        ));

        // ===== MONTH =====
        cbMonth.setItems(FXCollections.observableArrayList(
                "Tất cả tháng",
                "Tháng 1",
                "Tháng 2",
                "Tháng 3",
                "Tháng 4",
                "Tháng 5",
                "Tháng 6",
                "Tháng 7",
                "Tháng 8",
                "Tháng 9",
                "Tháng 10",
                "Tháng 11",
                "Tháng 12"
        ));

        // ===== QUARTER =====
        cbQuarter.setItems(FXCollections.observableArrayList(
                "Tất cả quý",
                "Quý 1",
                "Quý 2",
                "Quý 3",
                "Quý 4"
        ));

        // ===== PRODUCT GROUP =====
        cbProductGroup.setItems(FXCollections.observableArrayList(
                "Tất cả nhóm",
                "Thuốc kháng sinh",
                "Thuốc giảm đau",
                "Vitamin & TPCN",
                "Vật tư y tế",
                "Thuốc cảm cúm",
                "Thuốc tiêu hóa",
                "Thuốc tim mạch",
                "Thuốc tiểu đường"
        ));

        // ===== CUSTOMER TYPE =====
        cbCustomerType.setItems(FXCollections.observableArrayList(
                "Tất cả khách hàng",
                "Khách VIP",
                "Khách mới",
                "Khách thân thiết",
                "Người cao tuổi",
                "Phụ nữ mang thai",
                "Trẻ em"
        ));

        // ===== DEFAULT VALUES =====
        cbYear.setValue("2026");
        cbMonth.setValue("Tất cả tháng");
        cbQuarter.setValue("Tất cả quý");
        cbProductGroup.setValue("Tất cả nhóm");
        cbCustomerType.setValue("Tất cả khách hàng");
    }

    private String getMetricBarColor() {

    switch (currentMetric) {

            case REVENUE:
                return "#E0CE2B";

            case PROFIT:
                return "#A4E02B";

            case LOSS:
                return "#E02B2B";

            case SPEND:
                return "#2B55E0";

            case ORDERS:
                return "#5E0482";

            default:
                return "#2BE0A1";
        }
    }

    private String getMetricAreaFillColor() {

    switch (currentMetric) {

            case REVENUE:
                return "rgba(245,158,11,0.18)";

            case PROFIT:
                return "rgba(78, 185, 16, 0.18)";

            case LOSS:
                return "rgba(220,38,38,0.18)";

            case SPEND:
                return "rgba(59,130,246,0.18)";

            case ORDERS:
                return "rgba(139,92,246,0.18)";

            default:
                return "rgba(0,191,165,0.18)";
        }
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

        Platform.runLater(() -> {

            String[] colors = {
                    "#23B07E",
                    "#4C15AB"
            };

            for (int i = 0; i < pieChartData.size(); i++) {

                Node node =
                        pieChartData.get(i).getNode();

                if (node != null) {

                    node.setStyle(
                            "-fx-pie-color: "
                                    + colors[i] + ";"
                    );
                }
            }
        });
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

        Platform.runLater(() -> {

            String[] colors = {
                    "#23B07E",
                    "#4C15AB",
                    "#878A94",
                    "#093287"
            };

            for (int i = 0; i < pieChartData.size(); i++) {

                Node node =
                        pieChartData.get(i).getNode();

                if (node != null) {

                    node.setStyle(
                            "-fx-pie-color: "
                                    + colors[i] + ";"
                    );
                }
            }
        });
    }

    private void refreshCustomerDashboard() {

    initGrowthChartData();

    loadCustomerGenderPieChart();

    loadCustomerAgePieChart();
}

    


    
    @FXML
    public void onInventoryClick(ActionEvent event) {

        paneInventory.setStyle("-fx-background-color: red;");
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

        refreshPerformanceDashboard();
    }
}

    
