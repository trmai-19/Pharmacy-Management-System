package com.pharmacy.model;

public class ReturnItemRow {
    private String malo;
    private int sl;

    public ReturnItemRow(String malo, int sl) {
        this.malo = malo;
        this.sl = sl;
    }

    public String getMalo() { return malo; }
    public void setMalo(String malo) { this.malo = malo; }
    public int getSl() { return sl; }
    public void setSl(int sl) { this.sl = sl; }
}