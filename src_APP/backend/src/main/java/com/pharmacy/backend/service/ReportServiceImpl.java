package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.PerformanceReportResponse;
import com.pharmacy.backend.mapper.ReportMapper;
import com.pharmacy.backend.repository.InvoiceRepository;
import com.pharmacy.backend.repository.CustomerRepository;
import com.pharmacy.backend.repository.ChartProjection;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    public ReportServiceImpl(InvoiceRepository invoiceRepository, CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public PerformanceReportResponse getPerformanceDashboard(Integer year, Integer quarter, String productGroup, String customerType, String metric) {        
        try {
            int paramYear = (year != null) ? year : 0;
            int paramQuarter = (quarter != null) ? quarter : 0;
            String pGroup = (productGroup != null) ? productGroup : "All Product Groups";
            String cType = (customerType != null) ? customerType : "All Customers";
            String activeMetric = (metric != null) ? metric : "REVENUE";

            // 1. Kéo dữ liệu thực tế cho các thẻ KPI tổng quát
            Long totalOrders = invoiceRepository.countTotalOrders(paramYear, paramQuarter, pGroup, cType);
            Long totalRevenue = invoiceRepository.sumTotalRevenue(paramYear, paramQuarter, pGroup, cType);
            Long totalSpend = invoiceRepository.sumTotalSpend(paramYear, paramQuarter);
            Long totalLoss = invoiceRepository.sumTotalLoss(paramYear, paramQuarter);

            totalOrders = (totalOrders != null) ? totalOrders : 0L;
            totalRevenue = (totalRevenue != null) ? totalRevenue : 0L;
            totalSpend = (totalSpend != null) ? totalSpend : 0L;
            totalLoss = (totalLoss != null) ? totalLoss : 0L;
            
            Long realProfit = totalRevenue - totalSpend - totalLoss;

            // 2. Kéo dữ liệu thực tế từ Database cho các đồ thị tương ứng
            List<ChartProjection> genderStats = customerRepository.getGenderStatistics(paramYear, paramQuarter, pGroup, cType, activeMetric);
            List<ChartProjection> ageStats = customerRepository.getAgeStatistics(paramYear, paramQuarter, pGroup, cType, activeMetric);
            List<ChartProjection> trendStats = invoiceRepository.getTrendStatistics(paramYear, paramQuarter, pGroup, cType, activeMetric);
            List<ChartProjection> topProductStats = invoiceRepository.getTopProductStatistics(paramYear, paramQuarter, pGroup, cType, activeMetric);

            // 3. Định dạng chuỗi hiển thị KPI
            String formattedProfit = String.format(java.util.Locale.US, "%.1fM", realProfit / 1000000.0);
            String formattedSpend = String.format(java.util.Locale.US, "%.1fM", totalSpend / 1000000.0);
            String formattedLoss = String.format(java.util.Locale.US, "%.1fM", totalLoss / 1000000.0);
            
            PerformanceReportResponse.KpiResponse kpis = PerformanceReportResponse.KpiResponse.builder()
                    .totalOrders(String.valueOf(totalOrders))
                    .ordersTrend(8.42) 
                    .profit(formattedProfit) 
                    .profitTrend(12.15)
                    .spend(formattedSpend)
                    .spendTrend(-2.18)
                    .loss(formattedLoss)
                    .lossTrend(-4.73)
                    .returningRate("68%")
                    .returningRateTrend(4.20)
                    .build();

            // 4. Khớp dữ liệu biểu đồ tròn
            List<PerformanceReportResponse.PieDataResponse> genderData = ReportMapper.toPieDataList(genderStats);
            List<PerformanceReportResponse.PieDataResponse> ageData = ReportMapper.toPieDataList(ageStats);

            // 5. Khớp dữ liệu đồ thị đường (Trực tiếp gán giá trị thật vào Metric tương ứng để Client vẽ đồ thị)
            List<PerformanceReportResponse.MonthlyDataResponse> trendData = trendStats.stream().map(p ->
                    PerformanceReportResponse.MonthlyDataResponse.builder()
                            .month(p.getLabel())
                            .revenue(activeMetric.equals("REVENUE") ? p.getValue().longValue() : 0L)
                            .profit(activeMetric.equals("PROFIT") ? p.getValue().longValue() : 0L)
                            .loss(activeMetric.equals("LOSS") ? p.getValue().longValue() : 0L)
                            .spend(activeMetric.equals("SPEND") ? p.getValue().longValue() : 0L)
                            .orders(activeMetric.equals("ORDERS") ? p.getValue().longValue() : 0L)
                            .build()
            ).toList();

            // 6. Khớp dữ liệu đồ thị cột Top 5
            List<PerformanceReportResponse.ProductDataResponse> topProducts = topProductStats.stream().map(p ->
                    PerformanceReportResponse.ProductDataResponse.builder()
                            .productName(p.getLabel())
                            .revenue(activeMetric.equals("REVENUE") ? p.getValue().longValue() : 0L)
                            .profit(activeMetric.equals("PROFIT") ? p.getValue().longValue() : 0L)
                            .loss(activeMetric.equals("LOSS") ? p.getValue().longValue() : 0L)
                            .spend(activeMetric.equals("SPEND") ? p.getValue().longValue() : 0L)
                            .orders(activeMetric.equals("ORDERS") ? p.getValue().longValue() : 0L)
                            .build()
            ).toList();

            return PerformanceReportResponse.builder()
                    .kpis(kpis)
                    .genderData(genderData)
                    .ageData(ageData)
                    .trendData(trendData)
                    .topProducts(topProducts)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi truy xuất dữ liệu báo cáo: " + e.getMessage());
        }
    }
}