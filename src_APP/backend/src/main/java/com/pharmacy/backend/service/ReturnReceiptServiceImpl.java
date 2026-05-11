package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.ReturnReceiptRequest;
import com.pharmacy.backend.dto.ReturnReceiptResponse;
import com.pharmacy.backend.mapper.ReturnReceiptMapper;
import com.pharmacy.backend.model.Invoice;
import com.pharmacy.backend.model.InvoiceDetail;
import com.pharmacy.backend.model.ReturnReceipt;
import com.pharmacy.backend.model.ReturnReceiptDetail;
import com.pharmacy.backend.repository.InvoiceDetailRepository;
import com.pharmacy.backend.repository.InvoiceRepository;
import com.pharmacy.backend.repository.ReturnReceiptDetailRepository;
import com.pharmacy.backend.repository.ReturnReceiptRepository;
import jakarta.persistence.EntityManager; 
import jakarta.persistence.PersistenceContext; 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnReceiptServiceImpl implements ReturnReceiptService {

    private final ReturnReceiptRepository returnReceiptRepository;
    private final ReturnReceiptDetailRepository returnReceiptDetailRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;

    @PersistenceContext
    private EntityManager entityManager; 

    @Override
    @Transactional
    public ReturnReceiptResponse createReturnReceipt(ReturnReceiptRequest request) {
        // 1. CHỐT CHẶN: Kiểm tra nếu hóa đơn này đã được trả rồi thì không cho trả nữa
        if (returnReceiptRepository.existsByMahd(request.getMahd())) {
            throw new RuntimeException("Lỗi: Hóa đơn này đã được hoàn trả trước đó. Chỉ được trả duy nhất 1 lần!");
        }

        // 2. Tìm hóa đơn gốc
        Invoice invoice = invoiceRepository.findById(request.getMahd())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy hóa đơn gốc " + request.getMahd()));

        // 3. Kiểm tra đúng khách hàng mua đơn đó mới được trả
        if (!invoice.getMakh().equals(request.getMakh())) {
            throw new RuntimeException("Lỗi: Hóa đơn không thuộc về khách hàng " + request.getMakh());
        }

        // 4. Lấy TOÀN BỘ chi tiết từ hóa đơn gốc
        List<InvoiceDetail> cthdList = invoiceDetailRepository.findByMahd(request.getMahd());
        if (cthdList.isEmpty()) {
            throw new RuntimeException("Lỗi: Hóa đơn không có sản phẩm nào để trả!");
        }

        Map<String, InvoiceDetail> originalMap = cthdList.stream()
                .collect(Collectors.toMap(InvoiceDetail::getMalo, d -> d));

        // 5. Tạo vỏ Phiếu Trả
        ReturnReceipt phieuTra = new ReturnReceipt();
        phieuTra.setMahd(request.getMahd());
        phieuTra.setManv(request.getManv());
        phieuTra.setLydotra(request.getLydotra());
        phieuTra.setNgaytra(LocalDateTime.now());
        
        ReturnReceipt savedHeader = returnReceiptRepository.saveAndFlush(phieuTra);

        // 6. Lấy data từ Request (chỉ trả những món khách muốn) và chốt giá hoàn từ DB
        List<ReturnReceiptDetail> detailsToSave = request.getItems().stream().map(item -> {
            InvoiceDetail origDetail = originalMap.get(item.getMalo());
            
            if (origDetail == null) {
                throw new RuntimeException("Lỗi: Lô " + item.getMalo() + " không có trong hóa đơn gốc!");
            }
            if (item.getSl() > origDetail.getSl()) {
                throw new RuntimeException("Lỗi: Số lượng trả (" + item.getSl() + ") vượt quá số lượng đã mua!");
            }

            ReturnReceiptDetail detail = new ReturnReceiptDetail();
            detail.setMaptKh(savedHeader.getMaptKh());
            detail.setMalo(item.getMalo());
            detail.setSl(item.getSl()); 
            
            // QUAN TRỌNG: Lấy giá bán lúc mua từ DB làm giá hoàn, không lấy từ request
            detail.setDongiahoan(origDetail.getDongia()); 
            
            return detail;
        }).collect(Collectors.toList());

        // 7. Lưu chi tiết và kích hoạt Trigger Oracle
        List<ReturnReceiptDetail> savedDetails = returnReceiptDetailRepository.saveAllAndFlush(detailsToSave);

        // 8. "F5" dữ liệu từ Database để lấy giá trị mà Trigger vừa tính toán (Thành tiền, Tổng tiền)
        for (ReturnReceiptDetail d : savedDetails) {
            entityManager.refresh(d);
        }
        entityManager.refresh(savedHeader);

        // 9. Trả về kết quả
        return ReturnReceiptMapper.toResponse(savedHeader, savedDetails);
    }
}