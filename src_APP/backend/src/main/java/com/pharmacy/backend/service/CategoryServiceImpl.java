package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.CategoryRequest;
import com.pharmacy.backend.dto.CategoryResponse;
import com.pharmacy.backend.model.Category;
import com.pharmacy.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> responses = new ArrayList<>();
        
        for (Category category : categories) {
            CategoryResponse response = new CategoryResponse();
            response.setMadm(category.getMadm());
            response.setTendm(category.getTendm());
            response.setMota(category.getMota());
            response.setTyleloinhuan(category.getTyleloinhuan());
            responses.add(response);
        }
        
        return responses;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        
        category.setTendm(request.getTendm());
        category.setMota(request.getMota());
        category.setTyleloinhuan(request.getTyleloinhuan());
        
        Category savedCategory = categoryRepository.save(category);
        
        CategoryResponse response = new CategoryResponse();
        response.setMadm(savedCategory.getMadm());
        response.setTendm(savedCategory.getTendm());
        response.setMota(savedCategory.getMota());
        response.setTyleloinhuan(savedCategory.getTyleloinhuan());
        
        return response;
    }

    @Override
    public CategoryResponse updateCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với mã: " + id));
            
        category.setTendm(request.getTendm());
        category.setMota(request.getMota());
        category.setTyleloinhuan(request.getTyleloinhuan());
        
        Category updatedCategory = categoryRepository.save(category);
        
        CategoryResponse response = new CategoryResponse();
        response.setMadm(updatedCategory.getMadm());
        response.setTendm(updatedCategory.getTendm());
        response.setMota(updatedCategory.getMota());
        response.setTyleloinhuan(updatedCategory.getTyleloinhuan());
        
        return response;
    }

    @Override
    public void deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với mã: " + id));
            
        categoryRepository.delete(category);
    }
}