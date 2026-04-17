package pharmaHMPP.cusapi.service;

import pharmaHMPP.cusapi.dto.DiemResponse;
import pharmaHMPP.cusapi.entity.*;
import pharmaHMPP.cusapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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

        // Lay diem tu DIEMTL qua maDTL cua KHACHHANG
        Integer tongDiem = 0;
        List<DiemResponse.DiemChiTiet> lichSuDiem = Collections.emptyList();

        if (kh.getMaDTL() != null) {
            DiemTL diemTL = diemTLRepo.findById(kh.getMaDTL()).orElse(null);
            if (diemTL != null) {
                tongDiem = diemTL.getSl() != null ? diemTL.getSl() : 0;
                // Tra ve 1 ban ghi diem hien tai
                lichSuDiem = Collections.singletonList(
                        new DiemResponse.DiemChiTiet(diemTL.getMaDTL(), diemTL.getLoaiGD(), diemTL.getSl())
                );
            }
        }

        return new DiemResponse(kh.getMaKH(), kh.getTenKH(), tongDiem, lichSuDiem);
    }

    public List<HoaDon> getLichSuMuaHang(String maTK) {
        KhachHang kh = getThongTin(maTK);
        return hoaDonRepo.findByMaKHOrderByNgayBanDesc(kh.getMaKH());
    }
}