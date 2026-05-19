package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.*;
import com.pharmacy.backend.service.ImportReceiptService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class ImportReceiptsWarehouseController {
    
    private final ImportReceiptService importReceiptService;

    @GetMapping("/import-receipts")
    public ResponseEntity<ApiResponse<List<ImportReceiptResponse>>> getAllImportReceipts(
            @RequestParam(required = false) String search) {
        List<ImportReceiptResponse> data = importReceiptService.getAllImportReceipts(search);
        
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách phiếu nhập thành công", data));
    }
    @PostMapping("/import-receipts")
    public ResponseEntity<ApiResponse<ImportReceiptResponse>> createImportReceipt(@RequestBody ImportReceiptRequest request) {
        ImportReceiptResponse data = importReceiptService.createImportReceipt(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Tạo phiếu nhập kho và cập nhật tồn kho thành công", data)); 
    }

    @GetMapping("/import-receipts/{mapn}/details")
    public ResponseEntity<ApiResponse<List<ImportReceiptDetailResponse>>> getReceiptDetails(@PathVariable String mapn) {
        
        List<ImportReceiptDetailResponse> data = importReceiptService.getDetailsByMapn(mapn);
        
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách chi tiết phiếu nhập thành công", data));
    }
}