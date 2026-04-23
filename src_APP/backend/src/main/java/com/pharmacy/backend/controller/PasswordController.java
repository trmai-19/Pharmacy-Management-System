package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.pharmacy.backend.dto.ChangePasswordRequest;
import com.pharmacy.backend.dto.ForgotPasswordRequest;
import com.pharmacy.backend.service.AccountService;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "*")
public class PasswordController {

    private final AccountService accountService;

    public PasswordController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/change")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        String currentSdt = SecurityContextHolder.getContext().getAuthentication().getName();
        
        boolean success = accountService.changePassword(currentSdt, request.getNewPassword());
        if (success) return ResponseEntity.ok("Đổi mật khẩu thành công!");
        return ResponseEntity.badRequest().body("Lỗi khi đổi mật khẩu!");
    }

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        boolean success = accountService.resetPassword(request.getSdt(), request.getEmail());
        if (success) {
            return ResponseEntity.ok("Mật khẩu tạm thời đã được gửi vào email của bạn!");
        }
        return ResponseEntity.badRequest().body("Số điện thoại hoặc Email không chính xác!");
    }
}