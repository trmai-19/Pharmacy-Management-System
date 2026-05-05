package pharmaHMPP.cusapi.service;

import pharmaHMPP.cusapi.dto.*;
import pharmaHMPP.cusapi.entity.*;
import pharmaHMPP.cusapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TaiKhoanRepository taiKhoanRepo;
    private final KhachHangRepository khachHangRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public String register(RegisterRequest req) {
        // Kiem tra SDT da ton tai chua
        if (taiKhoanRepo.existsBySdt(req.getSdt())) {
            throw new RuntimeException("Số điện thoại đã được đăng ký!");
        }

        // 1. Tao TAIKHOAN (luu them email) - ID do Oracle trigger tu dong sinh
        TaiKhoan taiKhoan = TaiKhoan.builder()
                .vaiTro("CUSTOMER")
                .password(passwordEncoder.encode(req.getPassword()))
                .sdt(req.getSdt())
                .ngayTao(LocalDate.now())
                .isFirstLogin(false)
                .email(req.getEmail())
                .trangThai("HOAT DONG")
                .build();
        taiKhoanRepo.saveAndFlush(taiKhoan);

        // Fetch lai de lay maTK do Oracle sinh
        TaiKhoan savedTaiKhoan = taiKhoanRepo.findBySdt(req.getSdt())
                .orElseThrow(() -> new RuntimeException("Lỗi khi tạo tài khoản!"));

        // 2. Tao KHACHHANG
        KhachHang khachHang = KhachHang.builder()
                .maTK(savedTaiKhoan.getMaTK())
                .tenKH(req.getTenKH())
                .sdt(req.getSdt())
                .gioiTinh(req.getGioiTinh())
                .tongDoanhThu(java.math.BigDecimal.ZERO)
                .diemTichLuy(0)
                .hangTV("Thành Viên")
                .build();
        khachHangRepo.save(khachHang);

        return "Đăng ký thành công!";
    }

    public LoginResponse login(LoginRequest req) {
        // Tim tai khoan theo SDT
        TaiKhoan taiKhoan = taiKhoanRepo.findBySdt(req.getSdt())
                .orElseThrow(() -> new RuntimeException("Số điện thoại chưa đăng ký!"));

        // Kiem tra mat khau
        if (!passwordEncoder.matches(req.getPassword(), taiKhoan.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng!");
        }

        // Lay thong tin khach hang
        KhachHang khachHang = khachHangRepo.findByMaTK(taiKhoan.getMaTK())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng!"));

        // Tao JWT token
        String token = jwtService.generateToken(taiKhoan.getMaTK());

        // Tra ve isFirstLogin de frontend redirect neu can
        return new LoginResponse(
                token,
                khachHang.getMaKH(),
                khachHang.getTenKH(),
                khachHang.getSdt(),
                taiKhoan.getVaiTro(),
                taiKhoan.isFirstLogin()
        );
    }

    @Transactional
    public void forgotPassword(String sdt, String email) {
        // Xac minh SDT va email khop voi tai khoan
        TaiKhoan taiKhoan = taiKhoanRepo.findBySdtAndEmail(sdt, email)
                .orElseThrow(() -> new RuntimeException("Số điện thoại hoặc email không chính xác!"));

        // Tao mat khau tam thoi (8 ky tu ngau nhien)
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        // Cap nhat DB
        taiKhoan.setPassword(passwordEncoder.encode(tempPassword));
        taiKhoan.setFirstLogin(true);
        taiKhoanRepo.save(taiKhoan);

        // Gui email khoi phuc
        emailService.sendTempPassword(email, tempPassword);
    }

    @Transactional
    public void changePassword(String maTK, String newPassword) {
        TaiKhoan taiKhoan = taiKhoanRepo.findById(maTK)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        taiKhoan.setPassword(passwordEncoder.encode(newPassword));
        taiKhoan.setFirstLogin(false);
        taiKhoanRepo.save(taiKhoan);
    }
}