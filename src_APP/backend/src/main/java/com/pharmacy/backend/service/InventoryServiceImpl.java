
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

            // 1. Tính toán số liệu KPI thật
            Long totalMeds = inventoryRepository.countTotalMedicines(paramYear, paramQuarter, pGroup);
            Long lowStock = inventoryRepository.countLowStock(paramYear, paramQuarter, pGroup);
            Long nearExpiry = inventoryRepository.countNearExpiry(paramYear, paramQuarter, pGroup);
            Long expired = inventoryRepository.countExpired(paramYear, paramQuarter, pGroup);
            Double valueRaw = inventoryRepository.getInventoryValue(paramYear, paramQuarter, pGroup);

            String formattedValue = String.format(java.util.Locale.US, "%.1fM", valueRaw / 1000000.0);

            // 2. Map dữ liệu bảng danh sách thuốc
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

            // 3. Map dữ liệu biểu đồ tròn
            List<ChartProjection> catRaw = inventoryRepository.getCategoryDistribution(paramYear, paramQuarter, pGroup);
            List<InventoryReportResponse.ChartData> categoryDistribution = catRaw.stream().map(c ->
                    InventoryReportResponse.ChartData.builder()
                            .label(c.getLabel())
                            .value(c.getValue().longValue())
                            .build()
            ).toList();

            // 4. Map dữ liệu biểu đồ cột Top 5 đọng hàng
            List<ChartProjection> slowRaw = inventoryRepository.getSlowestMovingProducts(paramYear, paramQuarter, pGroup);
            List<InventoryReportResponse.ChartData> slowestMoving = slowRaw.stream().map(s ->
                    InventoryReportResponse.ChartData.builder()
                            .label(s.getLabel())
                            .value(s.getValue().longValue())
                            .build()
            ).toList();

            return InventoryReportResponse.builder()
                    .totalMedicines(String.valueOf(totalMeds))
                    .lowStockCount(String.valueOf(lowStock))
                    .nearExpiryCount(String.valueOf(nearExpiry))
                    .expiredCount(String.valueOf(expired))
                    .inventoryValue(formattedValue)
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
}