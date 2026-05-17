package com.pharmacy.model;

public class InvoiceProductOption {
    private String masp;
    private String tensanpham;
    private String malo;
    private int slMua;
    private double dongiaHoan;
    
    public InvoiceProductOption(String masp, String tensanpham, String malo, int slMua, double dongiaHoan) {
        this.masp = masp; 
        this.tensanpham = tensanpham; 
        this.malo = malo; 
        this.slMua = slMua; 
        this.dongiaHoan = dongiaHoan;
    }

    public String getMasp() { return masp; }
    public void setMasp(String masp) { this.masp = masp; }

    public String getTensanpham() { return tensanpham; }
    public void setTensanpham(String tensanpham) { this.tensanpham = tensanpham; }

    public String getMalo() { return malo; }
    public void setMalo(String malo) { this.malo = malo; }

    public int getSlMua() { return slMua; }
    public void setSlMua(int slMua) { this.slMua = slMua; }

    public double getDongiaHoan() { return dongiaHoan; }
    public void setDongiaHoan(double dongiaHoan) { this.dongiaHoan = dongiaHoan; }

    @Override 
    public String toString() { 
        return tensanpham + " (Lô: " + malo + ") - Đã mua: " + slMua; 
    }
}