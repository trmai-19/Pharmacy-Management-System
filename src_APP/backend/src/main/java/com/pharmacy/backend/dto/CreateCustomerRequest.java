package com.pharmacy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {
    
    
    @NotBlank(message = "Tên khách hàng không được để trống")
    private String tenkh;
    
    private String gioitinh;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 10, message = "Số điện thoại tối đa 10 ký tự")
    private String sdt;

    private Date ngaysinh;
}