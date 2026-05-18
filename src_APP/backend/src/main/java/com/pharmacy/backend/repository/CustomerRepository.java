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

    long countByHangtvIn(List<String> hangtv);

    long countByHangtv(String hangtv);

    @Query("SELECT c FROM Customer c WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(c.tenkh) LIKE LOWER(CONCAT('%', :search, '%')) OR c.sdt LIKE CONCAT('%', :search, '%')) " +
           "AND (:tier IS NULL OR :tier = '' OR :tier = 'Tất cả hạng mức' OR c.hangtv = :tier)")
    List<Customer> searchAndFilterCustomers(@Param("search") String search, @Param("tier") String tier);
}

