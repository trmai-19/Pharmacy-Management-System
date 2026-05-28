package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.InvoiceRequest;
import com.pharmacy.backend.dto.InvoiceResponse;
import com.pharmacy.backend.service.InvoiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import com.pharmacy.backend.dto.InvoiceListResponse;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceListResponse>>> getAllInvoices(
            @RequestParam(required = false) String search) {
        
        List<InvoiceListResponse> data = invoiceService.getAllInvoices(search);
        
        ApiResponse<List<InvoiceListResponse>> response = ApiResponse.<List<InvoiceListResponse>>builder()
                .status(200)
                .message("Lấy danh sách tóm tắt hóa đơn thành công")
                .data(data)
                .build();
                
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InvoiceResponse>> updateInvoiceStatus(
            @PathVariable String id,
            @RequestParam String status) {
            
        InvoiceResponse data = invoiceService.updateInvoiceStatus(id, status);

        ApiResponse<InvoiceResponse> response = ApiResponse.<InvoiceResponse>builder()
                .status(200)
                .message("Cập nhật trạng thái hóa đơn thành công")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}