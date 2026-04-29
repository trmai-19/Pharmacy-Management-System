package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.CreateUserRequest;
import com.pharmacy.backend.service.AccountService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AccountService accountService;

    public AdminController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/create-account")
    public ResponseEntity<ApiResponse<Void>> createAccount(@RequestBody CreateUserRequest request) {
        
        boolean isSuccess = accountService.createAccount (
            request.getSdt(),
            request.getEmail(),
            request.getVaitro()
        );

        if(isSuccess) {
            ApiResponse<Void> res = new ApiResponse<>(200, "Tạo tài khoản thành công! Vui lòng kiểm tra email để nhận thông tin đăng nhập lần đầu!", null);
            return ResponseEntity.ok(res);
        } else {
            ApiResponse<Void> res = new ApiResponse<>(400, "Tài khoản đã tồn tại!", null);
            return ResponseEntity.badRequest().body(res);
        }
    }
}
