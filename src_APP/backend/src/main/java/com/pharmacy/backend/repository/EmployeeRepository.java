package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findBySdt(String sdt);
    Optional<Employee> findByMatk(String matk);
}