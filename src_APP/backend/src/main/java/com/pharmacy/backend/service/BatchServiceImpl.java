package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.BatchResponse;
import com.pharmacy.backend.mapper.WarehouseMapper;
import com.pharmacy.backend.repository.BatchRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Date;

@Service
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;

    public BatchServiceImpl(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @Override
    public List<BatchResponse> getAllBatches() {
        return batchRepository.findAll().stream()
                .map(WarehouseMapper::toBatchResponse)
                .toList();
    }
    @Override
    public List<BatchResponse> getExpiringSoonAlerts() {
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        
        cal.add(Calendar.MONTH, 3);
        Date threeMonthsLater = cal.getTime();

        return batchRepository.findByHsdBetween(today, threeMonthsLater).stream()
                .map(WarehouseMapper::toBatchResponse)
                .toList();
    }
}