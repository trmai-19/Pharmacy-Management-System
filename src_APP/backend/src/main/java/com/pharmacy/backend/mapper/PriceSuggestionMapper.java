package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.PriceSuggestionResponse;
import com.pharmacy.backend.model.Product;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PriceSuggestionMapper {

    public PriceSuggestionResponse toPriceSuggestionResponse(
            Product product, String tenDanhMuc, Double giaNhap, Date ngayNhap,
            Double tyleLoiNhuan, Double giaDeXuat, Double chenhLech, String ghiChu) {

        return PriceSuggestionResponse.builder()
                .masp(product.getMasp())
                .tensanpham(product.getTensanpham())
                .madm(product.getMadm())
                .tenDanhMuc(tenDanhMuc)
                .giaNhapGanNhat(giaNhap)
                .ngayNhapGanNhat(ngayNhap)
                .tyleLoiNhuan(tyleLoiNhuan)
                .giaDeXuat(giaDeXuat)
                .giaBanHienTai(product.getGiaban())
                .chenhLech(chenhLech)
                .isManualPrice(product.isManualPrice())
                .ghiChu(ghiChu)
                .build();
    }
}