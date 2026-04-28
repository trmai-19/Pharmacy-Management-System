package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.LoginRequest;
import com.pharmacy.backend.dto.LoginResponse;
import com.pharmacy.backend.service.AccountService;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "*") 
public class LoginController {

    private final AccountService accountService;

    public LoginController(AccountService accountService)
    {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        
        String sdt = loginRequest.getSdt();
        String password = loginRequest.getPassword();

        LoginResponse response = accountService.checkLogin(sdt, password);
        
        if (response != null) {
            ApiResponse<LoginResponse> res = new ApiResponse<>(200, "Đăng nhập thành công", response);
            return ResponseEntity.ok(res); 
        } else {
            ApiResponse<LoginResponse> res = new ApiResponse<>(401, "Sai số điện thoại hoặc mật khẩu", null);
            return ResponseEntity.status(401).body(res); 
        }
    }
}