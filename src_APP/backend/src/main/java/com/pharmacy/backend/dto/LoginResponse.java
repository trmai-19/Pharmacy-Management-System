package com.pharmacy.backend.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String vaitro;
    private String token;
    private boolean firstLogin;

    public LoginResponse() {};

    public LoginResponse(boolean success, String message, String vaitro, String token) {
        this.success = success;
        this.message = message;
        this.vaitro = vaitro;
        this.token = token;
    }
    
    public LoginResponse(boolean success, String message, String vaitro, String token, boolean firstLogin) {
        this.success = success;
        this.message = message;
        this.vaitro = vaitro;
        this.token = token;
        this.firstLogin = firstLogin;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVaitro() {
        return vaitro;
    }

    public void setVaitro(String vaitro) {
        this.vaitro = vaitro;
    }

    public String getToken() { 
        return token;
    }
    public void setToken(String token) { 
        this.token = token; 
    }

    public boolean isFirstLogin() { 
        return firstLogin; 
    }
    public void setFirstLogin(boolean firstLogin) { 
        this.firstLogin = firstLogin; 
    }
}
