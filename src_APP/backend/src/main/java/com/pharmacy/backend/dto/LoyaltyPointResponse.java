package com.pharmacy.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class LoyaltyPointResponse {
    private String id;
    private String invoiceId;
    private String transactionType;
    private Integer pointAmount;
    private LocalDateTime transactionDate;
    private String note;
}