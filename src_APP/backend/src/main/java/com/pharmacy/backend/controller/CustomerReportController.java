
package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.CustomerReportResponse;
import com.pharmacy.backend.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reports")
public class CustomerReportController {

    private final CustomerService customerService;

    public CustomerReportController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<CustomerReportResponse>> getCustomerReport(
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String quarter,
            @RequestParam(required = false) String productGroup,
            @RequestParam(required = false) String customerType) {
        
        // Chuyển đổi định dạng Year an toàn
        Integer paramYear = null;
        if (year != null && !year.equals("Năm") && !year.equals("Year")) {
            try { paramYear = Integer.parseInt(year); } catch (NumberFormatException e) { paramYear = 2026; }
        }

        CustomerReportResponse data = customerService.getCustomerDashboard(paramYear, quarter, productGroup, customerType);
        
        // Đóng gói đúng chuẩn cấu trúc ApiResponse mà Frontend đang chờ
        ApiResponse<CustomerReportResponse> response = ApiResponse.<CustomerReportResponse>builder()
                .status(200)
                .message("Tải báo cáo khách hàng thành công")
                .data(data)
                .build();
                
        return ResponseEntity.ok(response);
    }
}