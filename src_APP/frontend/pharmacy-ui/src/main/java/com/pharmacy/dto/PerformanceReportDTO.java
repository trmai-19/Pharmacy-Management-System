package com.pharmacy.dto;

import java.util.List;

public class PerformanceReportDTO {
    public KpiDTO kpis;
    public List<MonthlyDataDTO> trendData;
    public List<ProductDataDTO> topProducts;
    public List<PieDataDTO> genderData;
    public List<PieDataDTO> ageData;

    public static class KpiDTO {
        public String totalOrders;
        public double ordersTrend;
        public String profit;
        public double profitTrend;
        public String spend;
        public double spendTrend;
        public String loss;
        public double lossTrend;
        public String returningRate;
        public double returningRateTrend;
    }

    public static class MonthlyDataDTO {
        public String month;
        public long revenue, profit, loss, spend, orders;
    }

    public static class ProductDataDTO {
        public String productName;
        public long revenue, profit, loss, spend, orders;
    }

    public static class PieDataDTO {
        public String category;
        public double value;
    }
}