package com.pharmacy.model;

public class SupplierReturnTicket {
    private String maptNcc;
    private String ngaytra;
    private String lydotra;
    private double tongtien;

    public SupplierReturnTicket(String maptNcc, String ngaytra, String lydotra, double tongtien) {
        this.maptNcc = maptNcc; this.ngaytra = ngaytra;
        this.lydotra = lydotra; this.tongtien = tongtien;
    }
    // Cấp các Getter
    public String getMaptNcc() { return maptNcc; }
    public String getNgaytra() { return ngaytra; }
    public String getLydotra() { return lydotra; }
    public double getTongtien() { return tongtien; }
}