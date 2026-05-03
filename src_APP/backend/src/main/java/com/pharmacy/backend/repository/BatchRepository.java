package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, String> {
    List<Batch> findByHsdBetween(Date startDate, Date endDate);

    /**
     * Dùng cho API Expiry Timeline:
     * Lấy các lô còn hàng (slsp > 0) sắp hết hạn trong khoảng thời gian, sắp theo HSD tăng dần.
     */
    @Query("SELECT b FROM Batch b WHERE b.hsd BETWEEN :from AND :to AND b.slsp > 0 ORDER BY b.hsd ASC")
    List<Batch> findExpiringBatchesWithStock(@Param("from") Date from, @Param("to") Date to);
}