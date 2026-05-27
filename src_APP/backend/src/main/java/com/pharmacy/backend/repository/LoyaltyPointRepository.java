package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.LoyaltyPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, String> {
    List<LoyaltyPoint> findByCustomerIdOrderByTransactionDateDesc(String customerId);
}