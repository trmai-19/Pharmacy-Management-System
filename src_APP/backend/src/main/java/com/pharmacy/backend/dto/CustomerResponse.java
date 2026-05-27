package com.pharmacy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private String makh;          
    private String tenkh;         
    private String gioitinh;
    private Date ngaysinh;      
    private String sdt;           
    private Double tongdoanhthu;
    private double diemtichluy;
    private String hangtv;        
}