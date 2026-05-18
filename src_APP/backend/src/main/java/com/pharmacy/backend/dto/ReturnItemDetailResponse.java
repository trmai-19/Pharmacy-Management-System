package com.pharmacy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItemDetailResponse {
    private String masp;
    private String tensanpham; // Thêm trường này để Frontend không phải lôi masp ra hiển thị tạm
    private String malo;
    private Integer sl;
    private Double dongiahoan;
    private Double thanhtien;
}