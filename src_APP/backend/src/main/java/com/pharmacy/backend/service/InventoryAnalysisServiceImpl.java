package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.DeadStockResponse;
import com.pharmacy.backend.dto.ExpiryTimelineResponse;
import com.pharmacy.backend.dto.ReorderSuggestionResponse;
import com.pharmacy.backend.mapper.InventoryMapper;
import com.pharmacy.backend.model.Batch;
import com.pharmacy.backend.model.Product;
import com.pharmacy.backend.model.Warehouse;
import com.pharmacy.backend.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryAnalysisServiceImpl implements InventoryAnalysisService {

    private final WarehouseRepository warehouseRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryMapper inventoryMapper;

    // =========================================================
    // API 1: GET /api/warehouse/reorder-suggestions
    // =========================================================
    @Override
    public List<ReorderSuggestionResponse> getReorderSuggestions() {
        LocalDateTime since30Days = LocalDateTime.now().minusDays(30);

        // Bước 1: Tổng tồn kho hiện tại theo sản phẩm -> Map<masp, totalStock>
        Map<String, Long> stockByProduct = new HashMap<>();
        for (Object[] row : warehouseRepository.findStockByProduct()) {
            String masp = (String) row[0];
            Long stock = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            stockByProduct.put(masp, stock);
        }

        // Bước 2: Tổng đã bán trong 30 ngày theo sản phẩm -> Map<masp, totalSold>
        Map<String, Long> soldByProduct = new HashMap<>();
        for (Object[] row : invoiceDetailRepository.findSalesVelocityByProduct(since30Days)) {
            String masp = (String) row[0];
            Long sold = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            soldByProduct.put(masp, sold);
        }

        // Bước 3: Map tên sản phẩm và danh mục
        Map<String, Product> productMap = productRepository.findAll().stream()
                .collect(Collectors.toMap(Product::getMasp, p -> p));
        Map<String, String> categoryNameMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(c -> c.getMadm(), c -> c.getTendm()));

        // Bước 4: Tính toán và lọc chỉ những sản phẩm cần nhập thêm
        List<ReorderSuggestionResponse> result = new ArrayList<>();

        for (Map.Entry<String, Long> entry : stockByProduct.entrySet()) {
            String masp = entry.getKey();
            long currentStock = entry.getValue();
            long sold30Days = soldByProduct.getOrDefault(masp, 0L);

            // Chỉ xét sản phẩm có lịch sử bán (tránh gợi ý nhập hàng không ai mua)
            if (sold30Days == 0) continue;

            double dailyAvg = sold30Days / 30.0;
            int daysOfStock = dailyAvg > 0 ? (int) (currentStock / dailyAvg) : Integer.MAX_VALUE;

            // Chỉ đưa vào danh sách khi tồn kho < 30 ngày
            if (daysOfStock >= 30) continue;

            Product product = productMap.get(masp);
            if (product == null) continue;

            // Đề xuất nhập đủ cho 60 ngày
            int suggested = Math.max(0, (int) (dailyAvg * 60) - (int) currentStock);

            String priority = daysOfStock < 7 ? "KHAN_CAP" : "THAP";
            String tenDanhMuc = categoryNameMap.getOrDefault(product.getMadm(), "");
            result.add(inventoryMapper.toReorderSuggestionResponse(
                    masp, product, tenDanhMuc, currentStock, sold30Days, dailyAvg, daysOfStock, suggested, priority));
        }

        // Sắp xếp: hàng khẩn cấp lên đầu, sau đó theo số ngày tồn kho tăng dần
        result.sort(Comparator
                .comparing(ReorderSuggestionResponse::getMucDoUuTien).reversed()
                .thenComparingInt(ReorderSuggestionResponse::getSoNgayDuHang));

        return result;
    }

    // =========================================================
    // API 2: GET /api/warehouse/dead-stock?days=60
    // =========================================================
    @Override
    public List<DeadStockResponse> getDeadStock(int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        Date now = new Date();

        // Bước 1: Lấy danh sách MALO đã bán trong `days` ngày qua
        Set<String> activeIds = new HashSet<>(invoiceDetailRepository.findActiveBatchIdsSince(cutoff));

        // Bước 2: Lọc kho — lô còn hàng nhưng không nằm trong danh sách đã bán
        List<Warehouse> deadWarehouses = warehouseRepository.findAll().stream()
                .filter(w -> w.getSlton() != null && w.getSlton() > 0)
                .filter(w -> !activeIds.contains(w.getMalo()))
                .collect(Collectors.toList());

        // Bước 3: Enrich với thông tin Batch và Product
        Map<String, Batch> batchMap = batchRepository.findAll().stream()
                .collect(Collectors.toMap(Batch::getMalo, b -> b));
        Map<String, Product> productMap = productRepository.findAll().stream()
                .collect(Collectors.toMap(Product::getMasp, p -> p));
        Map<String, String> categoryNameMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(c -> c.getMadm(), c -> c.getTendm()));

        return deadWarehouses.stream().map(w -> {
            Batch batch = batchMap.get(w.getMalo());
            if (batch == null) return null;

            Product product = productMap.get(batch.getMasp());
            String tensanpham = product != null ? product.getTensanpham() : "N/A";
            String tenDanhMuc = categoryNameMap.getOrDefault(batch.getMadm(), "");

            // Số ngày không bán: tính từ ngày nhập (trường hợp chưa bao giờ bán)
            long soNgayKhongBan = batch.getNgaynhap() != null
                    ? (now.getTime() - batch.getNgaynhap().getTime()) / (1000 * 60 * 60 * 24)
                    : 0L;

            // Trạng thái HSD
            String trangThaiHsd;
            if (batch.getHsd() == null) {
                trangThaiHsd = "KHONG_RO";
            } else if (batch.getHsd().before(now)) {
                trangThaiHsd = "HET_HAN";
            } else {
                long daysToExpiry = (batch.getHsd().getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                trangThaiHsd = daysToExpiry < 90 ? "SAP_HET_HAN" : "CON_HAN";
            }

            return inventoryMapper.toDeadStockResponse(w, batch, tensanpham, tenDanhMuc, soNgayKhongBan, trangThaiHsd);
        })
        .filter(Objects::nonNull)
        // Ưu tiên hiển thị: hàng đã hết hạn hoặc sắp hết hạn lên đầu
        .sorted(Comparator
                .comparing(DeadStockResponse::getTrangThaiHsd)
                .thenComparing(Comparator.comparing(DeadStockResponse::getSoNgayKhongBan).reversed()))
        .collect(Collectors.toList());
    }

    // =========================================================
    // API 6: GET /api/warehouse/expiry-timeline?months=6
    // =========================================================
    @Override
    public List<ExpiryTimelineResponse> getExpiryTimeline(int months) {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MONTH, months);
        Date endDate = cal.getTime();

        List<Batch> expiring = batchRepository.findExpiringBatchesWithStock(now, endDate);

        // Nhóm theo "YYYY-MM"
        Map<String, List<Batch>> byMonth = new TreeMap<>(); // TreeMap để tự sắp xếp theo tháng
        for (Batch b : expiring) {
            Calendar c = Calendar.getInstance();
            c.setTime(b.getHsd());
            String monthKey = String.format("%04d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
            byMonth.computeIfAbsent(monthKey, k -> new ArrayList<>()).add(b);
        }

        return byMonth.entrySet().stream().map(entry -> {
            int tongSoLuong = entry.getValue().stream()
                    .mapToInt(b -> b.getSlsp() != null ? b.getSlsp() : 0)
                    .sum();
            return inventoryMapper.toExpiryTimelineResponse(entry.getKey(), entry.getValue().size(), tongSoLuong);
        }).collect(Collectors.toList());
    }
}
