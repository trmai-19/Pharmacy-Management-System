package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.SupplierReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierReturnRepository extends JpaRepository<SupplierReturn, String> {
    boolean existsByMapn(String mapn);

    List<SupplierReturn> findByMapn(String mapn);
}