package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.CategoryRequest;
import com.pharmacy.backend.dto.CategoryResponse;
import com.pharmacy.backend.dto.CategoryResponseStaff;
import com.pharmacy.backend.mapper.CategoryMapper;
import com.pharmacy.backend.model.Category;
import com.pharmacy.backend.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList(); 
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        
        categoryMapper.updateCategoryFromRequest(category, request);
        
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với mã: " + id));
            

        categoryMapper.updateCategoryFromRequest(category, request);
        
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với mã: " + id));
            
        category.setTrangthai("DA_XOA"); 
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryResponseStaff> getAllCategoriesForStaff() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toStaffResponse)
                .toList();
    }
}