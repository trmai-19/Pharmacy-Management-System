package pharmaHMPP.cusapi.repository;

import pharmaHMPP.cusapi.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {
    Optional<TaiKhoan> findBySdt(String sdt);
    boolean existsBySdt(String sdt);
    Optional<TaiKhoan> findBySdtAndEmail(String sdt, String email);
}