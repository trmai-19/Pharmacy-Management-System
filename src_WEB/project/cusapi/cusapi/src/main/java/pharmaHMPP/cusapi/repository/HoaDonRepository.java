package pharmaHMPP.cusapi.repository;

import pharmaHMPP.cusapi.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    List<HoaDon> findByMaKHOrderByNgayBanDesc(String maKH);
}