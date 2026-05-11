package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.InvoiceRequest;
import com.pharmacy.backend.dto.InvoiceResponse;
import com.pharmacy.backend.service.InvoiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sales/invoices") 
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody InvoiceRequest request) { 
            
        InvoiceResponse data = invoiceService.createInvoice(request);

        ApiResponse<InvoiceResponse> response = ApiResponse.<InvoiceResponse>builder()
                .status(200)
                .message("Lập hóa đơn bán hàng thành công")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(@PathVariable String id) {
    InvoiceResponse data = invoiceService.getInvoiceById(id);

    ApiResponse<InvoiceResponse> response = ApiResponse.<InvoiceResponse>builder()
            .status(200)
            .message("Lấy chi tiết hóa đơn thành công")
            .data(data)
            .build();

    return ResponseEntity.ok(response);
}
}