package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.dto_dashboard.MonthlyRevenueDTO;
import com.pharmacy.backend.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service 
public class DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    public List<MonthlyRevenueDTO> getRevenueForCurrentYear() {
        int currentYear = LocalDate.now().getYear(); 
        
        List<MonthlyRevenueDTO> data = dashboardRepository.getMonthlyRevenue(currentYear);
        
        return data; 
    }
}