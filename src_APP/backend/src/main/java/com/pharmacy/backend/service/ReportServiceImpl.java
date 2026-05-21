package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.PerformanceReportResponse;
import com.pharmacy.backend.mapper.ReportMapper;
import com.pharmacy.backend.repository.InvoiceRepository;
import com.pharmacy.backend.repository.CustomerRepository;
import com.pharmacy.backend.repository.ChartProjection;
import com.pharmacy.backend.service.ReportService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    // Constructor Injection chuẩn quy tắc DI
    public ReportServiceImpl(InvoiceRepository invoiceRepository, CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public PerformanceReportResponse getPerformanceDashboard() {
        try {
            // Gọi qua các repo tiếng Anh mới cập nhật
            Long totalOrders = invoiceRepository.countTotalOrders();
            Long totalRevenue = invoiceRepository.sumTotalRevenue();
            List<ChartProjection> genderStats = customerRepository.getGenderStatistics();

            // Đóng gói dữ liệu vào KPI DTO sử dụng Builder
            PerformanceReportResponse.KpiResponse kpis = PerformanceReportResponse.KpiResponse.builder()
                    .totalOrders(String.valueOf(totalOrders))
                    .ordersTrend(8.42)
                    .profit(totalRevenue / 1000000.0 + "M") 
                    .profitTrend(12.15)
                    .spend("64.8M")
                    .spendTrend(-2.18)
                    .loss("18.2M")
                    .lossTrend(-4.73)
                    .returningRate("68%")
                    .returningRateTrend(4.20)
                    .build();

            // Ánh xạ dữ liệu biểu đồ tròn qua Mapper tĩnh
            List<PerformanceReportResponse.PieDataResponse> genderData = ReportMapper.toPieDataList(genderStats);

            return PerformanceReportResponse.builder()
                    .kpis(kpis)
                    .genderData(genderData)
                    .trendData(new ArrayList<>()) 
                    .topProducts(new ArrayList<>()) 
                    .ageData(new ArrayList<>()) 
                    .build();

        } catch (Exception e) {
            // Tuân thủ nghiêm ngặt quy tắc ném lỗi RuntimeException
            throw new RuntimeException("Lỗi khi truy xuất dữ liệu báo cáo: " + e.getMessage());
        }
    }
}