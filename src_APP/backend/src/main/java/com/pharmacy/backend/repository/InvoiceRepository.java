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

    /**
     * Dùng cho API Staff Performance:
     * Thống kê doanh số theo từng nhân viên trong khoảng thời gian.
     * Trả về: [manv, tennv, soHoaDon (Long), tongDoanhThu (Double), trungBinhHoaDon (Double)]
     */
    @Query("SELECT i.manv, e.tennv, COUNT(i.mahd), SUM(i.tongtien), AVG(i.tongtien) " +
           "FROM Invoice i, Employee e " +
           "WHERE i.manv = e.manv " +
           "AND i.ngayban BETWEEN :from AND :to " +
           "AND i.trangthai = 'HOANTAT' " +
           "GROUP BY i.manv, e.tennv " +
           "ORDER BY SUM(i.tongtien) DESC")
    List<Object[]> findStaffPerformance(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

}