package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {
    List<Warehouse> findBySltonLessThan(Integer threshold);

    /**
     * Dùng cho API Reorder Suggestions:
     * Tính tổng tồn kho hiện tại theo từng sản phẩm (MASP).
     * Trả về: [masp (String), totalStock (Long)]
     */
    @Query("SELECT b.masp, SUM(w.slton) " +
           "FROM Warehouse w, Batch b " +
           "WHERE w.malo = b.malo " +
           "GROUP BY b.masp")
    List<Object[]> findStockByProduct();

}