package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.PerformanceReportResponse;
import com.pharmacy.backend.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reports") // Đảm bảo đúng chuẩn API Quản trị
@CrossOrigin(origins = "*") 
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<PerformanceReportResponse>> getPerformanceReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer quarter,
            @RequestParam(required = false) String productGroup,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) String metric
    ) {
        PerformanceReportResponse data = reportService.getPerformanceDashboard(year, quarter, productGroup, customerType, metric);
        ApiResponse<PerformanceReportResponse> response = new ApiResponse<>(200, "Lấy dữ liệu thành công", data);
        return ResponseEntity.ok(response);
    }
}