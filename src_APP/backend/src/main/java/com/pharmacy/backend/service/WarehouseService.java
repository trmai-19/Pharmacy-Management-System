package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.InventoryResponse;

import java.util.List;

public interface WarehouseService {
    List<InventoryResponse> getInventory();
    List<InventoryResponse> getLowStockAlerts();
}