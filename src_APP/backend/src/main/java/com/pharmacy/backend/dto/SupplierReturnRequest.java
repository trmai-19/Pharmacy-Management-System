package com.pharmacy.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class SupplierReturnRequest {
    @NotBlank(message = "Mã phiếu nhập không được để trống")
    private String mapn;

    @NotBlank(message = "Mã nhân viên thực hiện không được để trống")
    private String manv;

    @NotBlank(message = "Lý do trả hàng không được để trống")
    private String lydotra;

    @NotEmpty(message = "Danh sách sản phẩm trả không được rỗng")
    @Valid
    private List<SupplierReturnItemRequest> items;
}