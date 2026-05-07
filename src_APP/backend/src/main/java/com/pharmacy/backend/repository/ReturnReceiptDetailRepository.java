package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.ReturnReceiptDetail;
import com.pharmacy.backend.model.ReturnReceiptDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnReceiptDetailRepository extends JpaRepository<ReturnReceiptDetail, ReturnReceiptDetailId> {
}