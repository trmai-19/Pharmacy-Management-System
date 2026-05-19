package com.pharmacy.dto;

public class MonthlyRevenueDTO {
    private int month;
    private double totalRevenue;

    public int getMonth() { 
        return month; 
    }
    
    public void setMonth(int month) { 
        this.month = month; 
    }
    
    public double getTotalRevenue() { 
        return totalRevenue; 
    }
    
    public void setTotalRevenue(double totalRevenue) { 
        this.totalRevenue = totalRevenue; 
    }
}