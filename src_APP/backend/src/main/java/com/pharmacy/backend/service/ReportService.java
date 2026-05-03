package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.CustomerSegmentResponse;
import com.pharmacy.backend.dto.StaffPerformanceResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {
    /** API 3: GET /api/admin/reports/staff-performance?from=...&to=... */
    List<StaffPerformanceResponse> getStaffPerformance(LocalDateTime from, LocalDateTime to);

    /** API 4: GET /api/admin/reports/customer-segments */
    List<CustomerSegmentResponse> getCustomerSegments();
}
