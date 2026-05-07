package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.ReturnReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnReceiptRepository extends JpaRepository<ReturnReceipt, String> {
    boolean existsByMahd(String mahd);
}