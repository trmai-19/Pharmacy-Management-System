package com.pharmacy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // Thêm cái này
@AllArgsConstructor // Thêm cái này
public class CustomerResponse {
    private String makh;          
    private String tenkh;         
    private String gioitinh;      
    private String sdt;           
    private Double tongdoanhthu;
    private String hangtv;        
}