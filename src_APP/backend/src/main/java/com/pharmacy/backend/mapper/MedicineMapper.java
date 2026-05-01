package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.MedicineResponse;
import com.pharmacy.backend.model.Product;

public class MedicineMapper {
    public static MedicineResponse toResponse(Product product) {
        if (product == null) return null;
        
        return MedicineResponse.builder()
                .masp(product.getMasp())
                .tensanpham(product.getTensanpham())
                .dvt(product.getDvt())
                .congdung(product.getCongdung())
                .thanhphan(product.getThanhphan())
                .giaban(product.getGiaban())
                .build();
    }
}