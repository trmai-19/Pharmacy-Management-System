package com.pharmacy.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceResponse {
    private String mahd;
    private String manv;
    private LocalDateTime ngayban;
    private Double tongtien;
    private Integer diemsudung; 
    private Double tienthanhtoan;
    private String trangthai;
}