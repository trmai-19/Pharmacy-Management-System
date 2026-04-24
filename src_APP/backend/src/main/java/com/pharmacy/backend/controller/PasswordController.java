package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.FirstLoginChangePasswordRequest;
import com.pharmacy.backend.dto.ForgotPasswordRequest;
import com.pharmacy.backend.dto.SettingChangePasswordRequest;
import com.pharmacy.backend.service.AccountService;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "*")
public class PasswordController {

    private final AccountService accountService;

    public PasswordController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/first-login-change")
    public ResponseEntity<ApiResponse<Void>> changeFirstLoginPassword(@RequestBody FirstLoginChangePasswordRequest request) {
        String currentSdt = SecurityContextHolder.getContext().getAuthentication().getName();
        
        boolean success = accountService.changePasswordFirstLogin(currentSdt, request.getNewPassword());
        if (success) {
            return ResponseEntity.ok(new ApiResponse<>(200, "Đổi mật khẩu thành công!", null));
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Lỗi khi đổi mật khẩu!", null));
    }

    @PostMapping("/setting-change")
    public ResponseEntity<ApiResponse<Void>> changeSettingPassword(@RequestBody SettingChangePasswordRequest request) {
    
        String currentSdt = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean success = accountService.changePasswordSetting(
                currentSdt, 
                request.getOldPassword(), 
                request.getNewPassword()
        );

        if (success) {
            return ResponseEntity.ok(new ApiResponse<>(200, "Đổi mật khẩu thành công!", null));
        }
        
        return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Mật khẩu cũ không chính xác hoặc không đủ điều kiện đổi!", null));
    }


    @PostMapping("/forgot")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        boolean success = accountService.resetPassword(request.getSdt(), request.getEmail());
        if (success) {
            return ResponseEntity.ok(new ApiResponse<>(200, "Mật khẩu tạm thời đã được gửi vào email của bạn!", null));
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Số điện thoại hoặc Email không chính xác!", null));
    }
}