package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.backend.dto.LoginRequest;
import com.pharmacy.backend.dto.LoginResponse;
import com.pharmacy.backend.service.AccountService;
import org.springframework.security.core.context.SecurityContextHolder;
import com.pharmacy.backend.dto.ChangePasswordRequest;
import com.pharmacy.backend.dto.ForgotPasswordRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class LoginController {

    private final AccountService accountService;

    public LoginController(AccountService accountService)
    {
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        
        String sdt = loginRequest.getSdt();
        String password = loginRequest.getPassword();

        // --- test ---
        System.out.println("SĐT -> Frontend: [" + sdt + "]");
        System.out.println("Pass -> Frontend: [" + password + "]");

        LoginResponse response = accountService.checkLogin(sdt, password);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response); 
        } else {
            return ResponseEntity.status(401).body(response); 
        }
    }

    @PostMapping("/login/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        String currentSdt = SecurityContextHolder.getContext().getAuthentication().getName();
        
        boolean success = accountService.changePassword(currentSdt, request.getNewPassword());
        if(success) return ResponseEntity.ok("Đổi mật khẩu thành công!");
        return ResponseEntity.badRequest().body("Lỗi khi đổi mật khẩu!");
    }

    @PostMapping("login/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        boolean success = accountService.resetPassword(request.getSdt(), request.getEmail());
        if(success) {
            return ResponseEntity.ok("Mật khẩu tạm thời đã được gửi vào email của bạn!");
        }
        return ResponseEntity.badRequest().body("Số điện thoại hoặc Email không chính xác!");
    }
}