package com.pharmacy.backend.dto;

import lombok.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceSuggestionResponse {
    private String masp;
    private String tensanpham;
    private String madm;
    private String tenDanhMuc;
    private Double giaNhapGanNhat;      // Giá nhập từ phiếu nhập gần nhất
    private Date ngayNhapGanNhat;       // Ngày của phiếu nhập đó
    private Double tyleLoiNhuan;        // Tỉ lệ lợi nhuận của danh mục (%)
    private Double giaDeXuat;           // = giaNhapGanNhat * (1 + tyleLoiNhuan/100)
    private Double giaBanHienTai;       // Giá bán đang áp dụng
    private Double chenhLech;           // giaDeXuat - giaBanHienTai
    private boolean isManualPrice;      // true nếu giá đang được set thủ công
    private String ghiChu;              // Ghi chú nếu chưa có lịch sử nhập
}
