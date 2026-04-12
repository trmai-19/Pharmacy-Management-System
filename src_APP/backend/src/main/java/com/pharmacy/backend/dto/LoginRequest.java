package com.pharmacy.backend.dto;

public class LoginRequest {
    private String sdt;
    private String password;

    public LoginRequest() {};

    public LoginRequest(String sdt, String password) {
        this.sdt = sdt;
        this.password = password;
    }

    public String getSdt() {
        return sdt;
    }
    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
