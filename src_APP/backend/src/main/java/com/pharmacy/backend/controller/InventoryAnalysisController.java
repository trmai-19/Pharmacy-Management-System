package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.DeadStockResponse;
import com.pharmacy.backend.dto.ExpiryTimelineResponse;
import com.pharmacy.backend.dto.ReorderSuggestionResponse;
import com.pharmacy.backend.service.InventoryAnalysisService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class InventoryAnalysisController {

    private final InventoryAnalysisService inventoryAnalysisService;

    /**
     * API 1: Gợi ý tái nhập kho thông minh
     * GET /api/warehouse/reorder-suggestions
     *
     * Tính dựa trên: tốc độ bán 30 ngày gần nhất + tồn kho hiện tại.
     * Chỉ trả về sản phẩm có tồn kho < 30 ngày, sắp xếp theo mức độ ưu tiên.
     */
    @GetMapping("/reorder-suggestions")
    public ResponseEntity<ApiResponse<List<ReorderSuggestionResponse>>> getReorderSuggestions() {
        List<ReorderSuggestionResponse> data = inventoryAnalysisService.getReorderSuggestions();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy gợi ý tái nhập kho thành công", data));
    }

    /**
     * API 2: Phát hiện hàng tồn "chết"
     * GET /api/warehouse/dead-stock?days=60
     *
     * @param days Số ngày không có giao dịch bán để coi là "hàng chết" (mặc định 60 ngày)
     */
    @GetMapping("/dead-stock")
    public ResponseEntity<ApiResponse<List<DeadStockResponse>>> getDeadStock(
            @RequestParam(defaultValue = "60") int days) {
        List<DeadStockResponse> data = inventoryAnalysisService.getDeadStock(days);
        return ResponseEntity.ok(new ApiResponse<>(200,
                "Lấy danh sách hàng tồn không bán trong " + days + " ngày thành công", data));
    }

    /**
     * API 6: Timeline hàng sắp hết hạn theo tháng
     * GET /api/warehouse/expiry-timeline?months=6
     *
     * @param months Số tháng cần xem trước (mặc định 6 tháng)
     */
    @GetMapping("/expiry-timeline")
    public ResponseEntity<ApiResponse<List<ExpiryTimelineResponse>>> getExpiryTimeline(
            @RequestParam(defaultValue = "6") int months) {
        List<ExpiryTimelineResponse> data = inventoryAnalysisService.getExpiryTimeline(months);
        return ResponseEntity.ok(new ApiResponse<>(200,
                "Lấy timeline hạn sử dụng " + months + " tháng tới thành công", data));
    }
}
