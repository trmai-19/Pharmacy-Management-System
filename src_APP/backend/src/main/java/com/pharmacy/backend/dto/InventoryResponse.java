package com.pharmacy.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private String makho;
    private String malo;
    private Integer slton;
    private String dvsp;
}