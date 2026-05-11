package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.ProductRequest;
import com.pharmacy.backend.dto.ProductResponse;
import com.pharmacy.backend.mapper.ProductMapper;
import com.pharmacy.backend.model.Product;
import com.pharmacy.backend.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        
        productMapper.updateProductFromRequest(product, request);
        
        if (product.getTrangthai() == null || product.getTrangthai().isEmpty()) {
            product.setTrangthai("DANG_BAN"); 
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + id));
            
        productMapper.updateProductFromRequest(product, request);
        
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + id));
            
        product.setTrangthai("NGUNG_BAN"); 
        productRepository.save(product);
    }
}