package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.InventoryReportResponse;
import java.util.List;

public interface InventoryService {
    InventoryReportResponse getInventoryDashboard(Integer year, Integer quarter, String productGroup, String customerType);
    List<String> getFilterCategories();
}