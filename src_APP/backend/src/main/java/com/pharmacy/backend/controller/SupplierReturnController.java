package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.SupplierReturnRequest;
import com.pharmacy.backend.dto.SupplierReturnResponse;
import com.pharmacy.backend.service.SupplierReturnService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warehouse/supplier-returns")
@RequiredArgsConstructor
public class SupplierReturnController {

    private final SupplierReturnService supplierReturnService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierReturnResponse>> createSupplierReturn(
            @Valid @RequestBody SupplierReturnRequest request) {
        
        SupplierReturnResponse data = supplierReturnService.createSupplierReturn(request);
        
        ApiResponse<SupplierReturnResponse> response = ApiResponse.<SupplierReturnResponse>builder()
                .status(200)
                .message("Tạo phiếu trả hàng cho nhà cung cấp thành công")
                .data(data)
                .build();
                
        return ResponseEntity.ok(response);
    }
}