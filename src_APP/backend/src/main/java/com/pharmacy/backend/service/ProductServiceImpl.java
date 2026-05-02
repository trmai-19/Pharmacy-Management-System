package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.ProductRequest;
import com.pharmacy.backend.dto.ProductResponse;
import com.pharmacy.backend.mapper.ProductMapper;
import com.pharmacy.backend.model.Product;
import com.pharmacy.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        
        // Đổ dữ liệu từ Request sang Model
        ProductMapper.updateProductFromRequest(product, request);
        
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + id));
            
        ProductMapper.updateProductFromRequest(product, request);
        
        Product updatedProduct = productRepository.save(product);
        return ProductMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + id));
            
        productRepository.delete(product);
    }
}