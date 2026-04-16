package com.pharmacy.backend.dto;

public class ForgotPasswordRequest {
    private String sdt;
    private String email;

    public String getSdt() {return sdt;}
    public void setSdt(String sdt) {this.sdt = sdt;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
}
