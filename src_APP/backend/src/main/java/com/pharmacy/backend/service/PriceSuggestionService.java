package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.PriceSuggestionResponse;

import java.util.List;

public interface PriceSuggestionService {
    /** API 7a: GET /api/admin/products/{id}/price-suggestion */
    PriceSuggestionResponse getSuggestionForProduct(String masp);

    /** API 7b: GET /api/admin/products/price-suggestions — Gợi ý giá cho tất cả sản phẩm */
    List<PriceSuggestionResponse> getAllPriceSuggestions();
}

