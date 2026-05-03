package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.DeadStockResponse;
import com.pharmacy.backend.dto.ExpiryTimelineResponse;
import com.pharmacy.backend.dto.ReorderSuggestionResponse;
import com.pharmacy.backend.model.Batch;
import com.pharmacy.backend.model.Product;
import com.pharmacy.backend.model.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public ReorderSuggestionResponse toReorderSuggestionResponse(
            String masp, Product product, String tenDanhMuc, long currentStock,
            long sold30Days, double dailyAvg, int daysOfStock, int suggested, String priority) {

        return ReorderSuggestionResponse.builder()
                .masp(masp)
                .tensanpham(product.getTensanpham())
                .madm(product.getMadm())
                .tenDanhMuc(tenDanhMuc)
                .sltonHienTai((int) currentStock)
                .luongBan30Ngay(sold30Days)
                .luongBanTBNgay(Math.round(dailyAvg * 100.0) / 100.0)
                .soNgayDuHang(daysOfStock)
                .deXuatNhap(suggested)
                .mucDoUuTien(priority)
                .build();
    }

    public DeadStockResponse toDeadStockResponse(
            Warehouse w, Batch batch, String tensanpham, String tenDanhMuc,
            long soNgayKhongBan, String trangThaiHsd) {

        return DeadStockResponse.builder()
                .malo(w.getMalo())
                .masp(batch.getMasp())
                .tensanpham(tensanpham)
                .madm(batch.getMadm())
                .tenDanhMuc(tenDanhMuc)
                .sltonHienTai(w.getSlton())
                .hsd(batch.getHsd())
                .ngaynhap(batch.getNgaynhap())
                .soNgayKhongBan(soNgayKhongBan)
                .trangThaiHsd(trangThaiHsd)
                .build();
    }

    public ExpiryTimelineResponse toExpiryTimelineResponse(String month, int count, int totalQty) {
        return ExpiryTimelineResponse.builder()
                .thang(month)
                .soLo(count)
                .tongSoLuong(totalQty)
                .build();
    }
}