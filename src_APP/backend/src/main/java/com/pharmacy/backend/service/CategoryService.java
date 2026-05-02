package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.CategoryRequest;
import com.pharmacy.backend.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(String id, CategoryRequest request);
    void deleteCategory(String id);
}