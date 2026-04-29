package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.ProductRequest;
import com.pharmacy.backend.dto.ProductResponse;
import com.pharmacy.backend.model.Product;

public class ProductMapper {
    public static ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .masp(product.getMasp())
                .madm(product.getMadm())
                .tensanpham(product.getTensanpham())
                .dvt(product.getDvt())
                .congdung(product.getCongdung())
                .thanhphan(product.getThanhphan())
                .giaban(product.getGiaban())
                .isManualPrice(product.isManualPrice()) 
                .build();
    }
    public static void updateProductFromRequest(Product product, ProductRequest request) {
        product.setMadm(request.getMadm());
        product.setTensanpham(request.getTensanpham());
        product.setDvt(request.getDvt());
        product.setCongdung(request.getCongdung());
        product.setThanhphan(request.getThanhphan());
        product.setGiaban(request.getGiaban());
        product.setManualPrice(request.isManualPrice());
    }
}