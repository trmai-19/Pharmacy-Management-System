package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.FirstLoginChangePasswordRequest;
import com.pharmacy.backend.dto.ForgotPasswordRequest;
import com.pharmacy.backend.dto.SettingChangePasswordRequest;
import com.pharmacy.backend.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PasswordController {

    private final AccountService accountService;

    @PostMapping("/first-login-change")
    public ResponseEntity<ApiResponse<Void>> changeFirstLoginPassword(@RequestBody FirstLoginChangePasswordRequest request) {
        String currentSdt = SecurityContextHolder.getContext().getAuthentication().getName();
        
        accountService.changePasswordFirstLogin(currentSdt, request.getNewPassword());
        
        return ResponseEntity.ok(new ApiResponse<>(200, "Đổi mật khẩu thành công!", null));
    }

    @PostMapping("/setting-change")
    public ResponseEntity<ApiResponse<Void>> changeSettingPassword(@RequestBody SettingChangePasswordRequest request) {
        String currentSdt = SecurityContextHolder.getContext().getAuthentication().getName();

        accountService.changePasswordSetting(
                currentSdt, 
                request.getOldPassword(), 
                request.getNewPassword()
        );

        return ResponseEntity.ok(new ApiResponse<>(200, "Đổi mật khẩu thành công!", null));
    }

    @PostMapping("/forgot")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        accountService.resetPassword(request.getSdt(), request.getEmail());
        
        return ResponseEntity.ok(new ApiResponse<>(200, "Mật khẩu tạm thời đã được gửi vào email của bạn!", null));
    }
}