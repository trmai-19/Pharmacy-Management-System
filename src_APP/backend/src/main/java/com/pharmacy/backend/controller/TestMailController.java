package com.pharmacy.backend.controller;

import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.backend.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestMailController {
    private final EmailService emailService;


    @GetMapping("/api/test-mail")
    public ResponseEntity<String> testSendMail(@RequestParam String toEmail) {
        try {
            String subject = "Test tính năng gửi mail";

            Map<String, Object> variables = new HashMap<>();
            variables.put("title", "TEST HỆ THỐNG");
            variables.put("subtitle", "Test gửi mail.");
            variables.put("message", "Chào bạn, nếu thấy mail chức năng gửi mail hoạt động!");
            variables.put("sdt", "0123456789");
            variables.put("password", "PassTest123");

            emailService.sendEmail(toEmail, subject, "email-template", variables);

            return ResponseEntity.ok("Đã gửi mail HTML thành công đến " + toEmail);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi gửi mail: " + e.getMessage());
        }
    }
}
