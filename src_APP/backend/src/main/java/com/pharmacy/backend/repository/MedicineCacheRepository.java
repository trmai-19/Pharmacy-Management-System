package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.MedicineCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MedicineCacheRepository extends JpaRepository<MedicineCache, Long> {
    Optional<MedicineCache> findByKeyword(String keyword);
}