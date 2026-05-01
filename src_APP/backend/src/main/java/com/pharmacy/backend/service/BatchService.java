package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.BatchResponse;
import java.util.List;

public interface BatchService {
    List<BatchResponse> getAllBatches();
    List<BatchResponse> getExpiringSoonAlerts();
}