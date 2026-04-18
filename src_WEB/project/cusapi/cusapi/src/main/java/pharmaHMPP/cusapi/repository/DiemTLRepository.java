package pharmaHMPP.cusapi.repository;

import pharmaHMPP.cusapi.entity.DiemTL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiemTLRepository extends JpaRepository<DiemTL, String> {
    // findById(maDTL) duoc ke thua tu JpaRepository
    // DIEMTL trong schema database.sql khong co MAKH,
    // quan he duoc quan ly qua KHACHHANG.MADTL
}