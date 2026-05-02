package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.LoyaltyPointResponse;
import com.pharmacy.backend.mapper.LoyaltyPointMapper;
import com.pharmacy.backend.model.LoyaltyPoint;
import com.pharmacy.backend.repository.LoyaltyPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor 
public class LoyaltyPointServiceImpl implements LoyaltyPointService {
    private final LoyaltyPointRepository loyaltyPointRepository;

    @Override
    public List<LoyaltyPointResponse> getHistoryByCustomerId(String customerId) {
        List<LoyaltyPoint> history = loyaltyPointRepository.findByCustomerIdOrderByTransactionDateDesc(customerId);
        
        return LoyaltyPointMapper.toResponseList(history);
    }
}