package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.PerformanceReportDTO;
import com.pharmacy.backend.service.ReportService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*") // Tránh lỗi chặn CORS từ JavaFX
public class ReportController {

    private final ReportService reportService;

    // Dependency Injection thông qua Constructor
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/performance")
    public PerformanceReportDTO getPerformanceReport() {
        return reportService.getPerformanceReport();
    }
}