package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.PerformanceReportResponse;

public interface ReportService {
    PerformanceReportResponse getPerformanceDashboard();
}