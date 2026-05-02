package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.ImportReceiptDetail;
import com.pharmacy.backend.model.ImportReceiptDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportReceiptDetailRepository extends JpaRepository<ImportReceiptDetail, ImportReceiptDetailId> { 
}