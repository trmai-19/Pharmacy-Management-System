package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.ImportReceiptDetail;
import com.pharmacy.backend.model.ImportReceiptDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImportReceiptDetailRepository extends JpaRepository<ImportReceiptDetail, ImportReceiptDetailId> { 
    List<ImportReceiptDetail> findByMapn(String mapn);
}