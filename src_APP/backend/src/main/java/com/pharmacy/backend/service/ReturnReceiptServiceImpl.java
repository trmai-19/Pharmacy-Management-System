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
import com.pharmacy.backend.dto.ReturnItemRequest;
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

/**
 * ┌─────────────────────────────────────────────────────────────────────────────┐
 * │  PHÂN CHIA TRÁCH NHIỆM: Java vs Oracle Triggers                             │
 * ├──────────────────────────────────────┬──────────────────────────────────────┤
 * │  JAVA XỬ LÝ                          │  ORACLE TRIGGER TỰ ĐỘNG              │
 * ├──────────────────────────────────────┼──────────────────────────────────────┤
 * │ • Validate đầu vào (30 ngày, KH...)  │ • CTPT_KH.THANHTIEN = SL*DONGIAHOAN │
 * │ • Tính DONGIAHOAN (phân bổ giảm giá) │ • PHIEUTRA_KH.TONGTIENHOAN (tổng)   │
 * │ • Insert 1 dòng CONG_DIEM (hoàn X)  │ • Thu hồi điểm Y (TRG_RETURN_DIEMTL)│
 * │                                      │ • Cộng/trừ DIEMTICHLUY (TRG_DIEMTL) │
 * │                                      │ • Cập nhật TONGDOANHTHU + HANGTV    │
 * │                                      │ • Hoàn tồn kho (TRG_CTPT_KH_KHO)   │
 * │                                      │ • Sinh MADTL, MAPT_KH tự động       │
 * └──────────────────────────────────────┴──────────────────────────────────────┘
 *
 * CHÍNH SÁCH ĐIỂM (đọc từ trigger TRG_HOADON_AUTO_LOG_DIEM & TRG_RETURN_DIEMTL):
 *   Tích điểm : FLOOR(TONGTIEN  × 0.01)  →  1 điểm mỗi 100 VNĐ
 *   Quy đổi   : 1 điểm = 1 VNĐ giảm giá →  TIENTHANHTOAN = TONGTIEN - DIEMSUDUNG
 *   Thu hồi Y : FLOOR(TONGTIENHOAN × 0.01) do trigger tự xử lý khi insert CTPT_KH
 *   Hoàn X    : FLOOR(tỷLệMónHàng × DIEMSUDUNG) — Java insert CONG_DIEM vào DIEMTL
 */
@Service
@RequiredArgsConstructor
public class ReturnReceiptServiceImpl implements ReturnReceiptService {

    /** Giới hạn ngày được phép trả hàng kể từ ngày mua */
    private static final int MAX_RETURN_DAYS = 30;

    private final ReturnReceiptRepository returnReceiptRepository;
    private final ReturnReceiptDetailRepository returnReceiptDetailRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;

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

        //  TONGTIEN     = Tổng giá trị hóa đơn TRƯỚC khi giảm  → dùng làm mẫu số tỷ lệ
        //  DIEMSUDUNG   = Số điểm đã dùng = Số VNĐ được giảm   (vì 1 điểm = 1 VNĐ)
        //  TIENTHANHTOAN = TONGTIEN - DIEMSUDUNG                (do trigger TRG_HOADON_CHECK_DIEM)
        double tongTienTruocGiam = invoice.getTongtien()      != null ? invoice.getTongtien()      : 0.0;
        double tienThanhToan     = invoice.getTienthanhtoan() != null ? invoice.getTienthanhtoan() : 0.0;
        int    diemDaTieu        = invoice.getDiemsudung()    != null ? invoice.getDiemsudung()    : 0;

        if (tongTienTruocGiam <= 0) {
            throw new RuntimeException("Lỗi: Dữ liệu hóa đơn không hợp lệ (tổng tiền = 0)!");
        }

        //  Tỷ lệ thanh toán thực tế:
        //    tyLeThanhToan = TIENTHANHTOAN / TONGTIEN
        //  → donGiaHoan = donGia × tyLeThanhToan
        //    (công thức rút gọn của phân bổ giảm giá theo tỷ lệ từng món hàng)
        double tyLeThanhToan = tienThanhToan / tongTienTruocGiam;

        // =====================================================================
        // BƯỚC 3: TẠO VỎ PHIẾU TRẢ (HEADER)
        // flush ngay → Oracle sinh MAPT_KH trước khi insert các dòng chi tiết
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

        // Tích lũy điểm X cần hoàn trả dạng raw (làm tròn một lần sau cùng → tránh dồn sai số)
        double tongDiemXHoanRaw = 0.0;

        for (ReturnItemRequest item : request.getItems()) {
            InvoiceDetail origDetail = originalMap.get(item.getMalo());

            // Validate: lô hàng có trong hóa đơn gốc không?
            if (origDetail == null) {
                throw new RuntimeException(
                    "Lỗi: Lô '" + item.getMalo() + "' không có trong hóa đơn gốc!");
            }

            // Validate: số lượng trả hợp lệ
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

            // -----------------------------------------------------------------
            // TÍNH ĐƠN GIÁ HOÀN
            //
            // Nguyên tắc: khách đã dùng điểm giảm giá trên toàn bộ hóa đơn,
            // nên khi trả 1 món, phần giảm giá tương ứng phải được trừ lại.
            //
            //   Tỷ lệ món hàng    = (donGia × sl) / TONGTIEN
            //   Giảm giá phân bổ  = tỷLệ × DIEMSUDUNG
            //   Đơn giá hoàn/đơn  = donGia - giảmPhânBổ / sl
            //
            // Rút gọn: donGiaHoan = donGia × (TIENTHANHTOAN / TONGTIEN)
            //
            // Trigger TRG_CTPT_KH_THANHTIEN sẽ tính: THANHTIEN = SL × DONGIAHOAN
            // -----------------------------------------------------------------
            double donGiaHoan = donGia * tyLeThanhToan;

            // -----------------------------------------------------------------
            // TÍCH LŨY ĐIỂM X CẦN HOÀN (raw, làm tròn sau)
            //
            //   tiLeMonHang = (donGia × sl) / TONGTIEN
            //   Điểm X của món này = tiLeMonHang × diemDaTieu
            //
            // (Điểm Y — thu hồi điểm đã tặng — sẽ do trigger TRG_RETURN_DIEMTL xử lý)
            // -----------------------------------------------------------------
            double tiLeMonHang = (donGia * soLuong) / tongTienTruocGiam;
            tongDiemXHoanRaw += tiLeMonHang * diemDaTieu;

            // Tạo dòng chi tiết phiếu trả
            ReturnReceiptDetail detail = new ReturnReceiptDetail();
            detail.setMaptKh(savedHeader.getMaptKh());
            detail.setMalo(item.getMalo());
            detail.setSl(soLuong);
            detail.setDongiahoan(donGiaHoan);
            detailsToSave.add(detail);
        }

        // =====================================================================
        // BƯỚC 5: LƯU CHI TIẾT → KÍCH HOẠT CHUỖI TRIGGER ORACLE
        //
        // Sau lệnh saveAllAndFlush này, Oracle tự động kích hoạt:
        //   TRG_CTPT_KH_THANHTIEN     → THANHTIEN = SL × DONGIAHOAN (từng dòng)
        //   TRG_CTPT_KH_HOAN_KHO      → cộng lại tồn kho cho từng lô
        //   TRG_CTPT_KH_SYNC_TOTAL    → cộng dồn TONGTIENHOAN lên header
        //   TRG_RETURN_DIEMTL         → thu hồi Y điểm → insert TRU_DIEM vào DIEMTL
        //   TRG_DIEMTL_SYNC_KHACHHANG → trừ Y điểm khỏi KHACHHANG.DIEMTICHLUY
        //   TRG_PHIEUTRA_KH_UPDATE_*  → trừ TONGDOANHTHU, cập nhật HANGTV
        // =====================================================================
        List<ReturnReceiptDetail> savedDetails =
                returnReceiptDetailRepository.saveAllAndFlush(detailsToSave);

        // =====================================================================
        // BƯỚC 6: HOÀN X ĐIỂM CHO KHÁCH (việc duy nhất Java phải tự làm cho điểm)
        //
        // Trigger chỉ tự thu hồi điểm tặng (Y), không tự hoàn điểm đã tiêu (X).
        // → Java insert 1 dòng CONG_DIEM vào DIEMTL.
        // → TRG_AUTO_ID_DIEMTL sẽ tự sinh MADTL (vì truyền null).
        // → TRG_DIEMTL_SYNC_KHACHHANG sẽ tự cộng vào KHACHHANG.DIEMTICHLUY.
        // → KHÔNG gọi customerRepository.save() — trigger đã lo việc đó.
        // =====================================================================
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
            // TRG_AUTO_ID_DIEMTL          → sinh MADTL tự động
            // TRG_DIEMTL_SYNC_KHACHHANG   → cộng diemXHoan vào DIEMTICHLUY
        }

        // =====================================================================
        // BƯỚC 7: REFRESH — lấy giá trị trigger đã tính về entity (THANHTIEN, TONGTIENHOAN)
        // =====================================================================
        for (ReturnReceiptDetail d : savedDetails) {
            entityManager.refresh(d);
        }
        entityManager.refresh(savedHeader);

        // =====================================================================
        // BƯỚC 8: TRẢ KẾT QUẢ
        // =====================================================================
        return ReturnReceiptMapper.toResponse(savedHeader, savedDetails);
    }
}