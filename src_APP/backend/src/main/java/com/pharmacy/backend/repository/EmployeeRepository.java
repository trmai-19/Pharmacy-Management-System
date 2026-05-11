package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findBySdt(String sdt);
    Optional<Employee> findByMatk(String matk);
    @Query("SELECT e FROM Employee e WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(e.tennv) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR e.sdt LIKE CONCAT('%', :keyword, '%'))")
    List<Employee> searchEmployees(@Param("keyword") String keyword);
}