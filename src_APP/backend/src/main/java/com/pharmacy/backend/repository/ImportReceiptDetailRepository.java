package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.ImportReceiptDetail;
import com.pharmacy.backend.model.ImportReceiptDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ImportReceiptDetailRepository extends JpaRepository<ImportReceiptDetail, ImportReceiptDetailId> { 
    /**
     * Dùng cho API Price Suggestion:
     * Lấy lịch sử nhập hàng của 1 sản phẩm từ các phiếu đã hoàn tất,
     * sắp xếp theo ngày nhập mới nhất lên đầu.
     * Trả về danh sách để lấy phần tử đầu tiên (giá nhập gần nhất).
     */
    @Query("SELECT d FROM ImportReceiptDetail d, Batch b, ImportReceipt r " +
           "WHERE d.malo = b.malo " +
           "AND d.mapn = r.mapn " +
           "AND b.masp = :masp " +
           "AND r.trangthai = 'HOANTAT' " +
           "ORDER BY r.ngaynhap DESC")
    List<ImportReceiptDetail> findLatestByProductId(@Param("masp") String masp);

}