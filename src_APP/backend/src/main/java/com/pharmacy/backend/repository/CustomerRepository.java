package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    // 1. Tìm kiếm khách hàng theo số điện thoại (Dùng trong quản lý khách hàng/tích điểm)
    Optional<Customer> findBySdt(String sdt);

    // 2. Đếm số lượng khách hàng theo Hạng thành viên (Kim Cương, Vàng, Bạc...)
    long countByHangtv(String hangtv);

    // 3. Tìm kiếm khách hàng theo Tên hoặc Số điện thoại
    @Query("SELECT c FROM Customer c WHERE LOWER(c.tenkh) LIKE LOWER(CONCAT('%', :keyword, '%')) OR c.sdt LIKE CONCAT('%', :keyword, '%')")
    List<Customer> searchByTenkhOrSdt(@Param("keyword") String keyword);

    // 4. Tìm kiếm kết hợp bộ lọc nâng cao theo Hạng thành viên
    @Query("SELECT c FROM Customer c " +
           "WHERE (LOWER(c.tenkh) LIKE LOWER(CONCAT('%', :search, '%')) OR c.sdt LIKE CONCAT('%', :search, '%') OR :search IS NULL OR :search = '') " +
           "AND (:tier = 'All' OR :tier IS NULL OR :tier = '' OR c.hangtv = :tier)")
    List<Customer> searchAndFilterCustomers(@Param("search") String search, @Param("tier") String tier);

    // 5. Thống kê Giới tính co giãn linh hoạt theo Metric và Bộ lọc (Dùng cho Tab PERFORMANCE)
    @Query(value = "SELECT kh.GIOITINH AS label, " +
           "CASE WHEN :metric = 'ORDERS' THEN COUNT(DISTINCT h.MAHD) " +
           "     ELSE NVL(SUM(ct.THANHTIEN), 0) END AS value " +
           "FROM KHACHHANG kh " +
           "JOIN HOADON h ON kh.MAKH = h.MAKH " +
           "JOIN CTHD ct ON h.MAHD = ct.MAHD " +
           "JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
           "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
           "JOIN DANHMUC d ON sp.MADM = d.MADM " +
           "WHERE (:year = 0 OR EXTRACT(YEAR FROM h.NGAYBAN) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h.NGAYBAN, 'Q')) = :quarter) " +
           "  AND (:productGroup = 'All Product Groups' OR d.TENDM = :productGroup) " +
           "  AND (:customerType = 'All Customers' " +
           "       OR (:customerType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
           "       OR (:customerType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
           "       OR (:customerType = 'NewCustomers' AND kh.DIEMTICHLUY < 10)) " +
           "GROUP BY kh.GIOITINH", nativeQuery = true)
    List<ChartProjection> getGenderStatistics(@Param("year") Integer year, @Param("quarter") Integer quarter, 
                                              @Param("productGroup") String productGroup, @Param("customerType") String customerType, 
                                              @Param("metric") String metric);

    // 6. Thống kê Nhóm độ tuổi co giãn linh hoạt theo Metric và Bộ lọc (Dùng cho Tab PERFORMANCE)
    @Query(value = "SELECT " +
           "CASE " +
           "  WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, kh.NGAYSINH)/12) BETWEEN 18 AND 24 THEN '18-24' " +
           "  WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, kh.NGAYSINH)/12) BETWEEN 25 AND 34 THEN '25-34' " +
           "  WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, kh.NGAYSINH)/12) BETWEEN 35 AND 44 THEN '35-44' " +
           "  ELSE '45+' END AS label, " +
           "CASE WHEN :metric = 'ORDERS' THEN COUNT(DISTINCT h.MAHD) " +
           "     ELSE NVL(SUM(ct.THANHTIEN), 0) END AS value " +
           "FROM KHACHHANG kh " +
           "JOIN HOADON h ON kh.MAKH = h.MAKH " +
           "JOIN CTHD ct ON h.MAHD = ct.MAHD " +
           "JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
           "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
           "JOIN DANHMUC d ON sp.MADM = d.MADM " +
           "WHERE kh.NGAYSINH IS NOT NULL " +
           "  AND (:year = 0 OR EXTRACT(YEAR FROM h.NGAYBAN) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h.NGAYBAN, 'Q')) = :quarter) " +
           "  AND (:productGroup = 'All Product Groups' OR d.TENDM = :productGroup) " +
           "  AND (:customerType = 'All Customers' " +
           "       OR (:customerType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
           "       OR (:customerType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
           "       OR (:customerType = 'NewCustomers' AND kh.DIEMTICHLUY < 10)) " +
           "GROUP BY " +
           "CASE " +
           "  WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, kh.NGAYSINH)/12) BETWEEN 18 AND 24 THEN '18-24' " +
           "  WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, kh.NGAYSINH)/12) BETWEEN 25 AND 34 THEN '25-34' " +
           "  WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, kh.NGAYSINH)/12) BETWEEN 35 AND 44 THEN '35-44' " +
           "  ELSE '45+' END", nativeQuery = true)
    List<ChartProjection> getAgeStatistics(@Param("year") Integer year, @Param("quarter") Integer quarter, 
                                           @Param("productGroup") String productGroup, @Param("customerType") String customerType, 
                                           @Param("metric") String metric);


    // =========================================================================================
    // CÁC HÀM CUNG CẤP DỮ LIỆU ĐỘNG CHO TAB CUSTOMER DASHBOARD
    // =========================================================================================

    @Query(value = "SELECT COUNT(DISTINCT K.MAKH) FROM KHACHHANG K " +
                   "JOIN HOADON H ON K.MAKH = H.MAKH " +
                   "WHERE (:year = 0 OR EXTRACT(YEAR FROM H.NGAYBAN) = :year) " +
                   "AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(H.NGAYBAN, 'Q')) = :quarter)", nativeQuery = true)
    Long countTotalCustomers(@Param("year") int year, @Param("quarter") String quarter);

    @Query(value = "SELECT * FROM (SELECT K.TENKH as label, SUM(C.THANHTIEN) as value " +
                   "FROM KHACHHANG K JOIN HOADON H ON K.MAKH = H.MAKH JOIN CTHD C ON H.MAHD = C.MAHD " +
                   "GROUP BY K.TENKH ORDER BY value DESC) WHERE ROWNUM <= 10", nativeQuery = true)
    List<Object[]> getTopSpenders();

    // -- Các hàm KPI đếm số lượng khách --
    @Query(value = "SELECT COUNT(DISTINCT h.MAKH) FROM HOADON h " +
                   "JOIN KHACHHANG kh ON h.MAKH = kh.MAKH " +
                   "JOIN CTHD ct ON h.MAHD = ct.MAHD " +
                   "JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
                   "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
                   "JOIN DANHMUC d ON sp.MADM = d.MADM " +
                   "WHERE (:year = 0 OR EXTRACT(YEAR FROM h.NGAYBAN) = :year) " +
                   "AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h.NGAYBAN, 'Q')) = :quarter) " +
                   "AND (:pGroup = 'All Product Groups' OR d.TENDM = :pGroup) " +
                   "AND (:cType = 'All Customers' OR :cType = 'AllCustomers' " +
                   "  OR (:cType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
                   "  OR (:cType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
                   "  OR (:cType = 'NewCustomers' AND kh.DIEMTICHLUY < 10))", nativeQuery = true)
    Long countActiveCustomers(@Param("year") int year, @Param("quarter") int quarter, @Param("pGroup") String pGroup, @Param("cType") String cType);

    @Query(value = "SELECT COUNT(DISTINCT h1.MAKH) FROM HOADON h1 " +
                   "JOIN KHACHHANG kh ON h1.MAKH = kh.MAKH " +
                   "JOIN CTHD ct ON h1.MAHD = ct.MAHD " +
                   "JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
                   "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
                   "JOIN DANHMUC d ON sp.MADM = d.MADM " +
                   "WHERE (:year = 0 OR EXTRACT(YEAR FROM h1.NGAYBAN) = :year) " +
                   "AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h1.NGAYBAN, 'Q')) = :quarter) " +
                   "AND (:pGroup = 'All Product Groups' OR d.TENDM = :pGroup) " +
                   "AND (:cType = 'All Customers' OR :cType = 'AllCustomers' " +
                   "  OR (:cType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
                   "  OR (:cType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
                   "  OR (:cType = 'NewCustomers' AND kh.DIEMTICHLUY < 10)) " +
                   "AND h1.MAKH IN (SELECT h2.MAKH FROM HOADON h2 WHERE h2.MAHD <> h1.MAHD)", nativeQuery = true)
    Long countReturningCustomers(@Param("year") int year, @Param("quarter") int quarter, @Param("pGroup") String pGroup, @Param("cType") String cType);

    @Query(value = "SELECT COUNT(DISTINCT h.MAKH) FROM HOADON h " +
                   "JOIN KHACHHANG kh ON h.MAKH = kh.MAKH " +
                   "JOIN CTHD ct ON h.MAHD = ct.MAHD " +
                   "JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
                   "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
                   "JOIN DANHMUC d ON sp.MADM = d.MADM " +
                   "WHERE (:year = 0 OR EXTRACT(YEAR FROM h.NGAYBAN) = :year) " +
                   "AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h.NGAYBAN, 'Q')) = :quarter) " +
                   "AND (:pGroup = 'All Product Groups' OR d.TENDM = :pGroup) " +
                   "AND (:cType = 'All Customers' OR :cType = 'AllCustomers' " +
                   "  OR (:cType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
                   "  OR (:cType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
                   "  OR (:cType = 'NewCustomers' AND kh.DIEMTICHLUY < 10)) " +
                   "AND kh.DIEMTICHLUY < 10", nativeQuery = true)
    Long countNewCustomers(@Param("year") int year, @Param("quarter") int quarter, @Param("pGroup") String pGroup, @Param("cType") String cType);

    @Query(value = "SELECT COUNT(DISTINCT h.MAKH) FROM HOADON h " +
                   "JOIN KHACHHANG kh ON h.MAKH = kh.MAKH " +
                   "JOIN CTHD ct ON h.MAHD = ct.MAHD " +
                   "JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
                   "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
                   "JOIN DANHMUC d ON sp.MADM = d.MADM " +
                   "WHERE (:year = 0 OR EXTRACT(YEAR FROM h.NGAYBAN) = :year) " +
                   "AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h.NGAYBAN, 'Q')) = :quarter) " +
                   "AND (:pGroup = 'All Product Groups' OR d.TENDM = :pGroup) " +
                   "AND (:cType = 'All Customers' OR :cType = 'AllCustomers' " +
                   "  OR (:cType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
                   "  OR (:cType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
                   "  OR (:cType = 'NewCustomers' AND kh.DIEMTICHLUY < 10)) " +
                   "AND kh.DIEMTICHLUY >= 100", nativeQuery = true)
    Long countVipCustomers(@Param("year") int year, @Param("quarter") int quarter, @Param("pGroup") String pGroup, @Param("cType") String cType);


    @Query(value = "SELECT COUNT(kh.MAKH) FROM KHACHHANG kh " +
                   "WHERE (:cType = 'All Customers' OR :cType = 'AllCustomers' " +
                   "  OR (:cType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
                   "  OR (:cType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
                   "  OR (:cType = 'NewCustomers' AND kh.DIEMTICHLUY < 10)) " +
                   "AND kh.MAKH NOT IN (" +
                   "  SELECT DISTINCT h.MAKH FROM HOADON h " +
                   "  JOIN CTHD ct ON h.MAHD = ct.MAHD " +
                   "  JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
                   "  JOIN SANPHAM sp ON l.MASP = sp.MASP " +
                   "  JOIN DANHMUC d ON sp.MADM = d.MADM " +
                   "  WHERE (:year = 0 OR EXTRACT(YEAR FROM h.NGAYBAN) = :year) " +
                   "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h.NGAYBAN, 'Q')) = :quarter) " +
                   "  AND (:pGroup = 'All Product Groups' OR d.TENDM = :pGroup)" +
                   ")", nativeQuery = true)
    Long countLostCustomers(@Param("year") int year, @Param("quarter") int quarter, @Param("pGroup") String pGroup, @Param("cType") String cType);

    // -- Biểu đồ Area Chart (Dữ liệu tăng trưởng theo tháng) --
    @Query(value = "SELECT TO_CHAR(h.NGAYBAN, 'Mon') AS period, " +
                   "COUNT(DISTINCT h.MAKH) AS totalCustomers, " +
                   "COUNT(DISTINCT CASE WHEN kh.DIEMTICHLUY < 10 THEN h.MAKH END) AS newCustomers, " +
                   "COUNT(DISTINCT CASE WHEN kh.DIEMTICHLUY >= 10 THEN h.MAKH END) AS returningCustomers " +
                   "FROM HOADON h " +
                   "JOIN KHACHHANG kh ON h.MAKH = kh.MAKH " +
                   "JOIN CTHD ct ON h.MAHD = ct.MAHD " +
                   "JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
                   "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
                   "JOIN DANHMUC d ON sp.MADM = d.MADM " +
                   "WHERE (:year = 0 OR EXTRACT(YEAR FROM h.NGAYBAN) = :year) " +
                   "AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h.NGAYBAN, 'Q')) = :quarter) " +
                   "AND (:pGroup = 'All Product Groups' OR d.TENDM = :pGroup) " +
                   "AND (:cType = 'All Customers' OR :cType = 'AllCustomers' " +
                   "  OR (:cType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
                   "  OR (:cType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
                   "  OR (:cType = 'NewCustomers' AND kh.DIEMTICHLUY < 10)) " +
                   "GROUP BY TO_CHAR(h.NGAYBAN, 'Mon'), EXTRACT(MONTH FROM h.NGAYBAN) " +
                   "ORDER BY EXTRACT(MONTH FROM h.NGAYBAN)", nativeQuery = true)
    List<Object[]> getCustomerGrowth(@Param("year") int year, @Param("quarter") int quarter, @Param("pGroup") String pGroup, @Param("cType") String cType);

    // -- Biểu đồ Bar Chart ngang (Top Spenders) --
    @Query(value = "SELECT * FROM (SELECT kh.TENKH AS label, NVL(SUM(ct.THANHTIEN),0) AS value " +
                   "FROM KHACHHANG kh " +
                   "JOIN HOADON h ON kh.MAKH = h.MAKH " +
                   "JOIN CTHD ct ON h.MAHD = ct.MAHD " +
                   "JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
                   "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
                   "JOIN DANHMUC d ON sp.MADM = d.MADM " +
                   "WHERE (:year = 0 OR EXTRACT(YEAR FROM h.NGAYBAN) = :year) " +
                   "AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h.NGAYBAN, 'Q')) = :quarter) " +
                   "AND (:pGroup = 'All Product Groups' OR d.TENDM = :pGroup) " +
                   "AND (:cType = 'All Customers' OR :cType = 'AllCustomers' " +
                   "  OR (:cType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
                   "  OR (:cType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
                   "  OR (:cType = 'NewCustomers' AND kh.DIEMTICHLUY < 10)) " +
                   "GROUP BY kh.TENKH ORDER BY value DESC) WHERE ROWNUM <= 10", nativeQuery = true)
    List<ChartProjection> getTopSpendersReport(@Param("year") int year, @Param("quarter") int quarter, @Param("pGroup") String pGroup, @Param("cType") String cType);


    // -- Biểu đồ Pie Chart Giới tính (Cho Tab CUSTOMER, có Metric: TOTAL, NEW, RETURNING) --
    @Query(value = "SELECT kh.GIOITINH AS label, COUNT(DISTINCT kh.MAKH) AS value " +
                   "FROM KHACHHANG kh " +
                   "JOIN HOADON h ON kh.MAKH = h.MAKH " +
                   "JOIN CTHD ct ON h.MAHD = ct.MAHD " +
                   "JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
                   "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
                   "JOIN DANHMUC d ON sp.MADM = d.MADM " +
                   "WHERE (:year = 0 OR EXTRACT(YEAR FROM h.NGAYBAN) = :year) " +
                   "AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h.NGAYBAN, 'Q')) = :quarter) " +
                   "AND (:pGroup = 'All Product Groups' OR d.TENDM = :pGroup) " +
                   "AND (:cType = 'All Customers' OR :cType = 'AllCustomers' " +
                   "  OR (:cType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
                   "  OR (:cType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
                   "  OR (:cType = 'NewCustomers' AND kh.DIEMTICHLUY < 10)) " +
                   "AND (:metric = 'TOTAL' " +
                   "  OR (:metric = 'NEW' AND kh.DIEMTICHLUY < 10) " +
                   "  OR (:metric = 'RETURNING' AND kh.DIEMTICHLUY >= 10)) " +
                   "GROUP BY kh.GIOITINH", nativeQuery = true)
    List<ChartProjection> getCustomerGenderStats(@Param("year") int year, @Param("quarter") int quarter, @Param("pGroup") String pGroup, @Param("cType") String cType, @Param("metric") String metric);

    // -- Biểu đồ Pie Chart Độ tuổi (Cho Tab CUSTOMER) --
    @Query(value = "SELECT " +
                   "CASE " +
                   "  WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, kh.NGAYSINH)/12) BETWEEN 18 AND 24 THEN '18-24' " +
                   "  WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, kh.NGAYSINH)/12) BETWEEN 25 AND 34 THEN '25-34' " +
                   "  WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, kh.NGAYSINH)/12) BETWEEN 35 AND 44 THEN '35-44' " +
                   "  ELSE '45+' END AS label, " +
                   "COUNT(DISTINCT kh.MAKH) AS value " +
                   "FROM KHACHHANG kh " +
                   "JOIN HOADON h ON kh.MAKH = h.MAKH " +
                   "JOIN CTHD ct ON h.MAHD = ct.MAHD " +
                   "JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
                   "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
                   "JOIN DANHMUC d ON sp.MADM = d.MADM " +
                   "WHERE kh.NGAYSINH IS NOT NULL " +
                   "AND (:year = 0 OR EXTRACT(YEAR FROM h.NGAYBAN) = :year) " +
                   "AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h.NGAYBAN, 'Q')) = :quarter) " +
                   "AND (:pGroup = 'All Product Groups' OR d.TENDM = :pGroup) " +
                   "AND (:cType = 'All Customers' OR :cType = 'AllCustomers' " +
                   "  OR (:cType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
                   "  OR (:cType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
                   "  OR (:cType = 'NewCustomers' AND kh.DIEMTICHLUY < 10)) " +
                   "AND (:metric = 'TOTAL' " +
                   "  OR (:metric = 'NEW' AND kh.DIEMTICHLUY < 10) " +
                   "  OR (:metric = 'RETURNING' AND kh.DIEMTICHLUY >= 10)) " +
                   "GROUP BY " +
                   "CASE " +
                   "  WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, kh.NGAYSINH)/12) BETWEEN 18 AND 24 THEN '18-24' " +
                   "  WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, kh.NGAYSINH)/12) BETWEEN 25 AND 34 THEN '25-34' " +
                   "  WHEN TRUNC(MONTHS_BETWEEN(SYSDATE, kh.NGAYSINH)/12) BETWEEN 35 AND 44 THEN '35-44' " +
                   "  ELSE '45+' END", nativeQuery = true)
    List<ChartProjection> getCustomerAgeStats(@Param("year") int year, @Param("quarter") int quarter, @Param("pGroup") String pGroup, @Param("cType") String cType, @Param("metric") String metric);
}