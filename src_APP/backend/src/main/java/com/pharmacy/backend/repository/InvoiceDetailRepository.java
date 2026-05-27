package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.InvoiceDetail;
import com.pharmacy.backend.model.InvoiceDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, InvoiceDetailId> {
       List<InvoiceDetail> findByMahd(String mahd);
}
