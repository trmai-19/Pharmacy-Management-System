package pharmaHMPP.cusapi.repository;

import pharmaHMPP.cusapi.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface BatchRepository extends JpaRepository<Batch, String> {
    List<Batch> findByMaspAndTrangthaiAndHsdAfterOrderByHsdAsc(String masp, String trangthai, LocalDate date);
}
