package com.pharmacy.model;

public class Supplier {
    private String mancc;
    private String tenncc;
    private String sdt;
    private String email;
    private String diachi;

    // Bắt buộc phải có Constructor rỗng cho Jackson
    public Supplier() {}

    public Supplier(String mancc, String tenncc, String sdt, String email, String diachi) {
        this.mancc = mancc;
        this.tenncc = tenncc;
        this.sdt = sdt;
        this.email = email;
        this.diachi = diachi;
    }

    public String getMancc() { return mancc; }
    public void setMancc(String mancc) { this.mancc = mancc; }
    
    public String getTenncc() { return tenncc; }
    public void setTenncc(String tenncc) { this.tenncc = tenncc; }
    
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDiachi() { return diachi; }
    public void setDiachi(String diachi) { this.diachi = diachi; }
}