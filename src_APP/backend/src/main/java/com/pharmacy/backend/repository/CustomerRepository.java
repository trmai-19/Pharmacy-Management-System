package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    Optional<Customer> findBySdt(String sdt);
   @Query("SELECT c FROM Customer c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(c.tenkh) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR c.sdt LIKE CONCAT('%', :keyword, '%'))")
    List<Customer> searchByTenkhOrSdt(@Param("keyword") String keyword);
}

