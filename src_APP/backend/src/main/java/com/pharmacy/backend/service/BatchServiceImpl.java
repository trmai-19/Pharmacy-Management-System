package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.BatchResponse;
import com.pharmacy.backend.dto.WarehouseResponse;
import com.pharmacy.backend.mapper.WarehouseMapper;
import com.pharmacy.backend.repository.BatchRepository;
import com.pharmacy.backend.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;
    private final WarehouseMapper warehouseMapper;
    private final WarehouseRepository warehouseRepository;

    @Override
    public List<BatchResponse> getBatchesByMasp(String masp) {
        return batchRepository.findBatchesWithPriceByMasp(masp);
    }

    @Override
    public List<BatchResponse> getLowStockAlerts() {
        return batchRepository.findBySlspBetween(1, 10).stream()
                .map(warehouseMapper::toBatchResponse)
                .toList();
    }

    @Override
    public List<BatchResponse> getExpiringSoonAlerts() {
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        
        cal.add(Calendar.MONTH, 3);
        Date threeMonthsLater = cal.getTime();

        return batchRepository.findByHsdBetween(today, threeMonthsLater).stream()
                .map(warehouseMapper::toBatchResponse)
                .toList();
    }

    @Override
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(w -> new WarehouseResponse(w.getMakho(), w.getLoaikho()))
                .toList();
    }
}