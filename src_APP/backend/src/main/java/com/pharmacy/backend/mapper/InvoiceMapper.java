package com.pharmacy.backend.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import com.pharmacy.backend.dto.InvoiceResponse;
import com.pharmacy.backend.model.Invoice;
import com.pharmacy.backend.model.InvoiceDetail;

@Component
public class InvoiceMapper {
    public static InvoiceResponse toResponse(Invoice entity) {
        if (entity == null) return null;
        return InvoiceResponse.builder()
                .mahd(entity.getMahd())
                .manv(entity.getManv())
                .makh(entity.getMakh())
                .ngayban(entity.getNgayban())
                .tongtien(entity.getTongtien())
                .diemsudung(entity.getDiemsudung()) 
                .tienthanhtoan(entity.getTienthanhtoan())
                .trangthai(entity.getTrangthai())
                .build();
    }
        public static InvoiceResponse toFullResponse(Invoice invoice, List<InvoiceDetail> details) {
            return toResponse(invoice).toBuilder()
            .build();
}
}