package pharmaHMPP.cusapi.service;

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
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

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

    public void sendTempPassword(String toEmail, String tempPassword, String sdt) {
        Map<String, Object> mailData = new HashMap<>();
        mailData.put("title", "HỆ THỐNG NHÀ THUỐC");
        mailData.put("subtitle", "Yêu cầu khôi phục mật khẩu");
        mailData.put("message", "Hệ thống vừa nhận được yêu cầu cấp lại mật khẩu của bạn. Mật khẩu tạm thời là:");
        mailData.put("sdt", sdt);
        mailData.put("password", tempPassword);

        sendEmail(toEmail, "[Pharmacy] Yêu cầu khôi phục mật khẩu", "email-template", mailData);
    }

    public void sendRegistrationEmail(String toEmail, String sdt) {
        Map<String, Object> mailData = new HashMap<>();
        mailData.put("title", "HỆ THỐNG NHÀ THUỐC");
        mailData.put("subtitle", "Đăng ký tài khoản thành công");
        mailData.put("message", "Chào mừng bạn đã đăng ký tài khoản thành công tại hệ thống nhà thuốc của chúng tôi. Bạn có thể sử dụng số điện thoại dưới đây để đăng nhập và tích lũy điểm khi mua sắm.");
        mailData.put("sdt", sdt);
        mailData.put("password", null); // Hide the password block in HTML template

        sendEmail(toEmail, "[Pharmacy] Đăng ký tài khoản thành công", "email-template", mailData);
    }
}
