
package com.pharmacy.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CustomerReportResponse {
    
    // 1. CÁC THẺ KPI CHÍNH
    private String totalCustomers;
    private String newCustomers;
    private String returnRate;
    private String vipCustomers;
    private String lostCustomers;

    // 2. CÁC THẺ TREND (% TĂNG TRƯỞNG)
    @Builder.Default private Double totalCustomersTrend = 0.0;
    @Builder.Default private Double newCustomersTrend = 0.0;
    @Builder.Default private Double returnRateTrend = 0.0;
    @Builder.Default private Double vipCustomersTrend = 0.0;
    @Builder.Default private Double lostCustomersTrend = 0.0;

    // 3. DỮ LIỆU BIỂU ĐỒ (CHARTS)
    private List<GrowthData> customerGrowth;      
    private List<ChartData> topSpenders;          
    private List<ChartData> customerSegmentation; 
    private List<ChartData> genderTotal;          
    private List<ChartData> ageTotal;             
    private List<ChartData> genderNew;            
    private List<ChartData> ageNew;               
    private List<ChartData> genderReturning;      
    private List<ChartData> ageReturning;         

    // 4. INNER CLASSES ĐỂ MAP DỮ LIỆU
    @Data
    @Builder
    public static class ChartData {
        private String label;
        private Double value; 
    }

    @Data
    @Builder
    public static class GrowthData {
        private String period;          
        private Long totalCustomers;    
        private Long newCustomers;      
        private Long returningCustomers;
    }
}