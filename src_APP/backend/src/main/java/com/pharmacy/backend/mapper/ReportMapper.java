package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.PerformanceReportResponse;
import com.pharmacy.backend.repository.ChartProjection;

import java.util.List;
import java.util.stream.Collectors;

public class ReportMapper {

    // Quy tắc: Hàm static, dùng Builder
    public static PerformanceReportResponse.PieDataResponse toPieDataResponse(ChartProjection projection) {
        return PerformanceReportResponse.PieDataResponse.builder()
                .category(projection.getLabel() != null ? projection.getLabel() : "Khác")
                .value(projection.getValue() != null ? projection.getValue() : 0.0)
                .build();
    }

    public static List<PerformanceReportResponse.PieDataResponse> toPieDataList(List<ChartProjection> projections) {
        return projections.stream()
                .map(ReportMapper::toPieDataResponse)
                .collect(Collectors.toList());
    }
}