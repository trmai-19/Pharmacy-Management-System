package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.ImportItemRequest;
import com.pharmacy.backend.dto.ImportReceiptRequest;
import com.pharmacy.backend.dto.ImportReceiptResponse;
import com.pharmacy.backend.mapper.WarehouseMapper;
import com.pharmacy.backend.model.*;
import com.pharmacy.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ImportReceiptServiceImpl implements ImportReceiptService {

    private final ImportReceiptRepository importReceiptRepository;
    private final ImportReceiptDetailRepository importReceiptDetailRepository;
    private final BatchRepository batchRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;

    public ImportReceiptServiceImpl(
            ImportReceiptRepository importReceiptRepository,
            ImportReceiptDetailRepository importReceiptDetailRepository,
            BatchRepository batchRepository,
            WarehouseRepository warehouseRepository,
            SupplierRepository supplierRepository) {
        this.importReceiptRepository = importReceiptRepository;
        this.importReceiptDetailRepository = importReceiptDetailRepository;
        this.batchRepository = batchRepository;
        this.warehouseRepository = warehouseRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<ImportReceiptResponse> getAllImportReceipts() {
        return importReceiptRepository.findAll().stream()
                .map(WarehouseMapper::toImportReceiptResponse)
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
        receipt.setTrangthai("KHOI TAO");
        
        ImportReceipt savedReceipt = importReceiptRepository.save(receipt);
        String finalMapn = savedReceipt.getMapn();

        for (ImportItemRequest item : request.getItems()) {
            
            Batch newBatch = new Batch();
            newBatch.setMasp(item.getMasp());
            newBatch.setMadm(item.getMadm());
            newBatch.setNgaysx(item.getNgaysx());
            newBatch.setNgaynhap(new Date());
            newBatch.setHsd(item.getHsd());
            newBatch.setSlsp(item.getSl());
            newBatch.setTrangthai("ACTIVE");
            
            Batch savedBatch = batchRepository.save(newBatch);
            String finalMalo = savedBatch.getMalo();

            Warehouse warehouse = new Warehouse();
            warehouse.setMalo(finalMalo);
            warehouse.setSlton(0);
            warehouse.setDvsp(item.getDvt());
            warehouseRepository.save(warehouse);

            ImportReceiptDetail detail = new ImportReceiptDetail();
            detail.setMapn(finalMapn);
            detail.setMalo(finalMalo);
            detail.setSl(item.getSl());
            detail.setGianhap(item.getGianhap());
            detail.setDvt(item.getDvt());
            detail.setGhichu(item.getGhichu());
            
            importReceiptDetailRepository.save(detail);
        }

        savedReceipt.setTrangthai("HOANTAT");
        importReceiptRepository.save(savedReceipt);

        ImportReceipt finalReceipt = importReceiptRepository.findById(finalMapn).get();

        return WarehouseMapper.toImportReceiptResponse(finalReceipt);
    }
}