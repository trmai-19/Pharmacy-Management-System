package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.SupplierReturnRequest;
import com.pharmacy.backend.dto.SupplierReturnResponse;
import com.pharmacy.backend.mapper.SupplierReturnMapper;
import com.pharmacy.backend.model.ImportReceiptDetail;
import com.pharmacy.backend.model.SupplierReturn;
import com.pharmacy.backend.model.SupplierReturnDetail;
import com.pharmacy.backend.repository.ImportReceiptDetailRepository;
import com.pharmacy.backend.repository.ImportReceiptRepository;
import com.pharmacy.backend.repository.SupplierReturnDetailRepository;
import com.pharmacy.backend.repository.SupplierReturnRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierReturnServiceImpl implements SupplierReturnService {

    private final SupplierReturnRepository supplierReturnRepository;
    private final SupplierReturnDetailRepository supplierReturnDetailRepository;
    private final ImportReceiptRepository importReceiptRepository;
    private final ImportReceiptDetailRepository importReceiptDetailRepository;
    private final SupplierReturnMapper supplierReturnMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public SupplierReturnResponse createSupplierReturn(SupplierReturnRequest request) {
        
        if (!importReceiptRepository.existsById(request.getMapn())) {
            throw new RuntimeException("Lỗi: Không tìm thấy phiếu nhập kho gốc có mã: " + request.getMapn());
        }

        List<ImportReceiptDetail> originalDetails = importReceiptDetailRepository.findByMapn(request.getMapn());
        if (originalDetails.isEmpty()) {
            throw new RuntimeException("Lỗi: Phiếu nhập gốc không có dữ liệu sản phẩm để đối soát!");
        }

        Map<String, ImportReceiptDetail> originalMap = originalDetails.stream()
                .collect(Collectors.toMap(ImportReceiptDetail::getMalo, d -> d));


        SupplierReturn phieuTra = new SupplierReturn();
        phieuTra.setMapn(request.getMapn());
        phieuTra.setManv(request.getManv());
        phieuTra.setLydotra(request.getLydotra());
        phieuTra.setNgaytra(new Date());
        
        SupplierReturn savedHeader = supplierReturnRepository.saveAndFlush(phieuTra);


        List<SupplierReturnDetail> detailsToSave = request.getItems().stream().map(item -> {
            ImportReceiptDetail origDetail = originalMap.get(item.getMalo());

            if (origDetail == null) {
                throw new RuntimeException("Lỗi: Lô hàng " + item.getMalo() + " không tồn tại trong phiếu nhập gốc!");
            }

            if (item.getSl() > origDetail.getSl()) {
                throw new RuntimeException("Lỗi: Số lượng trả của lô " + item.getMalo() + 
                                         " (" + item.getSl() + ") vượt quá số lượng đã nhập (" + origDetail.getSl() + ")!");
            }

            SupplierReturnDetail detail = new SupplierReturnDetail();
            detail.setMaptNcc(savedHeader.getMaptNcc());
            detail.setMalo(item.getMalo());
            detail.setSl(item.getSl());
            
            detail.setDongiatra(origDetail.getGianhap()); 
            
            return detail;
        }).collect(Collectors.toList());

        List<SupplierReturnDetail> savedDetails = supplierReturnDetailRepository.saveAllAndFlush(detailsToSave);

        for (SupplierReturnDetail d : savedDetails) {
            entityManager.refresh(d);
        }
        entityManager.refresh(savedHeader);

        return supplierReturnMapper.toResponse(savedHeader, savedDetails);
    }

    @Override
    public List<SupplierReturnResponse> getReturnsByReceiptId(String mapn) {
        // Tìm tất cả phiếu trả có mã mapn gốc này
        List<SupplierReturn> returns = supplierReturnRepository.findByMapn(mapn);
        
        return returns.stream().map(phieuTra -> {
            // Lấy chi tiết từng phiếu trả
            List<SupplierReturnDetail> details = supplierReturnDetailRepository.findByMaptNcc(phieuTra.getMaptNcc());
            return supplierReturnMapper.toResponse(phieuTra, details);
        }).collect(Collectors.toList());
    }
}