package com.pharmacy.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {
    private String mancc;
    private String tenncc;
    private String sdt;
    private String email;
    private String diachi;
}