package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.SupplierReturnItemResponse;
import com.pharmacy.backend.dto.SupplierReturnResponse;
import com.pharmacy.backend.model.SupplierReturn;
import com.pharmacy.backend.model.SupplierReturnDetail;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SupplierReturnMapper {
    
    public SupplierReturnResponse toResponse(SupplierReturn entity, List<SupplierReturnDetail> details) {
        if (entity == null) return null;

        List<SupplierReturnItemResponse> itemDtos = details.stream().map(d -> 
            SupplierReturnItemResponse.builder()
                .malo(d.getMalo())
                .sl(d.getSl())
                .dongiatra(d.getDongiatra())
                .thanhtien(d.getThanhtien())
                .build()
        ).collect(Collectors.toList());

        return SupplierReturnResponse.builder()
                .maptNcc(entity.getMaptNcc())
                .mapn(entity.getMapn())
                .manv(entity.getManv())
                .ngaytra(entity.getNgaytra())
                .lydotra(entity.getLydotra())
                .tongtien(entity.getTongtien())
                .items(itemDtos)
                .build();
    }
}