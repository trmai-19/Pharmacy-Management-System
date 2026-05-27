package pharmaHMPP.cusapi.service;

import pharmaHMPP.cusapi.dto.DiemResponse;
import pharmaHMPP.cusapi.entity.*;
import pharmaHMPP.cusapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KhachHangService {

    private final KhachHangRepository khachHangRepo;
    private final DiemTLRepository diemTLRepo;
    private final HoaDonRepository hoaDonRepo;

    public KhachHang getThongTin(String maTK) {
        return khachHangRepo.findByMaTK(maTK)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng!"));
    }

    public DiemResponse getDiem(String maTK) {
        KhachHang kh = getThongTin(maTK);

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
        KhachHang kh = getThongTin(maTK);
        return hoaDonRepo.findByMaKHOrderByNgayBanDesc(kh.getMaKH());
    }
}