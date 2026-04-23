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
}