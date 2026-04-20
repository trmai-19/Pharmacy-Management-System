package com.pharmacy.backend.service;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pharmacy.backend.dto.LoginResponse;
import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.repository.AccountRepository;

import com.pharmacy.backend.security.JwtUtils;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    public AccountServiceImpl(AccountRepository accountRepo, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, EmailService emailService) {
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }
    
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public LoginResponse checkLogin(String sdt, String password) {
        Optional<Account> accountOpt = accountRepo.findBySdt(sdt);
        if(accountOpt.isPresent()) {
            Account acc = accountOpt.get();
            boolean isPasswordMatch = passwordEncoder.matches(password, acc.getPassword());

            if(isPasswordMatch) {
                String generatedToken = jwtUtils.generateToken(acc.getSdt(), acc.getVaitro());
                LoginResponse res = new LoginResponse(true, "Success", acc.getVaitro(), generatedToken);
                res.setFirstLogin(acc.isFirstLogin());
                return res;
            } else {
                return new LoginResponse(false, "Error: Mật khẩu không chính xác!", null, null, true);
            }
        }
        else {
            return new LoginResponse(false, "Error: Số điện thoại chưa được đăng ký!", null, null, true);
        }
    }

    @Override
    public boolean createAccount(String sdt, String email, String vaitro) {
        if(accountRepo.findBySdt(sdt).isPresent()) return false;

        Account newAccount = new Account();
        String generatedMATK = "TK" + sdt;

        newAccount.setMatk(generatedMATK);
        newAccount.setSdt(sdt);
        newAccount.setVaitro(vaitro);
        newAccount.setEmail(email);
        newAccount.setFirstLogin(true);

        String rawPassword = generateRandomPassword();
        newAccount.setPassword(passwordEncoder.encode(rawPassword));

        newAccount.setNgaytao(new java.util.Date());

        accountRepo.save(newAccount);

        Map<String, Object> mailData = new HashMap<>();
        mailData.put("title", "HỆ THỐNG NHÀ THUỐC");
        mailData.put("subtitle", "Thông báo cấp tài khoản mới");
        mailData.put("message", "Quản trị viên vừa cấp cho bạn một tài khoản mới:");
        mailData.put("sdt", sdt);
        mailData.put("password", rawPassword);

        emailService.sendEmail(email, "[Pharmacy] Thông tin tài khoản", "email-template", mailData);
        return true;

    }

    @Override
    public boolean resetPassword(String sdt, String email) {
        Optional<Account> accOpt = accountRepo.findBySdt(sdt);
        if(accOpt.isPresent()) {
            Account acc = accOpt.get();
            if(acc.getEmail() != null && acc.getEmail().equals(email) ) {
                String tempPassword = generateRandomPassword();
                acc.setPassword(passwordEncoder.encode(tempPassword));
                acc.setFirstLogin(true);
                accountRepo.save(acc);

                Map<String, Object> mailData = new HashMap<>();
                mailData.put("title", "HỆ THỐNG NHÀ THUỐC");
                mailData.put("subtitle", "Yêu cầu khôi phục mật khẩu");
                mailData.put("message", "Hệ thống vừa nhận được yêu cầu cấp lại mật khẩu của bạn. Mật khẩu tạm thời là:");
                mailData.put("sdt", sdt);
                mailData.put("password", tempPassword);

                emailService.sendEmail(email, "[Pharmacy] Thông tin tài khoản", "email-template", mailData);

                return true;
            }
        }
        return false;
    }

    @Override
    public boolean changePassword(String sdt, String newPassword) {
        Optional<Account> accOpt = accountRepo.findBySdt(sdt);
        if(accOpt.isPresent()) {
            Account acc = accOpt.get();
            if (!acc.isFirstLogin()) {
                return false; 
            }
            acc.setPassword(passwordEncoder.encode(newPassword));
            acc.setFirstLogin(false);
            accountRepo.save(acc);
            return true;
        }
        return false;
    }

}
