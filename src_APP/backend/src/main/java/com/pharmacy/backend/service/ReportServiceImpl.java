package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.CustomerSegmentResponse;
import com.pharmacy.backend.dto.StaffPerformanceResponse;
import com.pharmacy.backend.mapper.ReportMapper;
import com.pharmacy.backend.repository.CustomerRepository;
import com.pharmacy.backend.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ReportMapper reportMapper;

    // =========================================================
    // API 3: GET /api/admin/reports/staff-performance
    // =========================================================
    @Override
    public List<StaffPerformanceResponse> getStaffPerformance(LocalDateTime from, LocalDateTime to) {
        List<Object[]> rows = invoiceRepository.findStaffPerformance(from, to);
        List<StaffPerformanceResponse> result = new ArrayList<>();

        // rows đã ORDER BY doanh thu DESC từ query -> xếp hạng theo index
        AtomicInteger rank = new AtomicInteger(1);

        for (Object[] row : rows) {
            String manv         = (String) row[0];
            String tennv        = (String) row[1];
            Long soHoaDon       = row[2] != null ? ((Number) row[2]).longValue()   : 0L;
            Double tongDoanhThu = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;
            Double trungBinh    = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;

            result.add(reportMapper.toStaffPerformanceResponse(manv, tennv, soHoaDon, tongDoanhThu, trungBinh, rank.getAndIncrement()));
        }

        return result;
    }

    // =========================================================
    // API 4: GET /api/admin/reports/customer-segments
    // =========================================================
    @Override
    public List<CustomerSegmentResponse> getCustomerSegments() {
        List<Object[]> rows = customerRepository.findCustomerSegments();

        // Tính tổng khách hàng để tính % (dùng để tính tỉ lệ phần trăm)
        long tongKhachHang = customerRepository.count();

        List<CustomerSegmentResponse> result = new ArrayList<>();

        for (Object[] row : rows) {
            String hangtv          = (String) row[0];
            Long soLuong           = row[1] != null ? ((Number) row[1]).longValue()   : 0L;
            Double tongDoanhThu    = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            Double trungBinhDoanh  = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;

            double tyLe = tongKhachHang > 0
                    ? Math.round((soLuong * 100.0 / tongKhachHang) * 100.0) / 100.0
                    : 0.0;

            result.add(reportMapper.toCustomerSegmentResponse(hangtv, soLuong, tongDoanhThu, trungBinhDoanh, tyLe));
        }

        return result;
    }
}
