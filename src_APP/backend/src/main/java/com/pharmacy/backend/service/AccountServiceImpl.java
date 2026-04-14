package com.pharmacy.backend.service;

import java.util.Optional;

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

    public AccountServiceImpl(AccountRepository accountRepo, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
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
    public boolean createStaffAccount(String sdt, String rawPassword, String vaitro) {
        if(accountRepo.findBySdt(sdt).isPresent()) return false;

        Account newAccount = new Account();
        String generatedMATK = "TK" + sdt;

        newAccount.setMatk(generatedMATK);
        newAccount.setSdt(sdt);
        newAccount.setVaitro(vaitro);
        newAccount.setFirstLogin(true);

        String hashedPassword = passwordEncoder.encode(rawPassword);
        newAccount.setPassword(hashedPassword);

        newAccount.setNgaytao(new java.util.Date());

        accountRepo.save(newAccount);

        return true;

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