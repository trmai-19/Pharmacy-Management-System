package com.pharmacy.backend.service;
import com.pharmacy.backend.dto.LoginResponse;

public interface AccountService {
    
    LoginResponse checkLogin(String sdt, String password);

    boolean createStaffAccount(String sdt, String rawPassword, String vaitro);

    boolean changePassword(String sdt, String newPassword);
}