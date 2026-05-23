
package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.InventoryReportResponse;
import com.pharmacy.backend.repository.InventoryRepository;
import com.pharmacy.backend.repository.ChartProjection;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public InventoryReportResponse getInventoryDashboard(Integer year, Integer quarter, String productGroup, String customerType) {
        try {
            int paramYear = (year != null) ? year : 0;
            int paramQuarter = (quarter != null) ? quarter : 0;
            String pGroup = (productGroup != null) ? productGroup : "All Product Groups";

            // 1. Tính toán số liệu KPI cho kỳ HIỆN TẠI
            Long totalMeds = inventoryRepository.countTotalMedicines(paramYear, paramQuarter, pGroup);
            Long lowStock = inventoryRepository.countLowStock(paramYear, paramQuarter, pGroup);
            Long nearExpiry = inventoryRepository.countNearExpiry(paramYear, paramQuarter, pGroup);
            Long expired = inventoryRepository.countExpired(paramYear, paramQuarter, pGroup);
            Double valueRaw = inventoryRepository.getInventoryValue(paramYear, paramQuarter, pGroup);

            String formattedValue = String.format(java.util.Locale.US, "%.1fM", valueRaw / 1000000.0);

            // 👇 2. XÁC ĐỊNH VÀ LẤY DỮ LIỆU KỲ TRƯỚC (Để làm data cho thẻ Trend) 👇
            int prevYear = paramYear;
            int prevQuarter = paramQuarter;
            if (paramQuarter > 0) {
                if (paramQuarter == 1) { prevQuarter = 4; prevYear = (paramYear > 0) ? paramYear - 1 : 0; }
                else { prevQuarter = paramQuarter - 1; }
            } else if (paramYear > 0) {
                prevYear = paramYear - 1;
            }

            Long prevTotal = inventoryRepository.countTotalMedicines(prevYear, prevQuarter, pGroup);
            Long prevLowStock = inventoryRepository.countLowStock(prevYear, prevQuarter, pGroup);
            Long prevNearExpiry = inventoryRepository.countNearExpiry(prevYear, prevQuarter, pGroup);
            Long prevExpired = inventoryRepository.countExpired(prevYear, prevQuarter, pGroup);
            Double prevValueRaw = inventoryRepository.getInventoryValue(prevYear, prevQuarter, pGroup);
            // 👆 ============================================================= 👆

            // 3. Map dữ liệu bảng danh sách thuốc
            List<Object[]> tableRaw = inventoryRepository.getInventoryTableData(paramYear, paramQuarter, pGroup);
            List<InventoryReportResponse.MedicineRow> tableData = tableRaw.stream().map(row ->
                    InventoryReportResponse.MedicineRow.builder()
                            .maThuoc(String.valueOf(row[0]))
                            .tenThuoc(String.valueOf(row[1]))
                            .phanLoai(String.valueOf(row[2]))
                            .tonKho(((Number) row[3]).longValue())
                            .hanSuDung(String.valueOf(row[4]))
                            .tinhTrang(String.valueOf(row[5]))
                            .build()
            ).toList();

            // 4. Map dữ liệu biểu đồ tròn/cột danh mục
            List<ChartProjection> catRaw = inventoryRepository.getCategoryDistribution(paramYear, paramQuarter, pGroup);
            List<InventoryReportResponse.ChartData> categoryDistribution = catRaw.stream().map(c ->
                    InventoryReportResponse.ChartData.builder()
                            .label(c.getLabel())
                            .value(c.getValue().longValue())
                            .build()
            ).toList();

            // 5. Map dữ liệu biểu đồ cột Top 5 đọng hàng
            List<ChartProjection> slowRaw = inventoryRepository.getSlowestMovingProducts(paramYear, paramQuarter, pGroup);
            List<InventoryReportResponse.ChartData> slowestMoving = slowRaw.stream().map(s ->
                    InventoryReportResponse.ChartData.builder()
                            .label(s.getLabel())
                            .value(s.getValue().longValue())
                            .build()
            ).toList();

            // ĐÃ XÓA 3 dòng var lỗi bị trùng lặp ở đây. 
            // Trả về dữ liệu đóng gói cùng với số liệu phần trăm (Trend)
            return InventoryReportResponse.builder()
                    .totalMedicines(String.valueOf(totalMeds))
                    .lowStockCount(String.valueOf(lowStock))
                    .nearExpiryCount(String.valueOf(nearExpiry))
                    .expiredCount(String.valueOf(expired))
                    .inventoryValue(formattedValue)
                    // 👇 Gắn 5 kết quả Trend vào Response 👇
                    .totalMedicinesTrend(calculateTrend(totalMeds, prevTotal))
                    .lowStockTrend(calculateTrend(lowStock, prevLowStock))
                    .nearExpiryTrend(calculateTrend(nearExpiry, prevNearExpiry))
                    .expiredTrend(calculateTrend(expired, prevExpired))
                    .inventoryValueTrend(calculateTrend(valueRaw, prevValueRaw))
                    // ===================================
                    .tableData(tableData)
                    .categoryDistribution(categoryDistribution)
                    .slowestMoving(slowestMoving)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi hệ thống khi tải báo cáo kho: " + e.getMessage());
        }
    }

    @Override
    public List<String> getFilterCategories() {
        return inventoryRepository.findAllActiveCategories();
    }

    private Double calculateTrend(double current, double previous) {
        // Nếu cả hai đều là 0, xu hướng là 0%
        if (current == 0 && previous == 0) return 0.0;
        
        // Nếu kỳ trước bằng 0 nhưng hiện tại có dữ liệu -> Tăng trưởng 100%
        if (previous == 0) return 100.0;
        
        // Công thức chuẩn: ((Hiện tại - Trước) / Trước) * 100
        double trend = ((current - previous) / previous) * 100.0;
        
        // Giới hạn kết quả để tránh các con số quá ảo (ví dụ: > 1000%)
        return Math.max(-100.0, Math.min(1000.0, trend));
    }
}