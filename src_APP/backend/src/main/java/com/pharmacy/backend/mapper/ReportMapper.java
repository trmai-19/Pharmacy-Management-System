package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.CustomerSegmentResponse;
import com.pharmacy.backend.dto.StaffPerformanceResponse;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public StaffPerformanceResponse toStaffPerformanceResponse(
            String manv, String tennv, Long soHoaDon, Double tongDoanhThu, Double trungBinh, int rank) {
        
        return StaffPerformanceResponse.builder()
                .manv(manv)
                .tennv(tennv != null ? tennv : "Chưa cập nhật")
                .soHoaDon(soHoaDon)
                .tongDoanhThu(tongDoanhThu)
                .trungBinhHoaDon(Math.round(trungBinh * 100.0) / 100.0)
                .xepHang(rank)
                .build();
    }

    public CustomerSegmentResponse toCustomerSegmentResponse(
            String hangtv, Long soLuong, Double tongDoanhThu, Double trungBinhDoanh, double tyLePhanTram) {
        
        return CustomerSegmentResponse.builder()
                .hangtv(hangtv)
                .soLuong(soLuong)
                .tongDoanhThu(tongDoanhThu)
                .trungBinhDoanhThu(Math.round(trungBinhDoanh * 100.0) / 100.0)
                .tyLePhanTram(tyLePhanTram)
                .build();
    }
}