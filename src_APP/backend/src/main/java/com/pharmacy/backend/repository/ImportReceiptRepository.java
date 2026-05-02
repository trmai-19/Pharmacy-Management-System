package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.ImportReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportReceiptRepository extends JpaRepository<ImportReceipt, String> {
}