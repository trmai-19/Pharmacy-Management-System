package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.PerformanceReportDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    public PerformanceReportDTO getPerformanceReport() {
        
        // 1. Dữ liệu KPI
        var kpis = new PerformanceReportDTO.KpiDTO(
                "12,582", 8.42,
                "152.4M", 12.15,
                "64.8M", -2.18,
                "18.2M", -4.73,
                "68%", 4.20
        );

        // 2. Dữ liệu Trend (Area Chart)
        var trendData = List.of(
                new PerformanceReportDTO.MonthlyDataDTO("Jan", 12000000, 8000000, 2000000, 5000000, 300),
                new PerformanceReportDTO.MonthlyDataDTO("Feb", 15000000, 9500000, 1800000, 6000000, 350),
                new PerformanceReportDTO.MonthlyDataDTO("Mar", 11000000, 7000000, 2500000, 5800000, 320)
        );

        // 3. Dữ liệu Top Products (Bar Chart)
        var topProducts = List.of(
                new PerformanceReportDTO.ProductDataDTO("Panadol", 520, 420, 90, 600, 800),
                new PerformanceReportDTO.ProductDataDTO("Vitamin C", 470, 350, 70, 350, 620),
                new PerformanceReportDTO.ProductDataDTO("Masks", 390, 280, 50, 300, 410)
        );

        // 4. Dữ liệu Pie Chart
        var genderData = List.of(
                new PerformanceReportDTO.PieDataDTO("Male", 45),
                new PerformanceReportDTO.PieDataDTO("Female", 55)
        );

        var ageData = List.of(
                new PerformanceReportDTO.PieDataDTO("18-24", 15),
                new PerformanceReportDTO.PieDataDTO("25-34", 40),
                new PerformanceReportDTO.PieDataDTO("35-44", 25),
                new PerformanceReportDTO.PieDataDTO("45+", 20)
        );

        return new PerformanceReportDTO(kpis, trendData, topProducts, genderData, ageData);
    }
}