package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.ImportItemRequest;
import com.pharmacy.backend.dto.ImportReceiptRequest;
import com.pharmacy.backend.dto.ImportReceiptResponse;
import com.pharmacy.backend.mapper.WarehouseMapper;
import com.pharmacy.backend.model.*;
import com.pharmacy.backend.repository.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportReceiptServiceImpl implements ImportReceiptService {

    private final ImportReceiptRepository importReceiptRepository;
    private final ImportReceiptDetailRepository importReceiptDetailRepository;
    private final BatchRepository batchRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseMapper warehouseMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ImportReceiptResponse> getAllImportReceipts() {
        return importReceiptRepository.findAll().stream()
                .map(warehouseMapper::toImportReceiptResponse)
                .toList();
    }

    @Override
    @Transactional
    public ImportReceiptResponse createImportReceipt(ImportReceiptRequest request) {
        
        supplierRepository.findById(request.getMancc())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với mã: " + request.getMancc()));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Phiếu nhập không có mặt hàng nào!");
        }
        ImportReceipt receipt = new ImportReceipt();
        receipt.setManv(request.getManv());
        receipt.setMancc(request.getMancc());
        receipt.setNgaynhap(new Date());
        receipt.setTrangthai("KHOI_TAO");
        
        ImportReceipt savedReceipt = importReceiptRepository.save(receipt);
        String finalMapn = savedReceipt.getMapn();

        for (ImportItemRequest item : request.getItems()) {
            
            Batch newBatch = new Batch();
            newBatch.setMasp(item.getMasp());
            newBatch.setMakho(item.getMakho());
            newBatch.setNgaysx(item.getNgaysx());
            newBatch.setNgaynhap(new Date());
            newBatch.setHsd(item.getHsd());
            newBatch.setSlsp(item.getSl());
            newBatch.setTrangthai("ACTIVE");
            
            Batch savedBatch = batchRepository.save(newBatch);
            String finalMalo = savedBatch.getMalo();

            ImportReceiptDetail detail = new ImportReceiptDetail();
            detail.setMapn(finalMapn);
            detail.setMalo(finalMalo);
            detail.setSl(item.getSl());
            detail.setGianhap(item.getGianhap());
            detail.setDvt(item.getDvt());
            detail.setGhichu(item.getGhichu());
            
            importReceiptDetailRepository.saveAndFlush(detail);
        }

        entityManager.refresh(savedReceipt);

        savedReceipt.setTrangthai("HOANTAT");

        importReceiptRepository.saveAndFlush(savedReceipt);

        return warehouseMapper.toImportReceiptResponse(savedReceipt);
    }

    @Override
    public List<ImportReceiptDetail> getDetailsByMapn(String mapn) {
        return importReceiptDetailRepository.findByMapn(mapn);
    }
}