package com.pharmacy.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpiryTimelineResponse {
    private String thang;           // Định dạng "YYYY-MM", ví dụ: "2025-08"
    private Integer soLo;           // Số lô sắp hết hạn trong tháng đó
    private Integer tongSoLuong;    // Tổng số lượng sản phẩm trong các lô đó
}
