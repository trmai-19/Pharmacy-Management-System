package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.ProductRequest;
import com.pharmacy.backend.dto.ProductResponse;
import com.pharmacy.backend.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/warehouse/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;


    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody ProductRequest request) {
        ProductResponse data = productService.createProduct(request);
        ApiResponse<ProductResponse> response = new ApiResponse<>(200, "Thêm sản phẩm thành công", data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable String id, 
            @RequestBody ProductRequest request) {
        ProductResponse data = productService.updateProduct(id, request);
        ApiResponse<ProductResponse> response = new ApiResponse<>(200, "Cập nhật sản phẩm thành công", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        ApiResponse<Void> response = new ApiResponse<>(200, "Xóa sản phẩm thành công", null);
        return ResponseEntity.ok(response);
    }
}