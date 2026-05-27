package com.pharmacy.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

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

    @NotEmpty(message = "Danh sách sản phẩm trả không được rỗng")
    @Valid
    private List<ReturnItemRequest> items;
}