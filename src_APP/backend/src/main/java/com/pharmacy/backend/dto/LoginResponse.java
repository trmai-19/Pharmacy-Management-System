package com.pharmacy.backend.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String vaitro;

    public LoginResponse() {};

    public LoginResponse(boolean success, String message, String vaitro) {
        this.success = success;
        this.message = message;
        this.vaitro = vaitro;
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
}
