package com.pharmacy.backend.repository;

import com.pharmacy.backend.dto.dto_dashboard.MonthlyRevenueDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository 
public class DashboardRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<MonthlyRevenueDTO> getMonthlyRevenue(int year) {
        String sql = "SELECT EXTRACT(MONTH FROM NGAYBAN) AS THANG, SUM(TIENTHANHTOAN) AS TONG_DOANH_THU " +
                     "FROM HOADON " +
                     "WHERE EXTRACT(YEAR FROM NGAYBAN) = ? " +
                     "GROUP BY EXTRACT(MONTH FROM NGAYBAN) " +
                     "ORDER BY THANG";

        return jdbcTemplate.query(sql, new Object[]{year}, (rs, rowNum) -> 
            new MonthlyRevenueDTO(rs.getInt("THANG"), rs.getDouble("TONG_DOANH_THU"))
        );
    }
}