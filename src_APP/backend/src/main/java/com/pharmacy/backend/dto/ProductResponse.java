package com.pharmacy.backend.dto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String masp;
    private String madm;
    private String tensanpham;
    private String dvt;
    private String congdung;
    private String thanhphan;
    private Double giaban;
    private String trangthai;
}
