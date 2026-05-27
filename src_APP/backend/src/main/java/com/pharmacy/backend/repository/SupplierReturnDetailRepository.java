package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.SupplierReturnDetail;
import com.pharmacy.backend.model.SupplierReturnDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierReturnDetailRepository extends JpaRepository<SupplierReturnDetail, SupplierReturnDetailId> {
    List<SupplierReturnDetail> findByMaptNcc(String maptNcc);
}