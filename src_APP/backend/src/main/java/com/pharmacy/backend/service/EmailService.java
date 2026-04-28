package com.pharmacy.backend.service;
import java.util.Map;

public interface EmailService {
    void sendEmail(String to, String subject, String templateName, Map<String, Object> variables);

    void sendAccountCreationEmail(String email, String sdt, String password);
    
    void sendPasswordResetEmail(String email, String sdt, String tempPassword);
}