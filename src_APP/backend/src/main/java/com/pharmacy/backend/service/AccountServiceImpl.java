package com.pharmacy.backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pharmacy.backend.dto.LoginResponse;
import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.repository.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;

    public AccountServiceImpl(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    public LoginResponse checkLogin(String sdt, String password) {
        Optional<Account> accountOpt = accountRepo.findBySdtAndPassword(sdt, password);
        if(accountOpt.isPresent()) {
            Account acc = accountOpt.get();
            return new LoginResponse(true, "Success", acc.getVaitro());
        }
        else {
            return new LoginResponse(false, "Error: Sai số điện thoại hoặc mật khẩu ", null);
        }
    }
}