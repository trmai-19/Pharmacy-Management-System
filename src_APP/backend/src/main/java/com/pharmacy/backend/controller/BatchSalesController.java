package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.BatchSalesResponse;
import com.pharmacy.backend.service.BatchSalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales/batches")
@RequiredArgsConstructor
public class BatchSalesController {

    private final BatchSalesService batchSalesService;

    @GetMapping("/{masp}")
    public ResponseEntity<ApiResponse<List<BatchSalesResponse>>> getBatchesForSales(@PathVariable String masp) {
        
        List<BatchSalesResponse> data = batchSalesService.getBatchesForSales(masp);
        
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách lô hàng bán hợp lệ thành công", data));
    }
}