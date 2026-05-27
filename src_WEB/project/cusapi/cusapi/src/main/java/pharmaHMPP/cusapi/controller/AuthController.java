package pharmaHMPP.cusapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pharmaHMPP.cusapi.dto.ChangePasswordRequest;
import pharmaHMPP.cusapi.dto.ForgotPasswordRequest;
import pharmaHMPP.cusapi.dto.LoginRequest;
import pharmaHMPP.cusapi.dto.LoginResponse;
import pharmaHMPP.cusapi.dto.RegisterRequest;
import pharmaHMPP.cusapi.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            String result = authService.register(req);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            LoginResponse response = authService.login(req);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Bước 1 quên mật khẩu: xác minh SĐT + email, gửi mật khẩu tạm vào email */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        try {
            authService.forgotPassword(req.getSdt(), req.getEmail());
            return ResponseEntity.ok("Mật khẩu tạm thời đã được gửi vào email của bạn!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Bước 2: đổi mật khẩu sau khi đăng nhập bằng mật khẩu tạm (cần JWT) */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req,
                                            Authentication authentication) {
        try {
            String maTK = authentication.getName();
            authService.changePassword(maTK, req.getNewPassword());
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}