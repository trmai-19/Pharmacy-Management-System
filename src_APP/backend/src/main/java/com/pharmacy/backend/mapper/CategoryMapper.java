package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.CategoryRequest;
import com.pharmacy.backend.dto.CategoryResponse;
import com.pharmacy.backend.dto.CategoryResponseStaff;
import com.pharmacy.backend.model.Category;

public class CategoryMapper {
    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .madm(category.getMadm())
                .tendm(category.getTendm())
                .mota(category.getMota())
                .tyleloinhuan(category.getTyleloinhuan())
                .build();
    }
    public static void updateCategoryFromRequest(Category category, CategoryRequest request) {
        category.setTendm(request.getTendm());
        category.setMota(request.getMota());
        category.setTyleloinhuan(request.getTyleloinhuan());
    }

    public static CategoryResponseStaff toStaffResponse(Category category) {
        CategoryResponseStaff dto = new CategoryResponseStaff();
        dto.setMadm(category.getMadm());
        dto.setTendm(category.getTendm());
        dto.setMota(category.getMota());
        return dto;
    }
}