package com.pharmacy.dto;

import java.util.Date;

public class EmployeeResponse {
    private String manv;
    private String matk;
    private String tennv;
    private String gioitinh;
    private Date ngaysinh;
    private String sdt;
    private String chucvu;
    private String trangthai;

    public EmployeeResponse() {}

    public String getManv() { return manv; }
    public void setManv(String manv) { this.manv = manv; }

    public String getMatk() { return matk; }
    public void setMatk(String matk) { this.matk = matk; }

    public String getTennv() { return tennv; }
    public void setTennv(String tennv) { this.tennv = tennv; }

    public String getGioitinh() { return gioitinh; }
    public void setGioitinh(String gioitinh) { this.gioitinh = gioitinh; }

    public Date getNgaysinh() { return ngaysinh; }
    public void setNgaysinh(Date ngaysinh) { this.ngaysinh = ngaysinh; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getChucvu() { return chucvu; }
    public void setChucvu(String chucvu) { this.chucvu = chucvu; }

    public String getTrangthai() { return trangthai; }
    public void setTrangthai(String trangthai) { this.trangthai = trangthai; }
}
