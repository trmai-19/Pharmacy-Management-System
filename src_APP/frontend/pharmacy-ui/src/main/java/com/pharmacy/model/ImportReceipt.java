package com.pharmacy.model;

public class ImportReceipt {
    private String mapn;
    private String ngaynhap;
    private String mancc;
    private double tongtien;
    private String trangthai;

    public ImportReceipt(String mapn, String ngaynhap, String mancc, double tongtien, String trangthai) {
        this.mapn = mapn; this.ngaynhap = ngaynhap; this.mancc = mancc;
        this.tongtien = tongtien; this.trangthai = trangthai;
    }
    // Cấp các Getter
    public String getMapn() { return mapn; }
    public String getNgaynhap() { return ngaynhap; }
    public String getMancc() { return mancc; }
    public double getTongtien() { return tongtien; }
    public String getTrangthai() { return trangthai; }
}