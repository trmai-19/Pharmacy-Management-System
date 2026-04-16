package com.pharmacy.backend.dto;

public class CreateUserRequest {
    private String sdt;
    private String vaitro;
    private String email;

    public CreateUserRequest() {}

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getVaitro() { return vaitro; }
    public void setVaitro(String vaitro) { this.vaitro = vaitro; }

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
}