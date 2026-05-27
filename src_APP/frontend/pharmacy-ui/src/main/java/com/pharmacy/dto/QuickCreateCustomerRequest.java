package com.pharmacy.dto;

public class QuickCreateCustomerRequest {
    private String tenkh;
    private String sdt;
    private String gioitinh;

    public QuickCreateCustomerRequest() {}

    public QuickCreateCustomerRequest(String tenkh, String sdt, String gioitinh) {
        this.tenkh = tenkh;
        this.sdt = sdt;
        this.gioitinh = gioitinh;
    }

    public String getTenkh() {
        return tenkh;
    }

    public void setTenkh(String tenkh) {
        this.tenkh = tenkh;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(String gioitinh) {
        this.gioitinh = gioitinh;
    }
}
