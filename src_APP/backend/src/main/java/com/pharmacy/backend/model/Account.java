package com.pharmacy.backend.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TAIKHOAN")
public class Account {

    @Id
    @Column(name = "MATK")
    private String matk;

    @Column(name = "VAITRO")
    private String vaitro;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "NGAYTAO")
    private Date ngaytao;

    // --- Getters và Setters ---
    public String getMatk() { return matk; }
    public void setMatk(String matk) { this.matk = matk; }

    public String getVaitro() { return vaitro; }
    public void setVaitro(String vaitro) { this.vaitro = vaitro; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public Date getNgaytao() { return ngaytao; }
    public void setNgaytao(Date ngaytao) { this.ngaytao = ngaytao; }
}