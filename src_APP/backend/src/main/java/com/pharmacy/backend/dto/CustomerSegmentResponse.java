package com.pharmacy.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSegmentResponse {
    private String hangtv;              // Tên hạng: "THANH VIEN", "Bạc", "Vàng", "Kim Cương"
    private Long soLuong;               // Số khách hàng thuộc hạng này
    private Double tongDoanhThu;        // Tổng doanh thu từ hạng này
    private Double trungBinhDoanhThu;   // Doanh thu trung bình mỗi khách
    private Double tyLePhanTram;        // % số lượng trên tổng khách hàng
}
