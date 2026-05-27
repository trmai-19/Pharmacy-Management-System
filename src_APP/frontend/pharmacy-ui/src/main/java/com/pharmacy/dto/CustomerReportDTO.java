package com.pharmacy.dto;

import java.util.List;

public class CustomerReportDTO {
    // 1. KPI & Trend
    private String totalCustomers;
    private Double totalCustomersTrend;
    private String newCustomers;
    private Double newCustomersTrend;
    private String returnRate;
    private Double returnRateTrend;
    private String vipCustomers;
    private Double vipCustomersTrend;
    private String lostCustomers;
    private Double lostCustomersTrend;

    // 2. Growth & Bar Charts
    private List<GrowthData> customerGrowth;
    private List<ChartData> topSpenders;
    private List<ChartData> customerSegmentation;

    // 3. Pie Charts (Được chia làm 3 bộ để bắt sự kiện click nút)
    private List<ChartData> genderTotal;
    private List<ChartData> genderNew;
    private List<ChartData> genderReturning;
    
    private List<ChartData> ageTotal;
    private List<ChartData> ageNew;
    private List<ChartData> ageReturning;

    // --- GETTERS & SETTERS (Cậu có thể dùng VS Code auto-generate cho nhanh nhé) ---
    public String getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(String totalCustomers) { this.totalCustomers = totalCustomers; }
    public Double getTotalCustomersTrend() { return totalCustomersTrend; }
    public void setTotalCustomersTrend(Double totalCustomersTrend) { this.totalCustomersTrend = totalCustomersTrend; }
    
    public String getNewCustomers() { return newCustomers; }
    public void setNewCustomers(String newCustomers) { this.newCustomers = newCustomers; }
    public Double getNewCustomersTrend() { return newCustomersTrend; }
    public void setNewCustomersTrend(Double newCustomersTrend) { this.newCustomersTrend = newCustomersTrend; }
    
    public String getReturnRate() { return returnRate; }
    public void setReturnRate(String returnRate) { this.returnRate = returnRate; }
    public Double getReturnRateTrend() { return returnRateTrend; }
    public void setReturnRateTrend(Double returnRateTrend) { this.returnRateTrend = returnRateTrend; }
    
    public String getVipCustomers() { return vipCustomers; }
    public void setVipCustomers(String vipCustomers) { this.vipCustomers = vipCustomers; }
    public Double getVipCustomersTrend() { return vipCustomersTrend; }
    public void setVipCustomersTrend(Double vipCustomersTrend) { this.vipCustomersTrend = vipCustomersTrend; }
    
    public String getLostCustomers() { return lostCustomers; }
    public void setLostCustomers(String lostCustomers) { this.lostCustomers = lostCustomers; }
    public Double getLostCustomersTrend() { return lostCustomersTrend; }
    public void setLostCustomersTrend(Double lostCustomersTrend) { this.lostCustomersTrend = lostCustomersTrend; }

    public List<GrowthData> getCustomerGrowth() { return customerGrowth; }
    public void setCustomerGrowth(List<GrowthData> customerGrowth) { this.customerGrowth = customerGrowth; }
    public List<ChartData> getTopSpenders() { return topSpenders; }
    public void setTopSpenders(List<ChartData> topSpenders) { this.topSpenders = topSpenders; }
    public List<ChartData> getCustomerSegmentation() { return customerSegmentation; }
    public void setCustomerSegmentation(List<ChartData> customerSegmentation) { this.customerSegmentation = customerSegmentation; }

    public List<ChartData> getGenderTotal() { return genderTotal; }
    public void setGenderTotal(List<ChartData> genderTotal) { this.genderTotal = genderTotal; }
    public List<ChartData> getGenderNew() { return genderNew; }
    public void setGenderNew(List<ChartData> genderNew) { this.genderNew = genderNew; }
    public List<ChartData> getGenderReturning() { return genderReturning; }
    public void setGenderReturning(List<ChartData> genderReturning) { this.genderReturning = genderReturning; }

    public List<ChartData> getAgeTotal() { return ageTotal; }
    public void setAgeTotal(List<ChartData> ageTotal) { this.ageTotal = ageTotal; }
    public List<ChartData> getAgeNew() { return ageNew; }
    public void setAgeNew(List<ChartData> ageNew) { this.ageNew = ageNew; }
    public List<ChartData> getAgeReturning() { return ageReturning; }
    public void setAgeReturning(List<ChartData> ageReturning) { this.ageReturning = ageReturning; }

    // --- INNER CLASSES ---
    public static class ChartData {
        private String label;
        private Double value;
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
    }

    public static class GrowthData {
        private String period;
        private Long totalCustomers;
        private Long newCustomers;
        private Long returningCustomers;
        
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
        public Long getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(Long totalCustomers) { this.totalCustomers = totalCustomers; }
        public Long getNewCustomers() { return newCustomers; }
        public void setNewCustomers(Long newCustomers) { this.newCustomers = newCustomers; }
        public Long getReturningCustomers() { return returningCustomers; }
        public void setReturningCustomers(Long returningCustomers) { this.returningCustomers = returningCustomers; }
    }
}