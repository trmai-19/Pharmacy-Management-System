package com.pharmacy.backend.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pharmacy.backend.model.Batch;
import com.pharmacy.backend.dto.BatchResponse;

@Repository
public interface BatchRepository extends JpaRepository<Batch, String> {
    List<Batch> findByHsdBetween(Date startDate, Date endDate);
    List<Batch> findByMasp(String masp);

    // THÊM CÂU TRUY VẤN NÀY ĐỂ NỐI BẢNG LẤY GIÁ NHẬP
    @Query("SELECT new com.pharmacy.backend.dto.BatchResponse(b.malo, b.masp, b.ngaysx, b.ngaynhap, b.hsd, b.slsp, b.trangthai, c.gianhap) " +
           "FROM Batch b LEFT JOIN ImportReceiptDetail c ON b.malo = c.malo " +
           "WHERE b.masp = :masp")
    List<BatchResponse> findBatchesWithPriceByMasp(@Param("masp") String masp);

    List<Batch> findBySlspBetween(Integer min, Integer max);

    java.util.List<Batch> findByMaspOrderByHsdAsc(String masp);

    @Query("SELECT new com.pharmacy.backend.dto.BatchResponse(b.malo, b.masp, b.ngaysx, b.ngaynhap, b.hsd, b.slsp, b.trangthai, c.gianhap) " +
           "FROM Batch b LEFT JOIN ImportReceiptDetail c ON b.malo = c.malo " +
           "WHERE b.slsp > 0 AND b.slsp <= 10")
    List<BatchResponse> findLowStockAlerts();

    @Query("SELECT new com.pharmacy.backend.dto.BatchResponse(b.malo, b.masp, b.ngaysx, b.ngaynhap, b.hsd, b.slsp, b.trangthai, c.gianhap) " +
           "FROM Batch b LEFT JOIN ImportReceiptDetail c ON b.malo = c.malo " +
           "WHERE b.hsd >= :today AND b.hsd <= :threeMonthsLater AND b.slsp > 0")
    List<BatchResponse> findExpiringSoonAlerts(@Param("today") Date today, @Param("threeMonthsLater") Date threeMonthsLater);
}