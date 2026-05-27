package com.pharmacy.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pharmacy.backend.dto.InvoiceItemResponse;
import com.pharmacy.backend.dto.InvoiceListResponse;
import com.pharmacy.backend.dto.InvoiceRequest;
import com.pharmacy.backend.dto.InvoiceResponse;
import com.pharmacy.backend.mapper.InvoiceMapper;
import com.pharmacy.backend.model.Batch;
import com.pharmacy.backend.model.Customer;
import com.pharmacy.backend.model.Invoice;
import com.pharmacy.backend.model.InvoiceDetail;
import com.pharmacy.backend.model.Product;
import com.pharmacy.backend.repository.BatchRepository;
import com.pharmacy.backend.repository.CustomerRepository;
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
    private final CustomerRepository customerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {

        // 1. Tạo vỏ HOADON
        Invoice invoice = new Invoice();
        invoice.setManv(request.getManv());
        invoice.setMakh(request.getMakh());
        invoice.setDiemsudung(request.getDiemsudung() != null ? request.getDiemsudung() : 0);
        invoice.setNgayban(LocalDateTime.now());
        invoice.setTrangthai("HOANTAT");

        // Dùng saveAndFlush để Oracle sinh mã HD ngay lập tức
        Invoice savedInvoice = invoiceRepository.saveAndFlush(invoice);

        // 2. Tạo chi tiết hóa đơn
        List<InvoiceItemResponse> itemResponses = new ArrayList<>();

        for (var item : request.getItems()) {
            // Lấy thông tin Lô để kiểm tra tồn kho
            Batch batch = batchRepository.findById(item.getMalo())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lô: " + item.getMalo()));

            // Lấy thông tin sản phẩm
            Product product = productRepository.findById(item.getMasp())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            // ---- CHỐT CHẶN: Kiểm tra trạng thái sản phẩm ----
            if ("NGUNG_BAN".equals(product.getTrangthai())) {
                throw new RuntimeException("Sản phẩm '" + product.getTensanpham() + "' đã ngừng kinh doanh!");
            }
            // -------------------------------------------------

            if (batch.getSlsp() < item.getSl()) {
                throw new RuntimeException("Lô " + item.getMalo() + " không đủ hàng (còn " + batch.getSlsp() + ")");
            }

            InvoiceDetail detail = new InvoiceDetail();
            detail.setMahd(savedInvoice.getMahd());
            detail.setMalo(item.getMalo());
            detail.setSl(item.getSl());
            detail.setDongia(product.getGiaban());

            // Dùng saveAndFlush để đẩy dữ liệu xuống Oracle ngay lập tức cho Trigger tính thanhtien
            InvoiceDetail savedDetail = invoiceDetailRepository.saveAndFlush(detail);

            // Refresh để lấy thanhtien đã được Trigger tính toán
            entityManager.refresh(savedDetail);

            itemResponses.add(InvoiceItemResponse.builder()
                    .malo(savedDetail.getMalo())
                    .masp(item.getMasp())
                    .tensanpham(product.getTensanpham())
                    .sl(savedDetail.getSl())
                    .dongia(savedDetail.getDongia())
                    .thanhtien(savedDetail.getThanhtien())
                    .ghichu(savedDetail.getGhichu())
                    .build());
        }

        // 3. Ép Flush để các trigger tính tổng tiền trên bảng HOADON và DIEMTL kích hoạt
        invoiceRepository.flush();

        // 4. Lấy lại hóa đơn và refresh để có tongtien + tienthanhtoan mới nhất
        Invoice finalInvoice = invoiceRepository.findById(savedInvoice.getMahd())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        entityManager.refresh(finalInvoice);

        // 5. Lấy điểm tích lũy mới nhất của khách hàng (sau khi trigger cộng điểm chạy xong)
        Integer currentDiem = 0;
        if (request.getMakh() != null) {
            Customer customer = customerRepository.findById(request.getMakh())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

            entityManager.refresh(customer);
            currentDiem = (customer.getDiemtichluy() != null) 
                            ? customer.getDiemtichluy().intValue() 
                            : 0;
        }
        
        // 6. Ép cập nhật TIENTHANHTOAN = TONGTIEN - DIEMSUDUNG (an toàn)
        if (finalInvoice.getTongtien() != null) {
            double tienthanhtoan = finalInvoice.getTongtien() - 
                                  (request.getDiemsudung() != null ? request.getDiemsudung() : 0);
            
            finalInvoice.setTienthanhtoan(tienthanhtoan);
            invoiceRepository.save(finalInvoice);   // cập nhật lại
        }

        // 7. Trả về response
        return InvoiceMapper.toResponse(finalInvoice).toBuilder()
                .items(itemResponses)
                .build();
    }

    @Override
    public InvoiceResponse getInvoiceById(String mahd) {
        Invoice invoice = invoiceRepository.findById(mahd)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + mahd));

        List<InvoiceDetail> details = invoiceDetailRepository.findByMahd(mahd);
        List<InvoiceItemResponse> itemResponses = new ArrayList<>();

        for (InvoiceDetail detail : details) {
            Batch batch = batchRepository.findById(detail.getMalo())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lô: " + detail.getMalo()));

            Product product = productRepository.findById(batch.getMasp())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            itemResponses.add(InvoiceItemResponse.builder()
                    .malo(detail.getMalo())
                    .masp(batch.getMasp())
                    .tensanpham(product.getTensanpham())
                    .sl(detail.getSl())
                    .dongia(detail.getDongia())
                    .thanhtien(detail.getThanhtien())
                    .ghichu(detail.getGhichu())
                    .build());
        }

        return InvoiceMapper.toResponse(invoice).toBuilder()
                .items(itemResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceListResponse> getAllInvoices(String search) {
        // Lấy tất cả hóa đơn
        List<Invoice> invoices = invoiceRepository.findAll();

        // Lọc tìm kiếm theo mã HĐ hoặc mã KH
        if (search != null && !search.trim().isEmpty()) {
            String s = search.toLowerCase();
            invoices = invoices.stream()
                    .filter(i -> i.getMahd().toLowerCase().contains(s) || 
                                (i.getMakh() != null && i.getMakh().toLowerCase().contains(s)))
                    .collect(Collectors.toList());
        }

        List<InvoiceListResponse> result = new ArrayList<>();

        for (Invoice i : invoices) {
            String ten = "Khách lẻ";
            String sdt = "";
            
            // Lấy thêm thông tin khách hàng nếu không phải khách lẻ
            if (i.getMakh() != null && !i.getMakh().equals("KHACH_LE")) {
                Customer c = customerRepository.findById(i.getMakh()).orElse(null);
                if (c != null) {
                    ten = c.getTenkh();
                    sdt = c.getSdt();
                }
            }

            result.add(InvoiceListResponse.builder()
                    .mahd(i.getMahd())
                    .ngayban(i.getNgayban())
                    .tenkh(ten)
                    .sdt(sdt)
                    .tongtien(i.getTongtien())
                    .trangthai(i.getTrangthai())
                    // TRẢ VỀ ĐIỂM SỬ DỤNG CHO FRONTEND
                    .diemsudung(i.getDiemsudung() != null ? i.getDiemsudung() : 0) 
                    .build());
        }

        // Sắp xếp ngày mới nhất lên đầu để dễ theo dõi
        result.sort((a, b) -> {
            if (a.getNgayban() == null) return 1;
            if (b.getNgayban() == null) return -1;
            return b.getNgayban().compareTo(a.getNgayban());
        });

        return result;
    }
}