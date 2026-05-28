package pharmaHMPP.cusapi.service;

import pharmaHMPP.cusapi.dto.*;
import pharmaHMPP.cusapi.entity.*;
import pharmaHMPP.cusapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KhachHangService {

    private final KhachHangRepository khachHangRepo;
    private final DiemTLRepository diemTLRepo;
    private final HoaDonRepository hoaDonRepo;
    private final TaiKhoanRepository taiKhoanRepo;

    public KhachHang getKhachHangEntity(String maTK) {
        return khachHangRepo.findByMaTK(maTK)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng!"));
    }

    public KhachHangProfileDto getThongTin(String maTK) {
        KhachHang kh = getKhachHangEntity(maTK);
        TaiKhoan tk = taiKhoanRepo.findById(maTK)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
        return KhachHangProfileDto.builder()
                .maKH(kh.getMaKH())
                .maTK(kh.getMaTK())
                .tenKH(kh.getTenKH())
                .gioiTinh(kh.getGioiTinh())
                .ngaySinh(kh.getNgaySinh())
                .sdt(kh.getSdt())
                .tongDoanhThu(kh.getTongDoanhThu())
                .diemTichLuy(kh.getDiemTichLuy())
                .hangTV(kh.getHangTV())
                .email(tk.getEmail())
                .build();
    }

    public DiemResponse getDiem(String maTK) {
        KhachHang kh = getKhachHangEntity(maTK);

        // Lấy tổng điểm trực tiếp từ KHACHHANG.DIEMTICHLUY
        Integer tongDiem = kh.getDiemTichLuy() != null ? kh.getDiemTichLuy() : 0;

        // Lấy lịch sử điểm từ bảng DIEMTL theo maKH
        List<DiemResponse.DiemChiTiet> lichSuDiem;
        try {
            List<DiemTL> danhSachDiem = diemTLRepo.findByMaKH(kh.getMaKH());
            lichSuDiem = danhSachDiem.stream()
                    .map(d -> new DiemResponse.DiemChiTiet(
                            d.getMaDTL(),
                            d.getLoaiGD(),
                            d.getDiemThayDoi() != null ? d.getDiemThayDoi() : 0
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            lichSuDiem = Collections.emptyList();
        }

        return new DiemResponse(kh.getMaKH(), kh.getTenKH(), tongDiem, lichSuDiem);
    }

    public List<HoaDon> getLichSuMuaHang(String maTK) {
        KhachHang kh = getKhachHangEntity(maTK);
        return hoaDonRepo.findByMaKHOrderByNgayBanDesc(kh.getMaKH());
    }

    @Transactional
    public KhachHangProfileDto updateThongTin(String maTK, KhachHangUpdateRequest req) {
        KhachHang kh = getKhachHangEntity(maTK);
        TaiKhoan tk = taiKhoanRepo.findById(maTK)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        // Check if SDT changed and is already taken
        if (req.getSdt() != null && !req.getSdt().trim().isEmpty() && !req.getSdt().equals(kh.getSdt())) {
            String newSdt = req.getSdt().trim();
            if (khachHangRepo.existsBySdt(newSdt) || taiKhoanRepo.existsBySdt(newSdt)) {
                throw new RuntimeException("Số điện thoại này đã được sử dụng bởi tài khoản khác!");
            }
            kh.setSdt(newSdt);
            tk.setSdt(newSdt);
        }

        if (req.getTenKH() != null) {
            kh.setTenKH(req.getTenKH());
        }
        if (req.getGioiTinh() != null) {
            kh.setGioiTinh(req.getGioiTinh());
        }
        if (req.getNgaySinh() != null) {
            kh.setNgaySinh(req.getNgaySinh());
        }
        if (req.getEmail() != null) {
            tk.setEmail(req.getEmail());
        }

        taiKhoanRepo.save(tk);
        khachHangRepo.save(kh);

        return KhachHangProfileDto.builder()
                .maKH(kh.getMaKH())
                .maTK(kh.getMaTK())
                .tenKH(kh.getTenKH())
                .gioiTinh(kh.getGioiTinh())
                .ngaySinh(kh.getNgaySinh())
                .sdt(kh.getSdt())
                .tongDoanhThu(kh.getTongDoanhThu())
                .diemTichLuy(kh.getDiemTichLuy())
                .hangTV(kh.getHangTV())
                .email(tk.getEmail())
                .build();
    }

    public List<HoaDonChiTietDto> getChiTietHoaDon(String maTK, String maHD) {
        KhachHang kh = getKhachHangEntity(maTK);
        HoaDon hd = hoaDonRepo.findById(maHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn!"));

        // Security check
        if (!hd.getMaKH().equals(kh.getMaKH())) {
            throw new RuntimeException("Bạn không có quyền xem hóa đơn này!");
        }

        List<Object[]> rawList = hoaDonRepo.findChiTietHoaDon(maHD);
        return rawList.stream().map(row -> HoaDonChiTietDto.builder()
                .maHD((String) row[0])
                .maLo((String) row[1])
                .maSP((String) row[2])
                .tenSanPham((String) row[3])
                .dvt((String) row[4])
                .sl(row[5] != null ? ((Number) row[5]).intValue() : 0)
                .donGia(row[6] != null ? new java.math.BigDecimal(row[6].toString()) : java.math.BigDecimal.ZERO)
                .thanhTien(row[7] != null ? new java.math.BigDecimal(row[7].toString()) : java.math.BigDecimal.ZERO)
                .ghiChu((String) row[8])
                .build()
        ).collect(Collectors.toList());
    }
}