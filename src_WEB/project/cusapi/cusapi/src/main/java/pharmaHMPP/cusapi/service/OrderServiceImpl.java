package pharmaHMPP.cusapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pharmaHMPP.cusapi.dto.HoaDonChiTietDto;
import pharmaHMPP.cusapi.dto.OrderRequest;
import pharmaHMPP.cusapi.dto.OrderResponse;
import pharmaHMPP.cusapi.entity.*;
import pharmaHMPP.cusapi.repository.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final HoaDonRepository hoaDonRepo;
    private final CTHDRepository cthdRepo;
    private final BatchRepository batchRepo;
    private final SanPhamRepository sanPhamRepo;
    private final KhachHangRepository khachHangRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public OrderResponse createOrder(String maTK, OrderRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Don hang phai co it nhat mot san pham.");
        }

        Set<String> seenProducts = new HashSet<>();
        for (var item : request.getItems()) {
            if (item.getMasp() == null || item.getMasp().trim().isEmpty()) {
                throw new RuntimeException("Ma san pham khong hop le.");
            }
            if (item.getSl() == null || item.getSl() <= 0) {
                throw new RuntimeException("So luong dat hang phai lon hon 0.");
            }
            if (!seenProducts.add(item.getMasp())) {
                throw new RuntimeException("San pham " + item.getMasp() + " bi lap trong don hang.");
            }
        }
        // 1. Tìm thông tin khách hàng bằng mã tài khoản (maTK)
        KhachHang khachHang = khachHangRepo.findByMaTK(maTK)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng đăng nhập!"));

        // 2. Kiểm tra điểm tích lũy nếu có sử dụng
        int diemSuDung = request.getDiemSuDung() != null ? request.getDiemSuDung() : 0;
        if (diemSuDung < 0) {
            throw new RuntimeException("So diem su dung khong duoc am.");
        }
        if (diemSuDung > 0) {
            if (khachHang.getDiemTichLuy() == null || khachHang.getDiemTichLuy() < diemSuDung) {
                throw new RuntimeException("Tài khoản của bạn không đủ điểm tích lũy. Hiện có: " 
                        + (khachHang.getDiemTichLuy() != null ? khachHang.getDiemTichLuy() : 0) + " điểm.");
            }
        }

        // 3. Tạo vỏ hóa đơn đặt trước
        HoaDon invoice = HoaDon.builder()
                .maKH(khachHang.getMaKH())
                .ngayBan(LocalDate.now())
                .trangThai("DAT_TRUOC")
                .diemSuDung(diemSuDung)
                .tongTien(BigDecimal.ZERO)
                .tienThanhToan(BigDecimal.ZERO)
                .build();

        // Sử dụng saveAndFlush để Oracle sinh mã HD ngay lập tức thông qua trigger
        HoaDon savedInvoice = hoaDonRepo.saveAndFlush(invoice);

        // 4. Phân bổ lô sản phẩm theo nguyên tắc FEFO (Hạn dùng gần nhất xếp trước)
        List<HoaDonChiTietDto> items = new ArrayList<>();
        double tongTienTamTinh = 0;

        for (var item : request.getItems()) {
            SanPham sanPham = sanPhamRepo.findById(item.getMasp())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm mã " + item.getMasp() + " không tồn tại!"));

            // Lấy các lô còn hàng, còn hạn của sản phẩm này, xếp theo HSD tăng dần
            List<Batch> batches = batchRepo.findByMaspAndTrangthaiAndHsdAfterOrderByHsdAsc(
                    item.getMasp(), "CON_HANG", LocalDate.now());

            int tongTonKho = batches.stream().mapToInt(Batch::getSlsp).sum();
            if (tongTonKho < item.getSl()) {
                throw new RuntimeException("Sản phẩm '" + sanPham.getTenSanPham() + "' không đủ hàng trong kho! (Cần: " 
                        + item.getSl() + ", Còn: " + tongTonKho + ")");
            }

            int remaining = item.getSl();
            for (Batch batch : batches) {
                if (remaining <= 0) break;

                int allocated = Math.min(batch.getSlsp(), remaining);
                
                CTHD cthd = CTHD.builder()
                        .maHD(savedInvoice.getMaHD())
                        .maLO(batch.getMalo())
                        .sl(allocated)
                        .dongia(sanPham.getGiaBan())
                        .ghiChu(request.getGhiChu())
                        .build();

                // Lưu chi tiết hóa đơn (Trigger ORA sẽ tự tính thanhtien và trừ kho của lô tương ứng)
                CTHD savedCthd = cthdRepo.saveAndFlush(cthd);
                entityManager.refresh(savedCthd);

                tongTienTamTinh += (savedCthd.getThanhtien() != null) ? savedCthd.getThanhtien() : 0;
                
                items.add(HoaDonChiTietDto.builder()
                        .maHD(savedCthd.getMaHD())
                        .maLo(savedCthd.getMaLO())
                        .maSP(sanPham.getMaSP())
                        .tenSanPham(sanPham.getTenSanPham())
                        .dvt(sanPham.getDvt())
                        .sl(savedCthd.getSl())
                        .donGia(BigDecimal.valueOf(savedCthd.getDongia()))
                        .thanhTien(savedCthd.getThanhtien() != null ? BigDecimal.valueOf(savedCthd.getThanhtien()) : BigDecimal.ZERO)
                        .ghiChu(savedCthd.getGhiChu())
                        .build());

                remaining -= allocated;
            }
        }

        // 5. Kiểm tra quy định dùng điểm tối đa 50% tổng tiền
        if (diemSuDung > 0 && diemSuDung > (tongTienTamTinh * 0.5)) {
            throw new RuntimeException("Số điểm sử dụng (" + diemSuDung 
                    + ") vượt quá 50% tổng số tiền tạm tính của đơn hàng (" + (int)(tongTienTamTinh * 0.5) + " điểm).");
        }

        // 6. Ép Oracle chạy các trigger tính tổng tiền trên HOADON
        hoaDonRepo.flush();

        // 7. Lấy lại hóa đơn và refresh để đồng bộ tongtien & tienthanhtoan tự sinh từ trigger DB
        HoaDon finalInvoice = hoaDonRepo.findById(savedInvoice.getMaHD())
                .orElseThrow(() -> new RuntimeException("Lỗi đồng bộ hóa đơn từ cơ sở dữ liệu!"));
        entityManager.refresh(finalInvoice);

        // 8. Cập nhật thủ công tiền thanh toán cho đồng bộ (nếu trigger Oracle bị trễ hoặc thiếu)
        double tienthanhtoan = finalInvoice.getTongTien().doubleValue() - diemSuDung;
        finalInvoice.setTienThanhToan(BigDecimal.valueOf(tienthanhtoan));
        hoaDonRepo.save(finalInvoice);

        // 9. Trả về response
        return OrderResponse.builder()
                .maHD(finalInvoice.getMaHD())
                .trangThai(finalInvoice.getTrangThai())
                .ngayBan(finalInvoice.getNgayBan())
                .tongTien(finalInvoice.getTongTien())
                .tienThanhToan(finalInvoice.getTienThanhToan())
                .diemSuDung(finalInvoice.getDiemSuDung())
                .ghiChu(request.getGhiChu())
                .items(items)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String maTK) {
        KhachHang khachHang = khachHangRepo.findByMaTK(maTK)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng đăng nhập!"));

        List<HoaDon> hoaDons = hoaDonRepo.findByMaKHOrderByNgayBanDesc(khachHang.getMaKH());

        return hoaDons.stream().map(hd -> {
            List<Object[]> rawDetails = hoaDonRepo.findChiTietHoaDon(hd.getMaHD());
            List<HoaDonChiTietDto> items = rawDetails.stream().map(row -> HoaDonChiTietDto.builder()
                    .maHD((String) row[0])
                    .maLo((String) row[1])
                    .maSP((String) row[2])
                    .tenSanPham((String) row[3])
                    .dvt((String) row[4])
                    .sl(((Number) row[5]).intValue())
                    .donGia(BigDecimal.valueOf(((Number) row[6]).doubleValue()))
                    .thanhTien(BigDecimal.valueOf(((Number) row[7]).doubleValue()))
                    .ghiChu((String) row[8])
                    .build()).collect(Collectors.toList());

            return OrderResponse.builder()
                    .maHD(hd.getMaHD())
                    .trangThai(hd.getTrangThai())
                    .ngayBan(hd.getNgayBan())
                    .tongTien(hd.getTongTien())
                    .tienThanhToan(hd.getTienThanhToan())
                    .diemSuDung(hd.getDiemSuDung())
                    .items(items)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(String maTK, String maHD) {
        KhachHang khachHang = khachHangRepo.findByMaTK(maTK)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng!"));

        HoaDon hd = hoaDonRepo.findById(maHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng mã: " + maHD));

        if (!hd.getMaKH().equals(khachHang.getMaKH())) {
            throw new RuntimeException("Bạn không có quyền truy cập vào đơn hàng này!");
        }

        List<Object[]> rawDetails = hoaDonRepo.findChiTietHoaDon(hd.getMaHD());
        List<HoaDonChiTietDto> items = rawDetails.stream().map(row -> HoaDonChiTietDto.builder()
                .maHD((String) row[0])
                .maLo((String) row[1])
                .maSP((String) row[2])
                .tenSanPham((String) row[3])
                .dvt((String) row[4])
                .sl(((Number) row[5]).intValue())
                .donGia(BigDecimal.valueOf(((Number) row[6]).doubleValue()))
                .thanhTien(BigDecimal.valueOf(((Number) row[7]).doubleValue()))
                .ghiChu((String) row[8])
                .build()).collect(Collectors.toList());

        return OrderResponse.builder()
                .maHD(hd.getMaHD())
                .trangThai(hd.getTrangThai())
                .ngayBan(hd.getNgayBan())
                .tongTien(hd.getTongTien())
                .tienThanhToan(hd.getTienThanhToan())
                .diemSuDung(hd.getDiemSuDung())
                .items(items)
                .build();
    }

    @Override
    @Transactional
    public void cancelOrder(String maTK, String maHD) {
        KhachHang khachHang = khachHangRepo.findByMaTK(maTK)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng!"));

        HoaDon hd = hoaDonRepo.findById(maHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng mã: " + maHD));

        if (!hd.getMaKH().equals(khachHang.getMaKH())) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này!");
        }

        if (!"DAT_TRUOC".equalsIgnoreCase(hd.getTrangThai())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng khi đơn ở trạng thái 'ĐẶT TRƯỚC'. Trạng thái hiện tại: " + hd.getTrangThai());
        }

        // Xóa toàn bộ các chi tiết hóa đơn (Trigger DELETE trong DB sẽ tự động hoàn trả lại số lượng thuốc vào các lô tương ứng)
        List<CTHD> cthds = cthdRepo.findByMaHD(maHD);
        cthdRepo.deleteAll(cthds);
        
        // Buộc flush để các trigger hoàn trả kho và tính lại tổng tiền chạy
        cthdRepo.flush();

        // Cập nhật trạng thái đơn hàng thành HUY
        hd.setTrangThai("HUY");
        hd.setTongTien(BigDecimal.ZERO);
        hd.setTienThanhToan(BigDecimal.ZERO);
        
        // Hủy điểm tích lũy dùng trong đơn hàng (Trigger Oracle sẽ tự hoàn lại điểm cho khách hàng qua log điểm)
        hd.setDiemSuDung(0);

        hoaDonRepo.save(hd);
    }
}
