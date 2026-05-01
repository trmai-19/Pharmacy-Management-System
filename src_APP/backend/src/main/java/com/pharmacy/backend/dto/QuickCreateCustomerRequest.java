package com.pharmacy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuickCreateCustomerRequest {
    @NotBlank(message = "Tên khách hàng không được để trống")
    private String tenkh;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 10, message = "SĐT tối đa 10 số")
    private String sdt;

    private String gioitinh;
}