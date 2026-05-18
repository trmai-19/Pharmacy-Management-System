package com.pharmacy.backend.dto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CategoryResponse {
    private String madm;
    private String tendm;
    private String mota;
    private String trangthai;
}
