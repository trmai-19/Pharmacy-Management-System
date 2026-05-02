package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.ProductRequest;
import com.pharmacy.backend.dto.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(String id, ProductRequest request);
    void deleteProduct(String id);
}