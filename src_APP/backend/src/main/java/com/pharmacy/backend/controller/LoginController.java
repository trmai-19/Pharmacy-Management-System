package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.LoginRequest;
import com.pharmacy.backend.dto.LoginResponse;
import com.pharmacy.backend.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "*") 
@RequiredArgsConstructor
public class LoginController {

    private final AccountService accountService;


    @PostMapping
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = accountService.checkLogin(loginRequest.getSdt(), loginRequest.getPassword());
        
        return ResponseEntity.ok(new ApiResponse<>(200, "Đăng nhập thành công", response)); 
    }
}