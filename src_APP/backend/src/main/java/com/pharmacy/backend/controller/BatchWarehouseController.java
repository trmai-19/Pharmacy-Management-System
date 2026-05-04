package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.*;
import com.pharmacy.backend.service.BatchService;
import com.pharmacy.backend.service.WarehouseService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class BatchWarehouseController {

    private final BatchService batchService;
    private final WarehouseService warehouseService;

    @GetMapping("/batches")
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getBatches() {
        List<BatchResponse> data = batchService.getAllBatches();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách lô sản phẩm thành công", data));
    }

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventory() {
        List<InventoryResponse> data = warehouseService.getInventory();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách tồn kho thành công", data));
    }

    @GetMapping("/alerts/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getLowStockAlerts() {
        List<InventoryResponse> data = warehouseService.getLowStockAlerts();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách thuốc sắp hết hàng thành công", data));
    }

    @GetMapping("/alerts/expiring-soon")
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getExpiringSoonAlerts() {
        List<BatchResponse> data = batchService.getExpiringSoonAlerts();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách lô thuốc sắp hết hạn (dưới 3 tháng) thành công", data));
    }
}