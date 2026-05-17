package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.*;
import com.pharmacy.backend.model.ImportReceiptDetail;
import com.pharmacy.backend.service.ImportReceiptService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class ImportReceiptsWarehouseController {
    
    private final ImportReceiptService importReceiptService;

    @GetMapping("/import-receipts")
    public ResponseEntity<ApiResponse<List<ImportReceiptResponse>>> getAllImportReceipts() {
        List<ImportReceiptResponse> data = importReceiptService.getAllImportReceipts();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách phiếu nhập kho thành công", data));
    }

    @PostMapping("/import-receipts")
    public ResponseEntity<ApiResponse<ImportReceiptResponse>> createImportReceipt(@RequestBody ImportReceiptRequest request) {
        ImportReceiptResponse data = importReceiptService.createImportReceipt(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Tạo phiếu nhập kho và cập nhật tồn kho thành công", data)); 
    }

    @GetMapping("/import-receipts/{mapn}/details")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getReceiptDetails(@PathVariable String mapn) {
        List<ImportReceiptDetail> details = importReceiptService.getDetailsByMapn(mapn);
        
        List<Map<String, Object>> data = details.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("malo", d.getMalo());
            map.put("sl", d.getSl());
            map.put("gianhap", d.getGianhap());
            return map;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách mã lô thành công", data));
    }
}