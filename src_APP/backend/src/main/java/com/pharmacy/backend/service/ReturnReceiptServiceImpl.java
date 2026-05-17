package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.ReturnReceiptRequest;
import com.pharmacy.backend.dto.ReturnReceiptResponse;
import com.pharmacy.backend.mapper.ReturnReceiptMapper;
import com.pharmacy.backend.model.Customer;
import com.pharmacy.backend.model.Invoice;
import com.pharmacy.backend.model.InvoiceDetail;
import com.pharmacy.backend.model.Product;
import com.pharmacy.backend.model.ReturnReceipt;
import com.pharmacy.backend.model.ReturnReceiptDetail;
import com.pharmacy.backend.repository.BatchRepository;
import com.pharmacy.backend.repository.CustomerRepository;
import com.pharmacy.backend.repository.InvoiceDetailRepository;
import com.pharmacy.backend.repository.InvoiceRepository;
import com.pharmacy.backend.repository.ProductRepository;
import com.pharmacy.backend.repository.ReturnReceiptDetailRepository;
import com.pharmacy.backend.repository.ReturnReceiptRepository;
import com.pharmacy.backend.dto.ReturnItemDetailResponse;
import com.pharmacy.backend.dto.ReturnItemRequest;
import com.pharmacy.backend.dto.ReturnReceiptListResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pharmacy.backend.model.Batch;

/**
 * ┌─────────────────────────────────────────────────────────────────────────────┐
 * │  PHÂN CHIA TRÁCH NHIỆM: Java vs Oracle Triggers                             │
 * ├──────────────────────────────────────┬──────────────────────────────────────┤
 * │  JAVA XỬ LÝ                          │  ORACLE TRIGGER TỰ ĐỘNG              │
 * ├──────────────────────────────────────┼──────────────────────────────────────┤
 * │ • Validate đầu vào (30 ngày, KH...)  │ • CTPT_KH.THANHTIEN = SL*DONGIAHOAN │
 * │ • Tính DONGIAHOAN (phân bổ giảm giá) │ • PHIEUTRA_KH.TONGTIENHOAN (tổng)   │
 * │ • Insert 1 dòng CONG_DIEM (hoàn X)   │ • Thu hồi điểm Y (TRG_RETURN_DIEMTL)│
 * │                                      │ • Cộng/trừ DIEMTICHLUY (TRG_DIEMTL) │
 * │                                      │ • Cập nhật TONGDOANHTHU + HANGTV    │
 * │                                      │ • Hoàn tồn kho (TRG_CTPT_KH_KHO)   │
 * │                                      │ • Sinh MADTL, MAPT_KH tự động       │
 * └──────────────────────────────────────┴──────────────────────────────────────┘
 *
 * CHÍNH SÁCH ĐIỂM (đọc từ trigger TRG_HOADON_AUTO_LOG_DIEM & TRG_RETURN_DIEMTL):
 * Tích điểm : FLOOR(TONGTIEN  × 0.01)  →  1 điểm mỗi 100 VNĐ
 * Quy đổi   : 1 điểm = 1 VNĐ giảm giá →  TIENTHANHTOAN = TONGTIEN - DIEMSUDUNG
 * Thu hồi Y : FLOOR(TONGTIENHOAN × 0.01) do trigger tự xử lý khi insert CTPT_KH
 * Hoàn X    : FLOOR(tỷLệMónHàng × DIEMSUDUNG) — Java insert CONG_DIEM vào DIEMTL
 */
@Service
@RequiredArgsConstructor
public class ReturnReceiptServiceImpl implements ReturnReceiptService {

    private static final int MAX_RETURN_DAYS = 30;

    private final ReturnReceiptRepository returnReceiptRepository;
    private final ReturnReceiptDetailRepository returnReceiptDetailRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final BatchRepository batchRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ReturnReceiptResponse createReturnReceipt(ReturnReceiptRequest request) {

        // =====================================================================
        // BƯỚC 1: CÁC KIỂM TRA ĐẦU VÀO
        // =====================================================================

        // 1a. Mỗi hóa đơn chỉ được trả đúng 1 lần
        if (returnReceiptRepository.existsByMahd(request.getMahd())) {
            throw new RuntimeException(
                "Lỗi: Hóa đơn này đã được hoàn trả trước đó. Chỉ được trả duy nhất 1 lần!");
        }

        // 1b. Tìm hóa đơn gốc
        Invoice invoice = invoiceRepository.findById(request.getMahd())
                .orElseThrow(() -> new RuntimeException(
                    "Lỗi: Không tìm thấy hóa đơn gốc " + request.getMahd()));

        // 1c. Kiểm tra thời hạn trả hàng
        LocalDateTime now = LocalDateTime.now();
        if (invoice.getNgayban() == null
                || invoice.getNgayban().isBefore(now.minusDays(MAX_RETURN_DAYS))) {
            throw new RuntimeException(
                "Lỗi: Chỉ được trả hàng trong vòng " + MAX_RETURN_DAYS
                + " ngày kể từ ngày mua. Hóa đơn đã quá hạn!");
        }

        // 1d. Kiểm tra đúng khách hàng của hóa đơn
        if (!invoice.getMakh().equals(request.getMakh())) {
            throw new RuntimeException(
                "Lỗi: Hóa đơn không thuộc về khách hàng " + request.getMakh());
        }

        // 1e. Lấy toàn bộ chi tiết hóa đơn gốc
        List<InvoiceDetail> cthdList = invoiceDetailRepository.findByMahd(request.getMahd());
        if (cthdList.isEmpty()) {
            throw new RuntimeException("Lỗi: Hóa đơn không có sản phẩm nào để trả!");
        }

        // 1f. Danh sách mặt hàng muốn trả không được rỗng
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Lỗi: Vui lòng chọn ít nhất một sản phẩm cần trả!");
        }

        // =====================================================================
        // BƯỚC 2: CHUẨN BỊ DỮ LIỆU TÀI CHÍNH TỪ HÓA ĐƠN GỐC
        // =====================================================================

        Map<String, InvoiceDetail> originalMap = cthdList.stream()
                .collect(Collectors.toMap(InvoiceDetail::getMalo, d -> d));

        double tongTienTruocGiam = invoice.getTongtien()      != null ? invoice.getTongtien()      : 0.0;
        double tienThanhToan     = invoice.getTienthanhtoan() != null ? invoice.getTienthanhtoan() : 0.0;
        int    diemDaTieu        = invoice.getDiemsudung()    != null ? invoice.getDiemsudung()    : 0;

        if (tongTienTruocGiam <= 0) {
            throw new RuntimeException("Lỗi: Dữ liệu hóa đơn không hợp lệ (tổng tiền = 0)!");
        }

        double tyLeThanhToan = tienThanhToan / tongTienTruocGiam;

        // =====================================================================
        // BƯỚC 3: TẠO VỎ PHIẾU TRẢ (HEADER)
        // =====================================================================

        ReturnReceipt phieuTra = new ReturnReceipt();
        phieuTra.setMahd(request.getMahd());
        phieuTra.setManv(request.getManv());
        phieuTra.setLydotra(request.getLydotra());
        phieuTra.setNgaytra(now);

        ReturnReceipt savedHeader = returnReceiptRepository.saveAndFlush(phieuTra);

        // =====================================================================
        // BƯỚC 4: XỬ LÝ TỪNG MẶT HÀNG TRẢ
        // =====================================================================

        List<ReturnReceiptDetail> detailsToSave = new ArrayList<>();
        double tongDiemXHoanRaw = 0.0;

        for (ReturnItemRequest item : request.getItems()) {
            InvoiceDetail origDetail = originalMap.get(item.getMalo());

            if (origDetail == null) {
                throw new RuntimeException(
                    "Lỗi: Lô '" + item.getMalo() + "' không có trong hóa đơn gốc!");
            }

            if (item.getSl() <= 0) {
                throw new RuntimeException(
                    "Lỗi: Số lượng trả của lô '" + item.getMalo() + "' phải lớn hơn 0!");
            }
            if (item.getSl() > origDetail.getSl()) {
                throw new RuntimeException(
                    "Lỗi: Số lượng trả (" + item.getSl() + ") của lô '" + item.getMalo()
                    + "' vượt quá số lượng đã mua (" + origDetail.getSl() + ")!");
            }

            double donGia  = origDetail.getDongia();
            int    soLuong = item.getSl();
            double donGiaHoan = donGia * tyLeThanhToan;

            double tiLeMonHang = (donGia * soLuong) / tongTienTruocGiam;
            tongDiemXHoanRaw += tiLeMonHang * diemDaTieu;

            ReturnReceiptDetail detail = new ReturnReceiptDetail();
            detail.setMaptKh(savedHeader.getMaptKh());
            detail.setMalo(item.getMalo());
            detail.setSl(soLuong);
            detail.setDongiahoan(donGiaHoan);
            detailsToSave.add(detail);
        }

        List<ReturnReceiptDetail> savedDetails =
                returnReceiptDetailRepository.saveAllAndFlush(detailsToSave);

        int diemXHoan = (int) Math.floor(tongDiemXHoanRaw);

        if (diemXHoan > 0) {
            entityManager.createNativeQuery(
                    "INSERT INTO DIEMTL (MAKH, MAHD, LOAIGD, DIEMTHAYDOI, NGAYGD, GHICHU) "
                    + "VALUES (?, ?, 'CONG_DIEM', ?, SYSDATE, ?)")
                    .setParameter(1, request.getMakh())
                    .setParameter(2, request.getMahd())
                    .setParameter(3, diemXHoan)
                    .setParameter(4, "Hoàn điểm tích lũy do trả hàng đơn " + request.getMahd())
                    .executeUpdate();
        }

        for (ReturnReceiptDetail d : savedDetails) {
            entityManager.refresh(d);
        }
        entityManager.refresh(savedHeader);

        return ReturnReceiptMapper.toResponse(savedHeader, savedDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnReceiptListResponse> getAllReturnReceipts(String search) {
        List<ReturnReceipt> receipts = returnReceiptRepository.searchReturnReceipts(search);
        List<ReturnReceiptListResponse> responseList = new ArrayList<>();

        for (ReturnReceipt r : receipts) {
            String tenkh = "Khách lẻ";
            Invoice invoice = invoiceRepository.findById(r.getMahd()).orElse(null);
            if (invoice != null && invoice.getMakh() != null && !invoice.getMakh().equals("KHACH_LE")) {
                Customer customer = customerRepository.findById(invoice.getMakh()).orElse(null);
                if (customer != null) {
                    tenkh = customer.getTenkh();
                }
            }

            List<ReturnReceiptDetail> details = returnReceiptDetailRepository.findByMaptKh(r.getMaptKh());
            List<InvoiceDetail> origDetails = invoiceDetailRepository.findByMahd(r.getMahd()); 
            
            int tongSl = 0;
            List<String> productNames = new ArrayList<>();
            double tongDiemXHoanRaw = 0.0;
            
            double tongTienTruocGiam = (invoice != null && invoice.getTongtien() != null) ? invoice.getTongtien() : 0.0;
            int diemDaTieu = (invoice != null && invoice.getDiemsudung() != null) ? invoice.getDiemsudung() : 0;

            for (ReturnReceiptDetail d : details) {
                tongSl += d.getSl();
                Batch batch = batchRepository.findById(d.getMalo()).orElse(null);
                if (batch != null) {
                    Product product = productRepository.findById(batch.getMasp()).orElse(null);
                    if (product != null && !productNames.contains(product.getTensanpham())) {
                        productNames.add(product.getTensanpham());
                    }
                }
                
                // TÍNH TOÁN ĐIỂM HOÀN ĐỂ TRẢ VỀ FRONTEND (KHÔNG GHI DATABASE Ở ĐÂY)
                if (tongTienTruocGiam > 0 && diemDaTieu > 0) {
                    for (InvoiceDetail od : origDetails) {
                        if (od.getMalo().equals(d.getMalo())) {
                            double tiLeMonHang = (od.getDongia() * d.getSl()) / tongTienTruocGiam;
                            tongDiemXHoanRaw += tiLeMonHang * diemDaTieu;
                            break;
                        }
                    }
                }
            }

            String sanPhamTomTat = String.join(", ", productNames);

            responseList.add(ReturnReceiptListResponse.builder()
                    .maptKh(r.getMaptKh())
                    .ngaytra(r.getNgaytra())
                    .mahd(r.getMahd())
                    .tenkh(tenkh)
                    .sanPhamTomTat(sanPhamTomTat)
                    .tongSl(tongSl)
                    .lydotra(r.getLydotra())
                    .tongtienhoan(r.getTongtienhoan())
                    .diemhoan((int) Math.floor(tongDiemXHoanRaw)) // <-- MAP ĐIỂM HOÀN VÀO ĐÂY
                    .build());
        }
        return responseList;
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnReceiptResponse getReturnReceiptById(String maptKh) {
        ReturnReceipt r = returnReceiptRepository.findById(maptKh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu trả hàng với mã: " + maptKh));

        List<ReturnReceiptDetail> details = returnReceiptDetailRepository.findByMaptKh(maptKh);
        List<ReturnItemDetailResponse> itemResponses = new ArrayList<>();

        for (ReturnReceiptDetail d : details) {
            String masp = "";
            String tensanpham = "Chưa rõ";
            
            Batch batch = batchRepository.findById(d.getMalo()).orElse(null);
            if (batch != null) {
                masp = batch.getMasp();
                Product product = productRepository.findById(masp).orElse(null);
                if (product != null) {
                    tensanpham = product.getTensanpham();
                }
            }

            itemResponses.add(ReturnItemDetailResponse.builder()
                    .masp(masp)
                    .tensanpham(tensanpham)
                    .malo(d.getMalo())
                    .sl(d.getSl())
                    .dongiahoan(d.getDongiahoan())
                    .thanhtien(d.getThanhtien())
                    .build());
        }

        return ReturnReceiptResponse.builder()
                .maptKh(r.getMaptKh())
                .mahd(r.getMahd())
                .manv(r.getManv())
                .ngaytra(r.getNgaytra())
                .lydotra(r.getLydotra())
                .tongtienhoan(r.getTongtienhoan())
                .items(itemResponses)
                .build();
    }
}