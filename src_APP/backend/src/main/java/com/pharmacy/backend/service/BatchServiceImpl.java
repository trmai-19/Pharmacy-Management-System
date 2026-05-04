package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.BatchResponse;
import com.pharmacy.backend.mapper.WarehouseMapper;
import com.pharmacy.backend.repository.BatchRepository;

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

    @Override
    public List<BatchResponse> getAllBatches() {
        return batchRepository.findAll().stream()
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
}