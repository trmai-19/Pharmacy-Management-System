package com.pharmacy.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineResponse {
    private String masp;      
    private String tensanpham; 
    private String dvt;        
    private String congdung;  
    private String thanhphan;  
    private Double giaban;    
}