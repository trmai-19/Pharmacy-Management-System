package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.CategoryRequest;
import com.pharmacy.backend.dto.CategoryResponse;
import com.pharmacy.backend.service.CategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/categories")
@RequiredArgsConstructor
public class CategoryStaffController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllForStaff() {
        List<CategoryResponse> data = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh mục thành công", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody CategoryRequest request) {
        CategoryResponse data = categoryService.createCategory(request);
        ApiResponse<CategoryResponse> response = new ApiResponse<>(200, "Thêm danh mục thành công", data);
        return ResponseEntity.ok(response);
    }
}