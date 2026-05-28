package pharmaHMPP.cusapi.repository;

import pharmaHMPP.cusapi.entity.CTHD;
import pharmaHMPP.cusapi.entity.CTHDId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CTHDRepository extends JpaRepository<CTHD, CTHDId> {
    List<CTHD> findByMaHD(String maHD);
}
