package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, String> {
    Optional<Supplier> findBySdt(String sdt);
    Optional<Supplier> findByEmail(String email);
}