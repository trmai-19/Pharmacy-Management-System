package pharmaHMPP.cusapi.repository;

import pharmaHMPP.cusapi.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    List<HoaDon> findByMaKHOrderByNgayBanDesc(String maKH);

    @Query(value = "SELECT c.MAHD, c.MALO, l.MASP, p.TENSANPHAM, p.DVT, c.SL, c.DONGIA, c.THANHTIEN, c.GHICHU " +
            "FROM CTHD c " +
            "JOIN LOSANPHAM l ON c.MALO = l.MALO " +
            "JOIN SANPHAM p ON l.MASP = p.MASP " +
            "WHERE c.MAHD = :maHD", nativeQuery = true)
    List<Object[]> findChiTietHoaDon(@Param("maHD") String maHD);
}