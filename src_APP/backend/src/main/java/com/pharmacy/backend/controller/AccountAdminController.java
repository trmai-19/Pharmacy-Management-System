package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.CreateUserRequest;
import com.pharmacy.backend.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
public class AccountAdminController {
    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createAccount(@RequestBody CreateUserRequest request) {
        accountService.createAccount(request);

        ApiResponse<Void> res = new ApiResponse<>(200, "Tạo tài khoản thành công! Vui lòng kiểm tra email để nhận thông tin đăng nhập lần đầu!", null);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> toggleAccountStatus(@PathVariable String id) {
        accountService.toggleAccountStatus(id);
        
        ApiResponse<Void> res = new ApiResponse<>(200, "Cập nhật trạng thái tài khoản thành công!", null);
        return ResponseEntity.ok(res);
    }
}