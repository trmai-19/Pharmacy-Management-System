package com.pharmacy.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pharmacy.backend.dto.InvoiceItemResponse;
import com.pharmacy.backend.dto.InvoiceRequest;
import com.pharmacy.backend.dto.InvoiceResponse;
import com.pharmacy.backend.mapper.InvoiceMapper;
import com.pharmacy.backend.model.Batch;
import com.pharmacy.backend.model.Invoice;
import com.pharmacy.backend.model.InvoiceDetail;
import com.pharmacy.backend.model.Product;
import com.pharmacy.backend.repository.BatchRepository;
import com.pharmacy.backend.repository.InvoiceDetailRepository;
import com.pharmacy.backend.repository.InvoiceRepository;
import com.pharmacy.backend.repository.ProductRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public InvoiceResponse createInvoice(InvoiceRequest request) {

        Invoice invoice = new Invoice();
        invoice.setManv(request.getManv());
        invoice.setMakh(request.getMakh());
        invoice.setDiemsudung(request.getDiemsudung() != null ? request.getDiemsudung() : 0);
        invoice.setNgayban(LocalDateTime.now());
        invoice.setTrangthai("HOANTAT");

        Invoice savedInvoice = invoiceRepository.save(invoice);

        List<InvoiceDetail> details = new ArrayList<>();

        for (var item : request.getItems()) {
            List<Batch> lots = batchRepository.findByMasp(item.getMasp());

            Batch chosenBatch = lots.stream()
                    .filter((Batch b) -> b.getSlsp() >= item.getSl()
                            && b.getHsd() != null
                            && b.getHsd().after(new java.util.Date()))         
                    .min(Comparator.comparing((Batch b) -> b.getHsd()))       
                    .orElseThrow(() -> new RuntimeException(
                        "Không đủ hàng cho sản phẩm " + item.getMasp() + 
                        " (kiểm tra tồn kho hoặc hạn sử dụng)"));

            Product product = productRepository.findById(item.getMasp())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            InvoiceDetail detail = new InvoiceDetail();
            detail.setMahd(savedInvoice.getMahd());
            detail.setMalo(chosenBatch.getMalo());
            detail.setSl(item.getSl());
            detail.setDongia(product.getGiaban());
           

            details.add(detail);
        }

        invoiceDetailRepository.saveAll(details);

        invoiceRepository.flush();
        invoiceDetailRepository.flush();

        Invoice finalInvoice = invoiceRepository.findById(savedInvoice.getMahd())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        entityManager.refresh(finalInvoice);

        return InvoiceMapper.toResponse(finalInvoice);
    }
    @Override
    public List<InvoiceItemResponse> getInvoiceDetails(String mahd) {
        List<InvoiceDetail> details = invoiceDetailRepository.findByMahd(mahd);
        
        if (details.isEmpty()) {
            throw new RuntimeException("Không tìm thấy chi tiết hóa đơn với mã: " + mahd);
        }

        List<InvoiceItemResponse> itemResponses = new ArrayList<>();

        for (InvoiceDetail detail : details) {
            Batch batch = batchRepository.findById(detail.getMalo())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lô: " + detail.getMalo()));

            Product product = productRepository.findById(batch.getMasp())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            InvoiceItemResponse item = InvoiceItemResponse.builder()
                    .malo(detail.getMalo())
                    .masp(batch.getMasp())
                    .tensanpham(product.getTensanpham())
                    .sl(detail.getSl())
                    .dongia(detail.getDongia())
                    .thanhtien(detail.getThanhtien())
                    .ghichu(detail.getGhichu())
                    .build();

            itemResponses.add(item);
        }

    return itemResponses;
}
}