package com.pharmacy.backend.service;
import com.pharmacy.backend.dto.LoginResponse;

public interface AccountService {
    LoginResponse checkLogin(String sdt, String password);
}