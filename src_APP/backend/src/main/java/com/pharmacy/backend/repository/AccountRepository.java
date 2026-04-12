package com.pharmacy.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pharmacy.backend.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    
    // Tìm tài khoản khớp Số điện thoại
    Optional<Account> findBySdt(String sdtString);
}