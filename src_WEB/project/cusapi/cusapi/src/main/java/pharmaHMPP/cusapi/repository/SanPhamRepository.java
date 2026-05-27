package pharmaHMPP.cusapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pharmaHMPP.cusapi.entity.SanPham;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, String> {

    // Tìm kiếm theo tên hoặc công dụng (không phân biệt hoa thường)
    List<SanPham> findByTenSanPhamContainingIgnoreCaseOrCongDungContainingIgnoreCase(
            String tenSanPham, String congDung);

    // Tìm sản phẩm cùng danh mục (gợi ý tương tự)
    List<SanPham> findByMaDMAndMaSPNot(String maDM, String maSPExclude);

    // Lấy SP theo danh mục
    List<SanPham> findByMaDM(String maDM);

    // Tìm kiếm đa trường (tên, công dụng, thành phần) — giống App
    @Query("SELECT p FROM SanPham p WHERE " +
           "(:keyword IS NULL OR :keyword = '' " +
           "OR LOWER(p.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.congDung) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.thanhPhan) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<SanPham> searchAll(@Param("keyword") String keyword);

    // Tìm kiếm đa trường + filter theo danh mục
    @Query("SELECT p FROM SanPham p WHERE " +
           "(:maDM IS NULL OR p.maDM = :maDM) AND " +
           "(:keyword IS NULL OR :keyword = '' " +
           "OR LOWER(p.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.congDung) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.thanhPhan) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<SanPham> searchWithCategory(@Param("keyword") String keyword, @Param("maDM") String maDM);
}
