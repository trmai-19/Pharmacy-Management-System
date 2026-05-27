package com.pharmacy.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class InvoiceRequest {
    private String makh; 
    @NotBlank(message = "Mã nhân viên lập đơn không được để trống")
    private String manv; 

    @NotNull(message = "Điểm sử dụng không được để null")
    @Min(value = 0, message = "Điểm sử dụng không được âm")
    private Integer diemsudung;

    @NotEmpty(message = "Hóa đơn phải có ít nhất 1 sản phẩm")
    @Valid
    private List<InvoiceItemRequest> items;
}