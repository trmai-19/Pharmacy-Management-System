package com.pharmacy.model;

public class ImportItemRow {
    private String masp;
    private String productName;
    private String makho;
    private int sl;
    private double gianhap;
    private String dvt;
    private String ghichu;
    private String ngaysx;
    private String hsd;

    public ImportItemRow(String masp, String productName, String makho, int sl, double gianhap, String dvt, String ghichu, String ngaysx, String hsd) {
        this.masp = masp;
        this.productName = productName;
        this.makho = makho;
        this.sl = sl;
        this.gianhap = gianhap;
        this.dvt = dvt;
        this.ghichu = ghichu;
        this.ngaysx = ngaysx;
        this.hsd = hsd;
    }

    public String getMasp() { return masp; }
    public String getProductName() { return productName; }
    public String getMakho() { return makho; }
    public int getSl() { return sl; }
    public double getGianhap() { return gianhap; }
    public String getDvt() { return dvt; }
    public String getGhichu() { return ghichu; }
    public String getNgaysx() { return ngaysx; }
    public String getHsd() { return hsd; }
}