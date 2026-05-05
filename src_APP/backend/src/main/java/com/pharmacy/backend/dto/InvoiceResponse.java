package com.pharmacy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder (toBuilder = true)
@NoArgsConstructor 
@AllArgsConstructor
public class InvoiceResponse {
    private String mahd;
    private String manv;
    private String makh;
    private LocalDateTime ngayban;
    private Double tongtien;
    private Integer diemsudung; 
    private Double tienthanhtoan;
    private String trangthai;

    private List<InvoiceItemResponse> items;
}