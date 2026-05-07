package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.ReturnReceiptResponse;
import com.pharmacy.backend.dto.ReturnItemDetailResponse;
import com.pharmacy.backend.model.ReturnReceipt;
import com.pharmacy.backend.model.ReturnReceiptDetail;
import java.util.List;
import java.util.stream.Collectors;

public class ReturnReceiptMapper {
    
    public static ReturnReceiptResponse toResponse(ReturnReceipt entity, List<ReturnReceiptDetail> details) {
        if (entity == null) return null;
        
        // Chuyển đổi danh sách chi tiết thực tế từ DB sang DTO
        List<ReturnItemDetailResponse> itemDtos = details.stream().map(d -> 
            ReturnItemDetailResponse.builder()
                .malo(d.getMalo())
                .sl(d.getSl())
                .dongiahoan(d.getDongiahoan())
                .thanhtien(d.getThanhtien()) // Giá trị này đã được Trigger Oracle tính
                .build()
        ).collect(Collectors.toList());

        return ReturnReceiptResponse.builder()
                .maptKh(entity.getMaptKh())
                .mahd(entity.getMahd())
                .manv(entity.getManv())
                .ngaytra(entity.getNgaytra())
                .lydotra(entity.getLydotra())
                .tongtienhoan(entity.getTongtienhoan())
                .items(itemDtos) // Gán danh sách chi tiết vào response
                .build();
    }
}