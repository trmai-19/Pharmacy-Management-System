package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.*;
import com.pharmacy.backend.service.BatchService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BatchWarehouseController {

    private final BatchService batchService;

    @GetMapping("/batches/{masp}")
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getBatchesByMasp(@PathVariable String masp) {
        List<BatchResponse> data = batchService.getBatchesByMasp(masp);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách lô theo sản phẩm thành công", data));
    }
    
    @GetMapping("/alerts/low-stock")
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getLowStockAlerts() {
        List<BatchResponse> data = batchService.getLowStockAlerts();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách lô thuốc sắp hết hàng thành công", data));
    }

    @GetMapping("/alerts/expiring-soon")
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getExpiringSoonAlerts() {
        List<BatchResponse> data = batchService.getExpiringSoonAlerts();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách lô thuốc sắp hết hạn thành công", data));
    }

    @GetMapping("/warehouses")
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getAllWarehouses() {
        List<WarehouseResponse> data = batchService.getAllWarehouses();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách kho thành công", data));
    }
}