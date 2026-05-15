package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.dto_dashboard.MonthlyRevenueDTO;
import com.pharmacy.backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/revenue")
    public List<MonthlyRevenueDTO> getRevenue() {
        return dashboardService.getRevenueForCurrentYear();
    }
}