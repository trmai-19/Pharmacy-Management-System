package com.pharmacy.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {
    private String tenncc;
    private String sdt;
    private String email;
    private String diachi;
}