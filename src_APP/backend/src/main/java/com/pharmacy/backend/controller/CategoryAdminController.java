package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.CategoryRequest;
import com.pharmacy.backend.dto.CategoryResponse;
import com.pharmacy.backend.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    public CategoryAdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> data = categoryService.getAllCategories();
        ApiResponse<List<CategoryResponse>> response = new ApiResponse<>(200, "Lấy danh sách danh mục thành công", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody CategoryRequest request) {
        CategoryResponse data = categoryService.createCategory(request);
        ApiResponse<CategoryResponse> response = new ApiResponse<>(200, "Thêm danh mục thành công", data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable String id, 
            @RequestBody CategoryRequest request) {
        CategoryResponse data = categoryService.updateCategory(id, request);
        ApiResponse<CategoryResponse> response = new ApiResponse<>(200, "Cập nhật danh mục thành công", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        ApiResponse<Void> response = new ApiResponse<>(200, "Xóa danh mục thành công", null);
        return ResponseEntity.ok(response);
    }
}