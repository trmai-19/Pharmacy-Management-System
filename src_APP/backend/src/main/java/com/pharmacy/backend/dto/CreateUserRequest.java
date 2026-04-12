package com.pharmacy.backend.dto;

public class CreateUserRequest {
    private String sdt;
    private String password;
    private String vaitro;

    public CreateUserRequest() {}

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getVaitro() { return vaitro; }
    public void setVaitro(String vaitro) { this.vaitro = vaitro; }
}