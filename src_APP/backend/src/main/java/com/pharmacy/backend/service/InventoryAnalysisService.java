package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.DeadStockResponse;
import com.pharmacy.backend.dto.ExpiryTimelineResponse;
import com.pharmacy.backend.dto.ReorderSuggestionResponse;

import java.util.List;

public interface InventoryAnalysisService {
    /** API 1: GET /api/warehouse/reorder-suggestions */
    List<ReorderSuggestionResponse> getReorderSuggestions();

    /** API 2: GET /api/warehouse/dead-stock?days=60 */
    List<DeadStockResponse> getDeadStock(int days);

    /** API 6: GET /api/warehouse/expiry-timeline?months=6 */
    List<ExpiryTimelineResponse> getExpiryTimeline(int months);
}
