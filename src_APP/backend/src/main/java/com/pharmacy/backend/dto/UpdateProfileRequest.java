package com.pharmacy.backend.dto;

import java.util.Date;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String hoten;
    private String gioitinh;
    private Date ngaysinh;
}