package com.pharmacy.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffPerformanceResponse {
    private String manv;
    private String tennv;
    private Long soHoaDon;              // Tổng số hóa đơn đã lập
    private Double tongDoanhThu;        // Tổng doanh thu từ các HD đã hoàn tất
    private Double trungBinhHoaDon;     // Giá trị trung bình mỗi hóa đơn
    private Integer xepHang;            // Thứ hạng theo doanh thu (1 = cao nhất)
}

