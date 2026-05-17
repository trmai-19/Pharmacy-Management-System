package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, String> {
    Optional<Supplier> findBySdt(String sdt);
    Optional<Supplier> findByEmail(String email);

    @Query("SELECT s FROM Supplier s WHERE LOWER(s.mancc) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.tenncc) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Supplier> searchSuppliers(@Param("keyword") String keyword);
}