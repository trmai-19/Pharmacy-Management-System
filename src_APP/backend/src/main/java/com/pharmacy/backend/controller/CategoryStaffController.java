package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.CategoryResponseStaff;
import com.pharmacy.backend.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryStaffController {

    private final CategoryService categoryService;

    public CategoryStaffController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseStaff>>> getAllForStaff() {
        List<CategoryResponseStaff> data = categoryService.getAllCategoriesForStaff();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh mục thành công", data));
    }
}