package com.pharmacy.backend.dto.dto_dashboard;

public class MonthlyRevenueDTO {
    private int month;
    private double totalRevenue;

    public MonthlyRevenueDTO(int month, double totalRevenue) {
        this.month = month;
        this.totalRevenue = totalRevenue;
    }
    
    public int getMonth() { return month; }
    public double getTotalRevenue() { return totalRevenue; }
}
