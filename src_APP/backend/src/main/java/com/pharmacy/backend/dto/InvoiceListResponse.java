package com.pharmacy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceListResponse {
    private String mahd;
    private LocalDateTime ngayban;
    private String tenkh;
    private String sdt;
    private Double tongtien;
    private String trangthai;
    private Integer diemsudung;
}