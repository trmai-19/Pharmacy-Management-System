package com.pharmacy.backend.service;
import org.springframework.jdbc.core.JdbcTemplate;

import com.pharmacy.backend.dto.AccountResponse;
import com.pharmacy.backend.dto.CreateUserRequest;
import com.pharmacy.backend.dto.LoginResponse;
import java.util.List;

public interface AccountService {
    
    LoginResponse checkLogin(String sdt, String password);

    void createAccount(CreateUserRequest request);

    void toggleAccountStatus(String id);

    void changePasswordFirstLogin(String sdt, String newPassword);

    void changePasswordSetting(String sdt, String oldPassword, String newPassword);

    void resetPassword(String sdt, String email);

    public List<AccountResponse> getAllAccountsInSystem();
    
}