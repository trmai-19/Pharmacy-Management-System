package com.pharmacy.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class InventoryReportResponse {
    // Các thẻ KPI
    private String totalMedicines;
    private String lowStockCount;
    private String nearExpiryCount;
    private String expiredCount;
    private String inventoryValue;

    private Double totalMedicinesTrend;
    private Double lowStockTrend;
    private Double nearExpiryTrend;
    private Double expiredTrend;
    private Double inventoryValueTrend;

    // Dữ liệu bảng danh sách thuốc
    private List<MedicineRow> tableData;

    // Dữ liệu biểu đồ
    private List<ChartData> categoryDistribution; // Biểu đồ tròn
    private List<ChartData> slowestMoving;        // Biểu đồ cột Top 5

    @Data
    @Builder
    public static class MedicineRow {
        private String maThuoc;
        private String tenThuoc;
        private String phanLoai;
        private Long tonKho;
        private String hanSuDung;
        private String tinhTrang;
    }

    @Data
    @Builder
    public static class ChartData {
        private String label;
        private Long value;
    }
}