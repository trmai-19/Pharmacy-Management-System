package pharmaHMPP.cusapi.repository;

import pharmaHMPP.cusapi.entity.DiemTL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiemTLRepository extends JpaRepository<DiemTL, String> {
    // Lấy lịch sử điểm theo mã khách hàng
    List<DiemTL> findByMaKH(String maKH);
}