package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.InventoryResponse;
import com.pharmacy.backend.mapper.WarehouseMapper;
import com.pharmacy.backend.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    @Override
    public List<InventoryResponse> getInventory() {
        return warehouseRepository.findAll().stream()
                .map(warehouseMapper::toInventoryResponse)
                .toList();
    }

    @Override
    public List<InventoryResponse> getLowStockAlerts() {
        return warehouseRepository.findBySltonLessThan(10).stream()
                .map(warehouseMapper::toInventoryResponse)
                .toList();
    }
}