package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.LoyaltyPointResponse;
import com.pharmacy.backend.service.LoyaltyPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loyalty") 
@RequiredArgsConstructor
@CrossOrigin("*") 
public class LoyaltyPointController {

    private final LoyaltyPointService loyaltyPointService;

    @GetMapping("/history/{customerId}")
    public ResponseEntity<List<LoyaltyPointResponse>> getCustomerHistory(@PathVariable String customerId) {
        List<LoyaltyPointResponse> history = loyaltyPointService.getHistoryByCustomerId(customerId);
        return ResponseEntity.ok(history);
    }
}