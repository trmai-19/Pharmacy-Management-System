package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    
    @Query("SELECT i FROM Invoice i WHERE i.makh = :makh AND i.ngayban >= :since ORDER BY i.ngayban DESC")
    List<Invoice> findPurchaseHistory(@Param("makh") String makh, @Param("since") LocalDateTime since);

    // 1. Đếm tổng số đơn hàng có lọc theo tất cả Slicers
    @Query(value = "SELECT COUNT(DISTINCT h.MAHD) FROM HOADON h " +
           "JOIN KHACHHANG kh ON h.MAKH = kh.MAKH " +
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
           "       OR (:customerType = 'NewCustomers' AND kh.DIEMTICHLUY < 10))", nativeQuery = true)
    Long countTotalOrders(@Param("year") Integer year, @Param("quarter") Integer quarter, 
                          @Param("productGroup") String productGroup, @Param("customerType") String customerType);

    // 2. Tính tổng doanh thu có lọc theo tất cả Slicers
    @Query(value = "SELECT NVL(SUM(ct.THANHTIEN), 0) FROM HOADON h " +
           "JOIN KHACHHANG kh ON h.MAKH = kh.MAKH " +
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
           "       OR (:customerType = 'NewCustomers' AND kh.DIEMTICHLUY < 10))", nativeQuery = true)
    Long sumTotalRevenue(@Param("year") Integer year, @Param("quarter") Integer quarter, 
                         @Param("productGroup") String productGroup, @Param("customerType") String customerType);

    // 3. Tính tổng chi phí nhập hàng (Spend) thật từ PHIEUNHAP
    @Query(value = "SELECT NVL(SUM(TONGTIEN), 0) FROM PHIEUNHAP " +
           "WHERE (:year = 0 OR EXTRACT(YEAR FROM NGAYNHAP) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(NGAYNHAP, 'Q')) = :quarter)", nativeQuery = true)
    Long sumTotalSpend(@Param("year") Integer year, @Param("quarter") Integer quarter);

    // 4. Tính tổng tiền hoàn trả khách hàng (Loss) thật từ PHIEUTRA_KH
    @Query(value = "SELECT NVL(SUM(TONGTIENHOAN), 0) FROM PHIEUTRA_KH " +
           "WHERE (:year = 0 OR EXTRACT(YEAR FROM NGAYTRA) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(NGAYTRA, 'Q')) = :quarter)", nativeQuery = true)
    Long sumTotalLoss(@Param("year") Integer year, @Param("quarter") Integer quarter);

    // 5. BIỂU ĐỒ ĐƯỜNG REAL DỮ LIỆU ĐỘNG THEO METRIC VÀ SLICERS
    @Query(value = "SELECT TO_CHAR(h.NGAYBAN, 'Mon', 'NLS_DATE_LANGUAGE = English') as label, " +
           "CASE WHEN :metric = 'ORDERS' THEN COUNT(DISTINCT h.MAHD) " +
           "     WHEN :metric = 'REVENUE' THEN SUM(ct.THANHTIEN) " +
           "     WHEN :metric = 'SPEND' THEN NVL(SUM(ct.SL * cp.GIANHAP), 0) " +
           "     WHEN :metric = 'LOSS' THEN NVL(SUM(pt.TONGTIENHOAN), 0) " +
           "     WHEN :metric = 'PROFIT' THEN SUM(ct.THANHTIEN) - NVL(SUM(ct.SL * cp.GIANHAP), 0) - NVL(SUM(pt.TONGTIENHOAN), 0) " +
           "     ELSE 0 END as value " +
           "FROM HOADON h " +
           "JOIN KHACHHANG kh ON h.MAKH = kh.MAKH " +
           "JOIN CTHD ct ON h.MAHD = ct.MAHD " +
           "JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
           "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
           "JOIN DANHMUC d ON sp.MADM = d.MADM " +
           "LEFT JOIN CTPN cp ON ct.MALO = cp.MALO " +
           "LEFT JOIN PHIEUTRA_KH pt ON h.MAHD = pt.MAHD " +
           "WHERE (:year = 0 OR EXTRACT(YEAR FROM h.NGAYBAN) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h.NGAYBAN, 'Q')) = :quarter) " +
           "  AND (:productGroup = 'All Product Groups' OR d.TENDM = :productGroup) " +
           "  AND (:customerType = 'All Customers' " +
           "       OR (:customerType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
           "       OR (:customerType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
           "       OR (:customerType = 'NewCustomers' AND kh.DIEMTICHLUY < 10)) " +
           "GROUP BY TO_CHAR(h.NGAYBAN, 'Mon', 'NLS_DATE_LANGUAGE = English'), EXTRACT(MONTH FROM h.NGAYBAN) " +
           "ORDER BY EXTRACT(MONTH FROM h.NGAYBAN)", nativeQuery = true)
    List<ChartProjection> getTrendStatistics(@Param("year") Integer year, @Param("quarter") Integer quarter, 
                                            @Param("productGroup") String productGroup, @Param("customerType") String customerType,
                                            @Param("metric") String metric);

    // 6. BIỂU ĐỒ CỘT TOP 5 REAL DỮ LIỆU ĐỘNG THEO METRIC VÀ SLICERS
    @Query(value = "SELECT sp.TENSANPHAM as label, " +
           "CASE WHEN :metric = 'ORDERS' THEN COUNT(DISTINCT h.MAHD) " +
           "     WHEN :metric = 'REVENUE' THEN SUM(ct.THANHTIEN) " +
           "     WHEN :metric = 'SPEND' THEN NVL(SUM(ct.SL * cp.GIANHAP), 0) " +
           "     WHEN :metric = 'LOSS' THEN NVL(SUM(pt.TONGTIENHOAN), 0) " +
           "     WHEN :metric = 'PROFIT' THEN SUM(ct.THANHTIEN) - NVL(SUM(ct.SL * cp.GIANHAP), 0) - NVL(SUM(pt.TONGTIENHOAN), 0) " +
           "     ELSE 0 END as value " +
           "FROM CTHD ct " +
           "JOIN HOADON h ON ct.MAHD = h.MAHD " +
           "JOIN KHACHHANG kh ON h.MAKH = kh.MAKH " +
           "JOIN LOSANPHAM l ON ct.MALO = l.MALO " +
           "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
           "JOIN DANHMUC d ON sp.MADM = d.MADM " +
           "LEFT JOIN CTPN cp ON ct.MALO = cp.MALO " +
           "LEFT JOIN PHIEUTRA_KH pt ON h.MAHD = pt.MAHD " +
           "WHERE (:year = 0 OR EXTRACT(YEAR FROM h.NGAYBAN) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(h.NGAYBAN, 'Q')) = :quarter) " +
           "  AND (:productGroup = 'All Product Groups' OR d.TENDM = :productGroup) " +
           "  AND (:customerType = 'All Customers' " +
           "       OR (:customerType = 'VIP' AND kh.DIEMTICHLUY >= 100) " +
           "       OR (:customerType = 'LoyalCustomers' AND kh.DIEMTICHLUY BETWEEN 10 AND 99) " +
           "       OR (:customerType = 'NewCustomers' AND kh.DIEMTICHLUY < 10)) " +
           "GROUP BY sp.TENSANPHAM " +
           "ORDER BY value DESC FETCH FIRST 5 ROWS ONLY", nativeQuery = true)
    List<ChartProjection> getTopProductStatistics(@Param("year") Integer year, @Param("quarter") Integer quarter, 
                                                 @Param("productGroup") String productGroup, @Param("customerType") String customerType,
                                                 @Param("metric") String metric);
}