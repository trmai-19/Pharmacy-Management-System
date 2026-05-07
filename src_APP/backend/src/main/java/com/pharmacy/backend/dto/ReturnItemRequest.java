package com.pharmacy.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReturnItemRequest { // <-- Đã thêm chữ public
    @NotBlank(message = "Mã lô không được để trống")
    private String malo;

    @NotNull(message = "Số lượng trả không được để trống")
    @Min(value = 1, message = "Số lượng trả phải lớn hơn 0")
    private Integer sl;

    @NotNull(message = "Đơn giá hoàn không được để trống")
    @Min(value = 0, message = "Đơn giá hoàn không được âm")
    private Double dongiahoan;
}