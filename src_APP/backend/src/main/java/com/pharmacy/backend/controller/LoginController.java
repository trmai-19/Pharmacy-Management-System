package com.pharmacy.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.backend.service.AccountService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class LoginController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        
        String sdt = request.get("sdt");
        String password = request.get("password");

        // --- test ---
        System.out.println("SĐT -> Frontend: [" + sdt + "]");
        System.out.println("Pass -> Frontend: [" + password + "]");

        String ketQua = accountService.kiemTraDangNhap(sdt, password);

        if (ketQua.startsWith("Success")) {
            return ResponseEntity.ok(ketQua); 
        } else {
            return ResponseEntity.status(401).body(ketQua); 
        }
    }
}