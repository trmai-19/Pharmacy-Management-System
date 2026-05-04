package com.pharmacy.backend.dto;

import lombok.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeadStockResponse {
    private String malo;
    private String masp;
    private String tensanpham;
    private String madm;
    private String tenDanhMuc;
    private Integer sltonHienTai;       // Số lượng hiện còn trong kho
    private Date hsd;                   // Hạn sử dụng
    private Date ngaynhap;              // Ngày nhập lô
    private Long soNgayKhongBan;        // Số ngày không có giao dịch bán
    private String trangThaiHsd;        // "CON_HAN", "SAP_HET_HAN" (< 90 ngày), "HET_HAN"
}
