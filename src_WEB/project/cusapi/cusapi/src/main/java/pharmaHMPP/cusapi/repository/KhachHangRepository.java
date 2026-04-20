package pharmaHMPP.cusapi.repository;

import pharmaHMPP.cusapi.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    Optional<KhachHang> findByMaTK(String maTK);
    boolean existsBySdt(String sdt);
}