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
import com.pharmacy.model.Medicine;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.Button;



public class ReportController implements Initializable {

    @FXML private AreaChart<String, Number> trendAreaChart;
    @FXML private BarChart<String, Number> topProductsBarChart;
    @FXML private PieChart genderPieChart;
    @FXML private PieChart agePieChart;

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
    @FXML private CheckBox chkTotal;
    @FXML private CheckBox chkNew;
    @FXML private CheckBox chkReturning;
    @FXML private BarChart<Number, String> barChartTopSpenders;

    private XYChart.Series<String, Number> seriesTotal = new XYChart.Series<>();
    private XYChart.Series<String, Number> seriesNew = new XYChart.Series<>();
    private XYChart.Series<String, Number> seriesReturning = new XYChart.Series<>();

    @FXML private BarChart<String, Number> barChartCustSeg;

    private enum MetricType {
        REVENUE,
        PROFIT,
        LOSS,
        SPEND,
        ORDERS
    }

    private MetricType currentMetric =
        MetricType.REVENUE;

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
        initGrowthChartData();
        loadTopSpendersChart();
        loadCustomerSegmentationChart();

    }

    @FXML
    public void onPerformanceClick(ActionEvent event) {
        panePerformance.toFront();
    }

    private void loadTrendAreaChart() {

        trendAreaChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.setName("Doanh thu 2026");

        series.getData().add(new XYChart.Data<>("Tháng 1", 12000000));
        series.getData().add(new XYChart.Data<>("Tháng 2", 15000000));
        series.getData().add(new XYChart.Data<>("Tháng 3", 11000000));
        series.getData().add(new XYChart.Data<>("Tháng 4", 18000000));
        series.getData().add(new XYChart.Data<>("Tháng 5", 22000000));

        trendAreaChart.getData().add(series);
        trendAreaChart.applyCss();
        trendAreaChart.layout();    

        Platform.runLater(() -> {

            panePerformance.getChildren().removeIf(
                node -> node instanceof Label
                        && node.getStyleClass()
                            .contains("chart-point-label")
            );

            for (XYChart.Data<String, Number> dt : series.getData()) {

                Node node = dt.getNode();

                if (node != null) {
                    Label label = new Label(
                            (dt.getYValue().intValue() / 1000000) + "M"
                    );

                    label.getStyleClass().add("chart-point-label");

                    panePerformance.getChildren().add(label);

                    Bounds bounds = node.localToScene(
                            node.getBoundsInLocal()
                    );

                    Bounds paneBounds = panePerformance.localToScene(
                            panePerformance.getBoundsInLocal()
                    );

                    double x =
                            bounds.getMinX()
                            - paneBounds.getMinX()
                            + (bounds.getWidth() / 2);

                    label.applyCss();
                    label.layout();

                    x -= label.getWidth() / 2;
                    double y =
                            bounds.getMinY()
                            - paneBounds.getMinY()
                            - 28;

                    label.setLayoutX(x);
                    label.setLayoutY(y);
                }
            }
        });
    }

    private void loadTopProductsBarChart() {
        topProductsBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng bán");

        series.getData().add(new XYChart.Data<>("Panadol", 500));
        series.getData().add(new XYChart.Data<>("Vitamin C", 420));
        series.getData().add(new XYChart.Data<>("Khẩu trang", 350));
        series.getData().add(new XYChart.Data<>("Nước muối", 300));
        series.getData().add(new XYChart.Data<>("Bông y tế", 150));

        topProductsBarChart.getData().add(series);
        topProductsBarChart.applyCss();
        topProductsBarChart.layout();
        
        Platform.runLater(() -> {

            String color = getMetricColor();

            for (XYChart.Data<String, Number> data : series.getData()) {

                Node node = data.getNode();

                if (node != null) {

                    node.setStyle(
                            "-fx-bar-fill: " + color + ";" +
                            "-fx-border-color: #111111;" +
                            "-fx-border-width: 1px;"
                    );
                }
            }
        });
    }

    private void loadGenderPieChart() {

        double male = 45;
        double female = 55;

        double total = male + female;

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(

                createPieData("Nam", male, total),
                createPieData("Nữ", female, total)
        );

        genderPieChart.setData(pieChartData);
    }

    private void loadAgePieChart() {

        double g1 = 15;
        double g2 = 40;
        double g3 = 25;
        double g4 = 20;

        double total = g1 + g2 + g3 + g4;

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(

                createPieData("18-24", g1, total),
                createPieData("25-34", g2, total),
                createPieData("35-44", g3, total),
                createPieData("45+", g4, total)
        );

        agePieChart.setData(pieChartData);
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
        if (lblCustTotal != null) lblCustTotal.setText("2,450");
        if (lblCustNew != null) lblCustNew.setText("120");
        if (lblCustReturnRate != null) lblCustReturnRate.setText("68%");
        if (lblCustVip != null) lblCustVip.setText("315");
        if (lblCustLost != null) lblCustLost.setText("89");
    }

    private void initGrowthChartData() {
        if (areaChartCustGrowth == null) return;

        seriesTotal.setName("Tổng khách");
        seriesNew.setName("Khách mới");
        seriesReturning.setName("Khách quay lại");

        String[] months = {"T1", "T2", "T3", "T4", "T5", "T6"};
        int[] totalData = {1000, 1200, 1150, 1400, 1600, 1800};
        int[] newData = {200, 300, 150, 400, 350, 500};
        int[] returnData = {800, 900, 1000, 1000, 1250, 1300};

        for (int i = 0; i < 6; i++) {
            seriesTotal.getData().add(new XYChart.Data<>(months[i], totalData[i]));
            seriesNew.getData().add(new XYChart.Data<>(months[i], newData[i]));
            seriesReturning.getData().add(new XYChart.Data<>(months[i], returnData[i]));
        }

        chkTotal.setSelected(true);
        areaChartCustGrowth.getData().add(seriesTotal);

        Platform.runLater(() -> {
            paneCustomer.getChildren().removeIf(
                node -> node instanceof Label
                        && node.getStyleClass().contains("chart-point-label")
            );

            XYChart.Series<String, Number>[] allSeries =
                    new XYChart.Series[]{
                            seriesTotal,
                            seriesNew,
                            seriesReturning
                    };

            for (XYChart.Series<String, Number> s : allSeries) {

                if (s == null) continue;

                for (XYChart.Data<String, Number> dt : s.getData()) {

                    Node node = dt.getNode();

                    if (node != null) {

                        Label label = new Label(
                                dt.getYValue().toString()
                        );

                        label.getStyleClass()
                            .add("chart-point-label");

                        paneCustomer.getChildren().add(label);

                        Bounds bounds = node.localToScene(
                                node.getBoundsInLocal()
                        );

                        Bounds paneBounds = paneCustomer.localToScene(
                                paneCustomer.getBoundsInLocal()
                        );

                        double x =
                                bounds.getMinX()
                                - paneBounds.getMinX()
                                + (bounds.getWidth() / 2);

                        label.applyCss();
                        label.layout();

                        x -= label.getWidth() / 2;
                        double y =
                                bounds.getMinY()
                                - paneBounds.getMinY()
                                - 28;

                        label.setLayoutX(x);
                        label.setLayoutY(y);
                    }
                }
            }
        });
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
    }

    private void loadCustomerSegmentationChart() {
        if (barChartCustSeg == null) return;
        barChartCustSeg.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng thành viên");
        series.getData().add(new XYChart.Data<>("Loyal (Thân thiết)", 1470));
        series.getData().add(new XYChart.Data<>("New (Mới)", 680));
        series.getData().add(new XYChart.Data<>("Lost (Rời bỏ)", 300));
        barChartCustSeg.getData().add(series);
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

        loadGenderPieChart();

        loadAgePieChart();
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
        

    @FXML
    public void onInventoryClick(ActionEvent event) {
        paneInventory.toFront();
    }

    @FXML
    public void onCustomerClick(ActionEvent event) {
        paneCustomer.toFront();
    }

    @FXML
    public void handleToggleGrowth(ActionEvent event) {
        areaChartCustGrowth.getData().clear();
        if (chkTotal.isSelected()) areaChartCustGrowth.getData().add(seriesTotal);
        if (chkNew.isSelected()) areaChartCustGrowth.getData().add(seriesNew);
        if (chkReturning.isSelected()) areaChartCustGrowth.getData().add(seriesReturning);
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

