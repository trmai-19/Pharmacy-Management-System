package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.*;
import com.pharmacy.backend.service.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
public class SuppliersWarehouseController {

    private final SupplierService supplierService;

    public SuppliersWarehouseController( SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/suppliers")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getAllSuppliers() {
        List<SupplierResponse> data = supplierService.getAllSuppliers();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách nhà cung cấp thành công", data));
    }

    @PostMapping("/suppliers")
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(@RequestBody SupplierRequest request) {
        SupplierResponse data = supplierService.createSupplier(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Thêm mới nhà cung cấp thành công", data));
    }

    @PutMapping("/suppliers/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(@PathVariable String id, @RequestBody SupplierRequest request) {
        SupplierResponse data = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật nhà cung cấp thành công", data));
    }

}