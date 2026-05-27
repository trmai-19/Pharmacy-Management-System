package com.pharmacy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemResponse {
    private String malo;
    private String masp;           
    private String tensanpham;     
    private Integer sl;
    private Double dongia;
    private Double thanhtien;
    private String ghichu;
}