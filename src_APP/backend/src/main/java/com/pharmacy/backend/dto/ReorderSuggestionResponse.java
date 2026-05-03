package com.pharmacy.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReorderSuggestionResponse {
    private String masp;
    private String tensanpham;
    private String madm;
    private String tenDanhMuc;
    private Integer sltonHienTai;       // Tồn kho hiện tại
    private Long luongBan30Ngay;        // Số lượng đã bán trong 30 ngày
    private Double luongBanTBNgay;      // Trung bình bán/ngày (luongBan30Ngay / 30.0)
    private Integer soNgayDuHang;       // Ước tính tồn kho đủ bao nhiêu ngày
    private Integer deXuatNhap;         // Số lượng đề xuất nhập thêm (đủ 60 ngày)
    private String mucDoUuTien;         // "KHAN_CAP" (< 7 ngày), "THAP" (< 30 ngày)
}

