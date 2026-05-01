package com.pharmacy.backend.dto;

import java.util.Date;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportReceiptResponse {
    private String mapn;
    private String manv;
    private String mancc;
    private Date ngaynhap;
    private Double tongtien;
    private String trangthai;
}