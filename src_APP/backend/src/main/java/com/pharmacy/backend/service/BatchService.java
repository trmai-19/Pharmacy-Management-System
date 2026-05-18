package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.BatchResponse;
import com.pharmacy.backend.dto.WarehouseResponse;
import java.util.List;

public interface BatchService {
    List<BatchResponse> getExpiringSoonAlerts();
    List<BatchResponse> getBatchesByMasp(String masp);
    List<BatchResponse> getLowStockAlerts();
    List<WarehouseResponse> getAllWarehouses();
}