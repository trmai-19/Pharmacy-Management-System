package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.CustomerSegmentResponse;
import com.pharmacy.backend.dto.StaffPerformanceResponse;

import lombok.RequiredArgsConstructor;

import com.pharmacy.backend.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class ReportAdminController {

    private final ReportService reportService;

    /**
     * API 3: Hiệu suất nhân viên bán hàng
     * GET /api/admin/reports/staff-performance?from=2025-01-01T00:00:00&to=2025-12-31T23:59:59
     *
     * Nếu không truyền from/to, mặc định lấy 30 ngày gần nhất.
     */
    @GetMapping("/staff-performance")
    public ResponseEntity<ApiResponse<List<StaffPerformanceResponse>>> getStaffPerformance(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        // Mặc định: 30 ngày gần nhất
        LocalDateTime effectiveFrom = from != null ? from : LocalDateTime.now().minusDays(30);
        LocalDateTime effectiveTo   = to   != null ? to   : LocalDateTime.now();

        List<StaffPerformanceResponse> data = reportService.getStaffPerformance(effectiveFrom, effectiveTo);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy báo cáo hiệu suất nhân viên thành công", data));
    }

    /**
     * API 4: Phân tích tệp khách hàng theo hạng thành viên
     * GET /api/admin/reports/customer-segments
     */
    @GetMapping("/customer-segments")
    public ResponseEntity<ApiResponse<List<CustomerSegmentResponse>>> getCustomerSegments() {
        List<CustomerSegmentResponse> data = reportService.getCustomerSegments();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy phân tích hạng khách hàng thành công", data));
    }
}
