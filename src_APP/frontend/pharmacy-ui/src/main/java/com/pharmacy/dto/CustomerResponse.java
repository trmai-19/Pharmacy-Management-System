package com.pharmacy.dto;

public class CustomerResponse {
    private String makh;
    private String tenkh;
    private String gioitinh;
    private String ngaysinh;
    private String sdt;
    private Double tongdoanhthu;
    private double diemtichluy;
    private String hangtv;

    public CustomerResponse() {}

    public String getMakh() { return makh; }
    public void setMakh(String makh) { this.makh = makh; }

    public String getTenkh() { return tenkh; }
    public void setTenkh(String tenkh) { this.tenkh = tenkh; }

    public String getGioitinh() { return gioitinh; }
    public void setGioitinh(String gioitinh) { this.gioitinh = gioitinh; }

    public String getNgaysinh() { return ngaysinh; }
    public void setNgaysinh(String ngaysinh) { this.ngaysinh = ngaysinh; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public Double getTongdoanhthu() { return tongdoanhthu; }
    public void setTongdoanhthu(Double tongdoanhthu) { this.tongdoanhthu = tongdoanhthu; }

    public double getDiemtichluy() { return diemtichluy; }
    public void setDiemtichluy(double diemtichluy) { this.diemtichluy = diemtichluy; }

    public String getHangtv() { return hangtv; }
    public void setHangtv(String hangtv) { this.hangtv = hangtv; }
}
