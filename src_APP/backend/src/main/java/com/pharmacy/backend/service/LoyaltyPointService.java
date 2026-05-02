package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.LoyaltyPointResponse;
import java.util.List;

public interface LoyaltyPointService {
    List<LoyaltyPointResponse> getHistoryByCustomerId(String customerId);
}