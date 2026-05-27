package com.pharmacy.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.backend.dto.AccountResponse;
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
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAllAccounts() {
        try {
            List<AccountResponse> accounts = accountService.getAllAccountsInSystem();
            // Trả về đúng quy tắc 200, thông điệp thành công, và danh sách data
            ApiResponse<List<AccountResponse>> response = new ApiResponse<>(200, "Tải danh sách tài khoản thành công!", accounts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Lỗi máy chủ: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/{username}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable String username, @RequestBody String email) {
        accountService.resetPassword(username, email);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đã đặt lại mật khẩu về mặc định!", null));
    }
}