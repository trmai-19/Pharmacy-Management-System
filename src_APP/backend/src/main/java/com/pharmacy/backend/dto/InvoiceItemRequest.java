

package com.pharmacy.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemRequest {
    @NotBlank(message = "Mã sản phẩm không được để trống")
    private String masp;

    @NotBlank(message = "Mã lô không được để trống")   // ← BẮT BUỘC
    private String malo;    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer sl;

}