package com.pharmacy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicineResponse {
    private String masp;      
    private String tensanpham; 
    private String dvt;        
    private String congdung;  
    private String thanhphan;  
    private Double giaban;

    public MedicineResponse() {}

    public String getMasp() { return masp; }
    public void setMasp(String masp) { this.masp = masp; }

    public String getTensanpham() { return tensanpham; }
    public void setTensanpham(String tensanpham) { this.tensanpham = tensanpham; }

    public String getDvt() { return dvt; }
    public void setDvt(String dvt) { this.dvt = dvt; }

    public String getCongdung() { return congdung; }
    public void setCongdung(String congdung) { this.congdung = congdung; }

    public String getThanhphan() { return thanhphan; }
    public void setThanhphan(String thanhphan) { this.thanhphan = thanhphan; }

    public Double getGiaban() { return giaban; }
    public void setGiaban(Double giaban) { this.giaban = giaban; }
}
