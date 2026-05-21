package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse; // Import file cấu trúc ApiResponse của bạn
import com.pharmacy.backend.dto.PerformanceReportResponse;
import com.pharmacy.backend.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<ApiResponse<PerformanceReportResponse>> getPerformanceReport() {
        
        PerformanceReportResponse data = reportService.getPerformanceDashboard();
        
        // Bọc vào ApiResponse theo chuẩn của team
        ApiResponse<PerformanceReportResponse> response = new ApiResponse<>(200, "Lấy dữ liệu thành công", data);
        
        return ResponseEntity.ok(response);
    }
}