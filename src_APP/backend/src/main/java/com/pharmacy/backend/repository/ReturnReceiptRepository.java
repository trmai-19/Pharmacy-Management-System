package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.ReturnReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReturnReceiptRepository extends JpaRepository<ReturnReceipt, String> {
    boolean existsByMahd(String mahd);

    @Query("SELECT r FROM ReturnReceipt r WHERE " +
           "(:search IS NULL OR :search = '' " +
           "OR LOWER(r.maptKh) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(r.mahd) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<ReturnReceipt> searchReturnReceipts(@Param("search") String search);
}