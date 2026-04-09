package com.pharmacy.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.repository.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;

    public String kiemTraDangNhap(String sdt, String password) {
        Optional<Account> account = accountRepo.findBySdtAndPassword(sdt, password);
        
        if (account.isPresent()) {
            return "Success!";
        } else {
            return "Error: Không tìm thấy tài khoản!";
        }
    }
}