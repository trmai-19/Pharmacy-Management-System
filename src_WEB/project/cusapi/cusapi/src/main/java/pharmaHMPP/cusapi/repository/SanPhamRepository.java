package pharmaHMPP.cusapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pharmaHMPP.cusapi.entity.SanPham;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, String> {

    // Tìm kiếm theo tên hoặc công dụng (không phân biệt hoa thường)
    List<SanPham> findByTenSanPhamContainingIgnoreCaseOrCongDungContainingIgnoreCase(
            String tenSanPham, String congDung);

    // Tìm sản phẩm cùng danh mục (gợi ý tương tự)
    List<SanPham> findByMaDMAndMaSPNot(String maDM, String maSPExclude);
}
