package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.ReturnReceiptRequest;
import com.pharmacy.backend.dto.ReturnReceiptResponse;
import com.pharmacy.backend.service.ReturnReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales/return-receipts")
@RequiredArgsConstructor
public class ReturnReceiptController {

    private final ReturnReceiptService returnReceiptService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReturnReceiptResponse>> createReturnReceipt(
            @Valid @RequestBody ReturnReceiptRequest request) {
        
        ReturnReceiptResponse data = returnReceiptService.createReturnReceipt(request);
        
        ApiResponse<ReturnReceiptResponse> response = ApiResponse.<ReturnReceiptResponse>builder()
                .status(200)
                .message("Tạo phiếu trả hàng thành công")
                .data(data)
                .build();
                
        return ResponseEntity.ok(response);
    }
}