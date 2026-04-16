package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> createAccount(@RequestBody CreateUserRequest request) {
        
        boolean isSuccess = accountService.createAccount (
            request.getSdt(),
            request.getEmail(),
            request.getVaitro()
        );

        if(isSuccess) {
            return ResponseEntity.ok("Tạo tài khoản thành công! Vui lòng kiểm tra email để nhận thông tin đăng nhập lần đầu!");
        } else {
            return ResponseEntity.badRequest().body("Tài khoản đã tồn tại!");
        }
    }
}
