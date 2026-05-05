package com.pharmacy.backend.repository;

import com.pharmacy.backend.model.InvoiceDetail;
import com.pharmacy.backend.model.InvoiceDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, InvoiceDetailId> {

    /**
     * Dùng cho API Reorder Suggestions:
     * Tính tổng số lượng đã bán theo từng sản phẩm (MASP) trong khoảng thời gian.
     * Trả về: [masp (String), totalSold (Long)]
     */
    @Query("SELECT b.masp, COALESCE(SUM(d.sl), 0) " +
           "FROM InvoiceDetail d, Batch b, Invoice i " +
           "WHERE d.malo = b.malo " +
           "AND d.mahd = i.mahd " +
           "AND i.ngayban >= :since " +
           "AND i.trangthai = 'HOANTAT' " +
           "GROUP BY b.masp")
    List<Object[]> findSalesVelocityByProduct(@Param("since") LocalDateTime since);

    /**
     * Dùng cho API Dead Stock:
     * Trả về danh sách MALO đã có giao dịch bán kể từ ngày :since.
     * Những lô KHÔNG có trong danh sách này = hàng tồn chết.
     */
    @Query("SELECT DISTINCT d.malo " +
           "FROM InvoiceDetail d, Invoice i " +
           "WHERE d.mahd = i.mahd " +
           "AND i.ngayban >= :since " +
           "AND i.trangthai = 'HOANTAT'")
    List<String> findActiveBatchIdsSince(@Param("since") LocalDateTime since);
       List<InvoiceDetail> findByMahd(String mahd);
}
