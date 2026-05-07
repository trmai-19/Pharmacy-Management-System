package com.pharmacy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReturnReceiptRequest {
    @NotBlank(message = "Mã hóa đơn gốc không được để trống")
    private String mahd;

    @NotBlank(message = "Mã nhân viên thực hiện không được để trống")
    private String manv;

    @NotBlank(message = "Mã khách hàng không được để trống")
    private String makh;

    @NotBlank(message = "Lý do trả hàng không được để trống")
    private String lydotra;
}