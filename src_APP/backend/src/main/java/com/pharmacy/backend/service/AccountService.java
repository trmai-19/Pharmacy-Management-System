package com.pharmacy.backend.service;
import com.pharmacy.backend.dto.CreateUserRequest;
import com.pharmacy.backend.dto.LoginResponse;

public interface AccountService {
    
    LoginResponse checkLogin(String sdt, String password);

    void createAccount(CreateUserRequest request);

    void toggleAccountStatus(String id);

    void changePasswordFirstLogin(String sdt, String newPassword);

    void changePasswordSetting(String sdt, String oldPassword, String newPassword);

    void resetPassword(String sdt, String email);
}