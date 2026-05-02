package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.CategoryRequest;
import com.pharmacy.backend.dto.CategoryResponse;
import com.pharmacy.backend.dto.CategoryResponseStaff;
import com.pharmacy.backend.mapper.CategoryMapper;
import com.pharmacy.backend.model.Category;
import com.pharmacy.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toResponse)
                .toList(); 
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        
        CategoryMapper.updateCategoryFromRequest(category, request);
        
        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.toResponse(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với mã: " + id));
            

        CategoryMapper.updateCategoryFromRequest(category, request);
        
        Category updatedCategory = categoryRepository.save(category);
        return CategoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với mã: " + id));
            
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryResponseStaff> getAllCategoriesForStaff() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toStaffResponse)
                .toList();
    }
}