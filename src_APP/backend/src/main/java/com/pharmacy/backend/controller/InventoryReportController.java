package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.InventoryReportResponse;
import com.pharmacy.backend.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports/inventory")
@CrossOrigin(origins = "*")
public class InventoryReportController {

    private final InventoryService inventoryService;

    public InventoryReportController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<InventoryReportResponse>> getInventoryReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer quarter,
            @RequestParam(required = false) String productGroup,
            @RequestParam(required = false) String customerType
    ) {
        InventoryReportResponse data = inventoryService.getInventoryDashboard(year, quarter, productGroup, customerType);
        ApiResponse<InventoryReportResponse> response = new ApiResponse<>(200, "Tải dữ liệu kho thành công", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getFilterCategories() {
        List<String> categories = inventoryService.getFilterCategories();
        ApiResponse<List<String>> response = new ApiResponse<>(200, "Tải danh mục thành công", categories);
        return ResponseEntity.ok(response);
    }
}