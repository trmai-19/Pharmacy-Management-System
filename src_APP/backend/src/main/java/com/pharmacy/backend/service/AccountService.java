package com.pharmacy.backend.service;
import com.pharmacy.backend.dto.LoginResponse;

public interface AccountService {
    
    LoginResponse checkLogin(String sdt, String password);

    boolean createAccount(String sdt, String email, String vaitro);

    boolean changePasswordFirstLogin(String sdt, String newPassword);

    boolean changePasswordSetting(String sdt, String oldPassword, String newPassword);

    boolean resetPassword(String sdt, String email);
}
