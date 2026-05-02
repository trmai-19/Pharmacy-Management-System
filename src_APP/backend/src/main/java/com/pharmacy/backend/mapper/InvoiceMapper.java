package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.InvoiceResponse;
import com.pharmacy.backend.model.Invoice;

public class InvoiceMapper {
    public static InvoiceResponse toResponse(Invoice entity) {
        if (entity == null) return null;
        return InvoiceResponse.builder()
                .mahd(entity.getMahd())
                .manv(entity.getManv())
                .ngayban(entity.getNgayban())
                .tongtien(entity.getTongtien())
                .diemsudung(entity.getDiemsudung()) 
                .tienthanhtoan(entity.getTienthanhtoan())
                .trangthai(entity.getTrangthai())
                .build();
    }
}