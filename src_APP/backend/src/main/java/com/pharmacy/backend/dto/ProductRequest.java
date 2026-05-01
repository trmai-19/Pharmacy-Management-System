package com.pharmacy.backend.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String madm;
    private String tensanpham;
    private String dvt;
    private String congdung;
    private String thanhphan;
    private Double giaban;
    private boolean isManualPrice;
}
