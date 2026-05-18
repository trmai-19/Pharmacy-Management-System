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
public class ReturnReceiptListResponse {
    private String maptKh;
    private LocalDateTime ngaytra;
    private String mahd;
    private String tenkh;          // Tên khách hàng để hiển thị trực quan
    private String sanPhamTomTat;  // Chuỗi tóm tắt danh mục (VD: "Vitamin C, Paracetamol...")
    private Integer tongSl;        // Tổng số lượng mặt hàng hoàn trả
    private String lydotra;
    private Double tongtienhoan;
    private Integer diemhoan;
}