package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.PriceSuggestionResponse;
import com.pharmacy.backend.dto.ProductRequest;
import com.pharmacy.backend.dto.ProductResponse;
import com.pharmacy.backend.service.PriceSuggestionService;
import com.pharmacy.backend.service.ProductService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;
    private final PriceSuggestionService priceSuggestionService;


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
/**
     * API 7a: Gợi ý giá bán cho 1 sản phẩm cụ thể
     * GET /api/admin/products/{id}/price-suggestion
     *
     * Tính: giá nhập gần nhất * (1 + tỉ lệ lợi nhuận của danh mục / 100)
     * So sánh với giá bán hiện tại và trả về chênh lệch.
     */
    @GetMapping("/{id}/price-suggestion")
    public ResponseEntity<ApiResponse<PriceSuggestionResponse>> getPriceSuggestion(@PathVariable String id) {
        PriceSuggestionResponse data = priceSuggestionService.getSuggestionForProduct(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy gợi ý giá bán thành công", data));
    }

    /**
     * API 7b: Gợi ý giá bán cho tất cả sản phẩm
     * GET /api/admin/products/price-suggestions
     *
     * Sắp xếp theo chênh lệch tuyệt đối giảm dần (sản phẩm cần điều chỉnh giá nhất lên đầu).
     */
    @GetMapping("/price-suggestions")
    public ResponseEntity<ApiResponse<List<PriceSuggestionResponse>>> getAllPriceSuggestions() {
        List<PriceSuggestionResponse> data = priceSuggestionService.getAllPriceSuggestions();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy gợi ý giá bán cho tất cả sản phẩm thành công", data));
    }

}