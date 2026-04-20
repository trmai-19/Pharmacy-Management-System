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
    private final DiemTLRepository diemTLRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public String register(RegisterRequest req) {
        // Kiem tra SDT da ton tai chua
        if (taiKhoanRepo.existsBySdt(req.getSdt())) {
            throw new RuntimeException("Số điện thoại đã được đăng ký!");
        }

        // Tao ma tu dong (tranh trung bang timestamp)
        long ts = System.currentTimeMillis() % 100000;
        String maTK  = "TK" + ts;
        String maKH  = "KH" + ts;
        String maDTL = "DTL" + ts;

        // 1. Tao DIEMTL truoc (vi KHACHHANG.MADTL la FK -> DIEMTL)
        DiemTL diemTL = DiemTL.builder()
                .maDTL(maDTL)
                .loaiGD("DANG_KY")
                .sl(0)
                .build();
        diemTLRepo.save(diemTL);

        // 2. Tao TAIKHOAN (luu them email)
        TaiKhoan taiKhoan = TaiKhoan.builder()
                .maTK(maTK)
                .vaiTro("KHACH_HANG")
                .password(passwordEncoder.encode(req.getPassword()))
                .sdt(req.getSdt())
                .ngayTao(LocalDate.now())
                .isFirstLogin(false)
                .email(req.getEmail())
                .build();
        taiKhoanRepo.save(taiKhoan);

        // 3. Tao KHACHHANG voi ca maTK va maDTL
        KhachHang khachHang = KhachHang.builder()
                .maKH(maKH)
                .maTK(maTK)
                .maDTL(maDTL)
                .tenKH(req.getTenKH())
                .sdt(req.getSdt())
                .gioiTinh(req.getGioiTinh())
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