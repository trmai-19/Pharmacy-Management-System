package com.pharmacy.backend.dto;

import java.util.Date;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private String sdt;
    private String hoten;
    private String vaitro;
    private String gioitinh;
    private Date ngaysinh;
    private Date ngayvaolam;
    private String email;
}