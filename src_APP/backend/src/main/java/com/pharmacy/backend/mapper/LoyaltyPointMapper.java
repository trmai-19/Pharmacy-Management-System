package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.LoyaltyPointResponse;
import com.pharmacy.backend.model.LoyaltyPoint;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoyaltyPointMapper {

    public LoyaltyPointResponse toResponse(LoyaltyPoint entity) {
        if (entity == null) return null;

        return LoyaltyPointResponse.builder()
                .id(entity.getId())
                .invoiceId(entity.getInvoiceId())
                .transactionType(entity.getTransactionType())
                .pointAmount(entity.getPointAmount())
                .transactionDate(entity.getTransactionDate())
                .note(entity.getNote())
                .build();
    }

    public List<LoyaltyPointResponse> toResponseList(List<LoyaltyPoint> entities) {
        return entities.stream()
                .map(this::toResponse) 
                .collect(Collectors.toList());
    }
}