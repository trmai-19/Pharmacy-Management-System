package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.InventoryResponse;
import com.pharmacy.backend.mapper.WarehouseMapper;
import com.pharmacy.backend.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public List<InventoryResponse> getInventory() {
        return warehouseRepository.findAll().stream()
                .map(WarehouseMapper::toInventoryResponse)
                .toList();
    }

    @Override
    public List<InventoryResponse> getLowStockAlerts() {
        // Lấy danh sách tồn kho < 10 (Khớp với Trigger TRG_KHO_WARNING)
        return warehouseRepository.findBySltonLessThan(10).stream()
                .map(WarehouseMapper::toInventoryResponse)
                .toList();
    }
}