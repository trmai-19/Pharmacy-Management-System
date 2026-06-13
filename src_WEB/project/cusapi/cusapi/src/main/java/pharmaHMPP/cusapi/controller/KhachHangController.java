package pharmaHMPP.cusapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pharmaHMPP.cusapi.dto.*;
import pharmaHMPP.cusapi.entity.HoaDon;
import pharmaHMPP.cusapi.service.KhachHangService;

import java.util.List;

@RestController
@RequestMapping("/api/khachhang")
@RequiredArgsConstructor
public class KhachHangController {

    private final KhachHangService khachHangService;

    // Lấy thông tin cá nhân
    @GetMapping("/me")
    public ResponseEntity<KhachHangProfileDto> getThongTin(Authentication auth) {
        String maTK = auth.getName();
        return ResponseEntity.ok(khachHangService.getThongTin(maTK));
    }

    // Cập nhật thông tin cá nhân
    @PutMapping("/me")
    public ResponseEntity<KhachHangProfileDto> updateThongTin(Authentication auth, @RequestBody KhachHangUpdateRequest req) {
        String maTK = auth.getName();
        return ResponseEntity.ok(khachHangService.updateThongTin(maTK, req));
    }

    // Lấy điểm tích lũy
    @GetMapping("/diem")
    public ResponseEntity<DiemResponse> getDiem(Authentication auth) {
        String maTK = auth.getName();
        return ResponseEntity.ok(khachHangService.getDiem(maTK));
    }

    // Lấy lịch sử mua hàng
    @GetMapping("/lichsu")
    public ResponseEntity<List<HoaDon>> getLichSu(Authentication auth) {
        String maTK = auth.getName();
        return ResponseEntity.ok(khachHangService.getLichSuMuaHang(maTK));
    }

    // Lấy chi tiết hóa đơn
    @GetMapping("/lichsu/{maHD}")
    public ResponseEntity<List<HoaDonChiTietDto>> getChiTietHoaDon(Authentication auth, @PathVariable String maHD) {
        String maTK = auth.getName();
        return ResponseEntity.ok(khachHangService.getChiTietHoaDon(maTK, maHD));
    }
}
