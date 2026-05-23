package com.pharmacy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineSuggestionDTO {
    private String tenChuan;
    private String donViTinh;
    private String thanhPhan;
    private String congDung;
}