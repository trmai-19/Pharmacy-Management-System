package com.pharmacy.backend.dto;

import java.util.List;

public record PerformanceReportDTO(
        KpiDTO kpis,
        List<MonthlyDataDTO> trendData,
        List<ProductDataDTO> topProducts,
        List<PieDataDTO> genderData,
        List<PieDataDTO> ageData
) {
    public record KpiDTO(
            String totalOrders, double ordersTrend,
            String profit, double profitTrend,
            String spend, double spendTrend,
            String loss, double lossTrend,
            String returningRate, double returningRateTrend
    ) {}

    public record MonthlyDataDTO(
            String month,
            long revenue,
            long profit,
            long loss,
            long spend,
            long orders
    ) {}

    public record ProductDataDTO(
            String productName,
            long revenue,
            long profit,
            long loss,
            long spend,
            long orders
    ) {}

    public record PieDataDTO(
            String category,
            double value
    ) {}
}