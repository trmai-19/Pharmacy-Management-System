package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.BatchSalesResponse;
import com.pharmacy.backend.model.Batch;
import com.pharmacy.backend.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchSalesServiceImpl implements BatchSalesService {

    private final BatchRepository batchRepository;

    @Override
    public List<BatchSalesResponse> getBatchesForSales(String masp) {
        // Lấy danh sách lô từ kho, ưu tiên cận date (HSD tăng dần)
        List<Batch> batches = batchRepository.findByMaspOrderByHsdAsc(masp);

        // Đóng gói vào DTO
        return batches.stream().map(b -> BatchSalesResponse.builder()
                .malo(b.getMalo())
                .nsx(b.getNgaysx())
                .hsd(b.getHsd())
                .sl(b.getSlsp())
                .trangthai(b.getTrangthai())
                .build()
        ).collect(Collectors.toList());
    }
}