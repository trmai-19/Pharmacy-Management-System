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

            // =======================================================
            // 1. KÉO DỮ LIỆU THỰC TẾ CỦA KỲ HIỆN TẠI TỪ DB
            // =======================================================
            Long totalOrders = invoiceRepository.countTotalOrders(paramYear, paramQuarter, pGroup, cType);
            Long totalRevenue = invoiceRepository.sumTotalRevenue(paramYear, paramQuarter, pGroup, cType);
            Long totalSpend = invoiceRepository.sumTotalSpend(paramYear, paramQuarter);
            Long totalLoss = invoiceRepository.sumTotalLoss(paramYear, paramQuarter);

            // Truy vấn dữ liệu khách hàng phục vụ tính toán Returning Rate thật
            Long currentActiveCustomers = customerRepository.countActiveCustomers(paramYear, paramQuarter, pGroup, cType);
            Long currentReturningCustomers = customerRepository.countReturningCustomers(paramYear, paramQuarter, pGroup, cType);

            totalOrders = (totalOrders != null) ? totalOrders : 0L;
            totalRevenue = (totalRevenue != null) ? totalRevenue : 0L;
            totalSpend = (totalSpend != null) ? totalSpend : 0L;
            totalLoss = (totalLoss != null) ? totalLoss : 0L;
            currentActiveCustomers = (currentActiveCustomers != null) ? currentActiveCustomers : 0L;
            currentReturningCustomers = (currentReturningCustomers != null) ? currentReturningCustomers : 0L;
            
            Long realProfit = totalRevenue - totalSpend - totalLoss;

            // Tính toán tỷ lệ phần trăm quay lại của kỳ hiện tại
            double currentReturnRate = 0.0;
            if (currentActiveCustomers > 0) {
                currentReturnRate = ((double) currentReturningCustomers / currentActiveCustomers) * 100.0;
            }

            // =======================================================
            // 2. TÍNH TOÁN DỮ LIỆU KỲ TRƯỚC (PREVIOUS PERIOD) ĐỂ LÀM TREND
            // =======================================================
            int prevYear = paramYear;
            int prevQuarter = paramQuarter;

            if (paramYear > 0) {
                if (paramQuarter == 0) { 
                    prevYear = paramYear - 1;
                } else { 
                    if (paramQuarter == 1) { 
                        prevQuarter = 4;
                        prevYear = paramYear - 1;
                    } else { 
                        prevQuarter = paramQuarter - 1;
                    }
                }
            }

            // Gọi DB lấy số liệu của Kỳ Trước
            Long prevOrders = invoiceRepository.countTotalOrders(prevYear, prevQuarter, pGroup, cType);
            Long prevRevenue = invoiceRepository.sumTotalRevenue(prevYear, prevQuarter, pGroup, cType);
            Long prevSpend = invoiceRepository.sumTotalSpend(prevYear, prevQuarter);
            Long prevLoss = invoiceRepository.sumTotalLoss(prevYear, prevQuarter);
            
            // Lấy số liệu khách hàng kỳ trước để tính toán Trend cho Returning Rate
            Long prevActiveCustomers = customerRepository.countActiveCustomers(prevYear, prevQuarter, pGroup, cType);
            Long prevReturningCustomers = customerRepository.countReturningCustomers(prevYear, prevQuarter, pGroup, cType);

            prevOrders = (prevOrders != null) ? prevOrders : 0L;
            prevRevenue = (prevRevenue != null) ? prevRevenue : 0L;
            prevSpend = (prevSpend != null) ? prevSpend : 0L;
            prevLoss = (prevLoss != null) ? prevLoss : 0L;
            prevActiveCustomers = (prevActiveCustomers != null) ? prevActiveCustomers : 0L;
            prevReturningCustomers = (prevReturningCustomers != null) ? prevReturningCustomers : 0L;
            
            Long prevProfit = prevRevenue - prevSpend - prevLoss;

            double prevReturnRate = 0.0;
            if (prevActiveCustomers > 0) {
                prevReturnRate = ((double) prevReturningCustomers / prevActiveCustomers) * 100.0;
            }

            // Tính toán sự chênh lệch % Trend thực tế
            double ordersTrend = Math.round(calculateTrend(totalOrders, prevOrders) * 100.0) / 100.0;
            double profitTrend = Math.round(calculateTrend(realProfit, prevProfit) * 100.0) / 100.0;
            double spendTrend = Math.round(calculateTrend(totalSpend, prevSpend) * 100.0) / 100.0;
            double lossTrend = Math.round(calculateTrend(totalLoss, prevLoss) * 100.0) / 100.0;
            
            // Xu hướng tăng trưởng của tỷ lệ quay lại (hiệu số phần trăm giữa 2 kỳ)
            double returnRateTrend = Math.round((currentReturnRate - prevReturnRate) * 100.0) / 100.0; 

            // =======================================================
            // 3. KÉO DỮ LIỆU ĐỒ THỊ TỪ DATABASE 
            // =======================================================
            List<ChartProjection> genderStats = customerRepository.getGenderStatistics(paramYear, paramQuarter, pGroup, cType, activeMetric);
            List<ChartProjection> ageStats = customerRepository.getAgeStatistics(paramYear, paramQuarter, pGroup, cType, activeMetric);
            List<ChartProjection> trendStats = invoiceRepository.getTrendStatistics(paramYear, paramQuarter, pGroup, cType, activeMetric);
            List<ChartProjection> topProductStats = invoiceRepository.getTopProductStatistics(paramYear, paramQuarter, pGroup, cType, activeMetric);

            // 4. Định dạng chuỗi hiển thị KPI
            String formattedProfit = String.format(java.util.Locale.US, "%.1fM", realProfit / 1000000.0);
            String formattedSpend = String.format(java.util.Locale.US, "%.1fM", totalSpend / 1000000.0);
            String formattedLoss = String.format(java.util.Locale.US, "%.1fM", totalLoss / 1000000.0);
            
            // 5. Đóng gói KpiResponse kết nối dữ liệu Returning Rate động từ DB
            PerformanceReportResponse.KpiResponse kpis = PerformanceReportResponse.KpiResponse.builder()
                    .totalOrders(String.valueOf(totalOrders))
                    .ordersTrend(ordersTrend) 
                    .profit(formattedProfit) 
                    .profitTrend(profitTrend)
                    .spend(formattedSpend)
                    .spendTrend(spendTrend)
                    .loss(formattedLoss)
                    .lossTrend(lossTrend)
                    .returningRate(String.format("%.1f%%", currentReturnRate)) 
                    .returningRateTrend(returnRateTrend)
                    .build();

            // 6. Khớp dữ liệu biểu đồ tròn
            List<PerformanceReportResponse.PieDataResponse> genderData = ReportMapper.toPieDataList(genderStats);
            List<PerformanceReportResponse.PieDataResponse> ageData = ReportMapper.toPieDataList(ageStats);

            // 7. Khớp dữ liệu đồ thị đường 
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

            // 8. Khớp dữ liệu đồ thị cột Top 5
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

    private double calculateTrend(double current, double previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0; 
        }
        return ((current - previous) / previous) * 100.0;
    }
}