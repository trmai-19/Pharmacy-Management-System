package com.pharmacy.backend.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {    
            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("your_email@gmail.com", "Pharmacy No-Reply");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAccountCreationEmail(String email, String sdt, String password) {
        Map<String, Object> mailData = new HashMap<>();
        mailData.put("title", "HỆ THỐNG NHÀ THUỐC");
        mailData.put("subtitle", "Thông báo cấp tài khoản mới");
        mailData.put("message", "Quản trị viên vừa cấp cho bạn một tài khoản mới:");
        mailData.put("sdt", sdt);
        mailData.put("password", password);

        sendEmail(email, "[Pharmacy] Thông tin tài khoản", "email-template", mailData);
    }

    @Override
    public void sendPasswordResetEmail(String email, String sdt, String tempPassword) {
        Map<String, Object> mailData = new HashMap<>();
        mailData.put("title", "HỆ THỐNG NHÀ THUỐC");
        mailData.put("subtitle", "Yêu cầu khôi phục mật khẩu");
        mailData.put("message", "Hệ thống vừa nhận được yêu cầu cấp lại mật khẩu của bạn. Mật khẩu tạm thời là:");
        mailData.put("sdt", sdt);
        mailData.put("password", tempPassword);

        sendEmail(email, "[Pharmacy] Yêu cầu khôi phục mật khẩu", "email-template", mailData);
    }
}
