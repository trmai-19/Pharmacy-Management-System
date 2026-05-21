package com.pharmacy.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PerformanceReportResponse {
    private KpiResponse kpis;
    private List<MonthlyDataResponse> trendData;
    private List<ProductDataResponse> topProducts;
    private List<PieDataResponse> genderData;
    private List<PieDataResponse> ageData;

    @Data
    @Builder
    public static class KpiResponse {
        private String totalOrders;
        private double ordersTrend;
        private String profit;
        private double profitTrend;
        private String spend;
        private double spendTrend;
        private String loss;
        private double lossTrend;
        private String returningRate;
        private double returningRateTrend;
    }

    @Data
    @Builder
    public static class MonthlyDataResponse {
        private String month;
        private long revenue;
        private long profit;
        private long loss;
        private long spend;
        private long orders;
    }

    @Data
    @Builder
    public static class ProductDataResponse {
        private String productName;
        private long revenue;
        private long profit;
        private long loss;
        private long spend;
        private long orders;
    }

    @Data
    @Builder
    public static class PieDataResponse {
        private String category;
        private double value;
    }
}