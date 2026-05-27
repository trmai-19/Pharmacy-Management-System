package pharmaHMPP.cusapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendTempPassword(String toEmail, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[Pharma] Mật khẩu tạm thời của bạn");
        message.setText(
                "Xin chào,\n\n" +
                "Bạn đã yêu cầu khôi phục mật khẩu cho tài khoản Pharma.\n\n" +
                "Mật khẩu tạm thời của bạn là: " + tempPassword + "\n\n" +
                "Vui lòng đăng nhập bằng mật khẩu này và đổi ngay mật khẩu mới.\n\n" +
                "Trân trọng,\nĐội ngũ Pharma"
        );
        mailSender.send(message);
    }
}
