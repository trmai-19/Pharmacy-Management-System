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
        
        System.out.println(">>> THUNDER CLIENT GỬI LÊN: SĐT=" + request.getSdt() + " | Pass=" + request.getPassword() + " | VaiTro=" + request.getVaitro());
        
        boolean isSuccess = accountService.createStaffAccount (
            request.getSdt(),
            request.getPassword(),
            request.getVaitro()
        );

        if(isSuccess) {
            return ResponseEntity.ok("Tạo tài khoản thành công!");
        } else {
            return ResponseEntity.badRequest().body("Tài khoản đã tồn tại!");
        }
    }
}
