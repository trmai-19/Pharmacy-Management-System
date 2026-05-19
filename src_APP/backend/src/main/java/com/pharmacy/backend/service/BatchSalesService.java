package com.pharmacy.backend.service;

import java.util.List;

import com.pharmacy.backend.dto.BatchSalesResponse;

public interface BatchSalesService {
    List<BatchSalesResponse> getBatchesForSales(String masp);
}