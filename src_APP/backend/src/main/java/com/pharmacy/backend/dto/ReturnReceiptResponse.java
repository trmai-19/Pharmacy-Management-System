package com.pharmacy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnReceiptResponse {
    private String maptKh;
    private String mahd;
    private String manv;
    private LocalDateTime ngaytra;
    private String lydotra;
    private Double tongtienhoan;
    private List<ReturnItemDetailResponse> items;
}