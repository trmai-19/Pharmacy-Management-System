package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    
    @Query("SELECT i FROM Invoice i WHERE i.makh = :makh AND i.ngayban >= :since ORDER BY i.ngayban DESC")
    List<Invoice> findPurchaseHistory(@Param("makh") String makh, @Param("since") LocalDateTime since);

}