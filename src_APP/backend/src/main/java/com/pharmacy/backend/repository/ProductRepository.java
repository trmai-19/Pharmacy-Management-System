package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR :keyword = '' " +
           "OR LOWER(p.tensanpham) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.congdung) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.thanhphan) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> searchProducts(@Param("keyword") String keyword);

    boolean existsByTensanphamIgnoreCase(String tensanpham);
}