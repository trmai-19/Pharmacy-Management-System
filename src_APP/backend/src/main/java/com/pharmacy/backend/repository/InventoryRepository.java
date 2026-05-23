package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Batch, String> {

    // 1. Lấy danh mục thuốc thật từ DB đổ vào ComboBox
    @Query(value = "SELECT TENDM FROM DANHMUC WHERE TRANGTHAI = 'ACTIVE'", nativeQuery = true)
    List<String> findAllActiveCategories();

    // 2. Tính tổng giá trị tồn kho (Số lượng tồn * Giá nhập trong chi tiết phiếu nhập)
    @Query(value = "SELECT NVL(SUM(l.SLSP * cp.GIANHAP), 0) FROM LOSANPHAM l " +
           "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
           "JOIN DANHMUC d ON sp.MADM = d.MADM " +
           "LEFT JOIN CTPN cp ON l.MALO = cp.MALO " +
           "WHERE (:year = 0 OR EXTRACT(YEAR FROM l.NGAYNHAP) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(l.NGAYNHAP, 'Q')) = :quarter) " +
           "  AND (:productGroup = 'All Product Groups' OR d.TENDM = :productGroup)", nativeQuery = true)
    Double getInventoryValue(@Param("year") Integer year, @Param("quarter") Integer quarter, @Param("productGroup") String productGroup);

    // 3. Đếm số lượng đầu thuốc độc nhất (Total Medicines)
    @Query(value = "SELECT COUNT(DISTINCT l.MASP) FROM LOSANPHAM l " +
           "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
           "JOIN DANHMUC d ON sp.MADM = d.MADM " +
           "WHERE (:year = 0 OR EXTRACT(YEAR FROM l.NGAYNHAP) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(l.NGAYNHAP, 'Q')) = :quarter) " +
           "  AND (:productGroup = 'All Product Groups' OR d.TENDM = :productGroup)", nativeQuery = true)
    Long countTotalMedicines(@Param("year") Integer year, @Param("quarter") Integer quarter, @Param("productGroup") String productGroup);

    // 4. Đếm số lô sắp hết hàng (Số lượng tồn dưới 20 và lớn hơn 0)
    @Query(value = "SELECT COUNT(l.MALO) FROM LOSANPHAM l " +
           "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
           "JOIN DANHMUC d ON sp.MADM = d.MADM " +
           "WHERE l.SLSP > 0 AND l.SLSP < 20 " +
           "  AND (:year = 0 OR EXTRACT(YEAR FROM l.NGAYNHAP) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(l.NGAYNHAP, 'Q')) = :quarter) " +
           "  AND (:productGroup = 'All Product Groups' OR d.TENDM = :productGroup)", nativeQuery = true)
    Long countLowStock(@Param("year") Integer year, @Param("quarter") Integer quarter, @Param("productGroup") String productGroup);

    // 5. Đếm số lô cận hạn sử dụng (Còn hạn nhưng sẽ hết hạn trong vòng 3 tháng tới)
    @Query(value = "SELECT COUNT(l.MALO) FROM LOSANPHAM l " +
           "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
           "JOIN DANHMUC d ON sp.MADM = d.MADM " +
           "WHERE l.HSD >= SYSDATE AND l.HSD <= ADD_MONTHS(SYSDATE, 3) " +
           "  AND (:year = 0 OR EXTRACT(YEAR FROM l.NGAYNHAP) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(l.NGAYNHAP, 'Q')) = :quarter) " +
           "  AND (:productGroup = 'All Product Groups' OR d.TENDM = :productGroup)", nativeQuery = true)
    Long countNearExpiry(@Param("year") Integer year, @Param("quarter") Integer quarter, @Param("productGroup") String productGroup);

    // 6. Đếm số lô đã quá hạn sử dụng (HSD < SYSDATE)
    @Query(value = "SELECT COUNT(l.MALO) FROM LOSANPHAM l " +
           "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
           "JOIN DANHMUC d ON sp.MADM = d.MADM " +
           "WHERE l.HSD < SYSDATE " +
           "  AND (:year = 0 OR EXTRACT(YEAR FROM l.NGAYNHAP) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(l.NGAYNHAP, 'Q')) = :quarter) " +
           "  AND (:productGroup = 'All Product Groups' OR d.TENDM = :productGroup)", nativeQuery = true)
    Long countExpired(@Param("year") Integer year, @Param("quarter") Integer quarter, @Param("productGroup") String productGroup);

    // 7. Truy vấn lấy danh sách bảng tồn kho thật hiển thị lên TableView
    @Query(value = "SELECT sp.MASP, sp.TENSANPHAM, d.TENDM, l.SLSP, TO_CHAR(l.HSD, 'DD/MM/YYYY'), " +
           "CASE WHEN l.HSD < SYSDATE THEN 'Hết hạn' " +
           "     WHEN l.HSD <= ADD_MONTHS(SYSDATE, 3) THEN 'Cận hạn sử dụng' " +
           "     WHEN l.SLSP = 0 THEN 'Hết hàng' " +
           "     WHEN l.SLSP < 20 THEN 'Sắp hết hàng' " +
           "     ELSE 'Bình thường' END as status " +
           "FROM LOSANPHAM l " +
           "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
           "JOIN DANHMUC d ON sp.MADM = d.MADM " +
           "WHERE (:year = 0 OR EXTRACT(YEAR FROM l.NGAYNHAP) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(l.NGAYNHAP, 'Q')) = :quarter) " +
           "  AND (:productGroup = 'All Product Groups' OR d.TENDM = :productGroup) " +
           "ORDER BY l.HSD ASC", nativeQuery = true)
    List<Object[]> getInventoryTableData(@Param("year") Integer year, @Param("quarter") Integer quarter, @Param("productGroup") String productGroup);

    // 8. Số liệu biểu đồ tròn: Phân bổ số lượng tồn kho theo Danh mục thuốc
    @Query(value = "SELECT d.TENDM as label, SUM(l.SLSP) as value FROM LOSANPHAM l " +
           "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
           "JOIN DANHMUC d ON sp.MADM = d.MADM " +
           "WHERE (:year = 0 OR EXTRACT(YEAR FROM l.NGAYNHAP) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(l.NGAYNHAP, 'Q')) = :quarter) " +
           "  AND (:productGroup = 'All Product Groups' OR d.TENDM = :productGroup) " +
           "GROUP BY d.TENDM", nativeQuery = true)
    List<ChartProjection> getCategoryDistribution(@Param("year") Integer year, @Param("quarter") Integer quarter, @Param("productGroup") String productGroup);

    // 9. Số liệu biểu đồ cột: Top 5 sản phẩm ứ đọng hàng nhiều nhất (Tồn lớn nhất)
    @Query(value = "SELECT sp.TENSANPHAM as label, SUM(l.SLSP) as value FROM LOSANPHAM l " +
           "JOIN SANPHAM sp ON l.MASP = sp.MASP " +
           "JOIN DANHMUC d ON sp.MADM = d.MADM " +
           "WHERE (:year = 0 OR EXTRACT(YEAR FROM l.NGAYNHAP) = :year) " +
           "  AND (:quarter = 0 OR TO_NUMBER(TO_CHAR(l.NGAYNHAP, 'Q')) = :quarter) " +
           "  AND (:productGroup = 'All Product Groups' OR d.TENDM = :productGroup) " +
           "GROUP BY sp.TENSANPHAM " +
           "ORDER BY value DESC FETCH FIRST 5 ROWS ONLY", nativeQuery = true)
    List<ChartProjection> getSlowestMovingProducts(@Param("year") Integer year, @Param("quarter") Integer quarter, @Param("productGroup") String productGroup);
}