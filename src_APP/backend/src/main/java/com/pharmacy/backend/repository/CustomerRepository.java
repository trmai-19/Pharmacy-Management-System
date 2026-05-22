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

    // 5. Thống kê Giới tính co giãn linh hoạt theo Metric và Bộ lọc (Dùng cho Tab Báo cáo)
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

    // 6. Thống kê Nhóm độ tuổi co giãn linh hoạt theo Metric và Bộ lọc (Dùng cho Tab Báo cáo)
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


       // 1. Đếm tổng khách hàng theo bộ lọc năm/quý (sử dụng JOIN với bảng HOADON)
    @Query(value = "SELECT COUNT(DISTINCT K.MAKH) FROM KHACHHANG K " +
                   "JOIN HOADON H ON K.MAKH = H.MAKH " +
                   "WHERE (:year = 0 OR EXTRACT(YEAR FROM H.NGAYBAN) = :year) " +
                   "AND (:quarter = 0 OR TO_CHAR(H.NGAYBAN, 'Q') = :quarter)", nativeQuery = true)
    Long countTotalCustomers(@Param("year") int year, @Param("quarter") String quarter);

    // 2. Lấy tổng doanh thu của top 10 khách hàng (Cho biểu đồ Bar ngang)
    @Query(value = "SELECT * FROM (SELECT K.TENKH as label, SUM(C.THANHTIEN) as value " +
                   "FROM KHACHHANG K JOIN HOADON H ON K.MAKH = H.MAKH JOIN CTHD C ON H.MAHD = C.MAHD " +
                   "GROUP BY K.TENKH ORDER BY value DESC) WHERE ROWNUM <= 10", nativeQuery = true)
    List<Object[]> getTopSpenders();
}