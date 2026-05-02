package com.pharmacy.backend.dto;
import lombok.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class EmployeeResponse {
    private String manv;
    private String matk;
    private String tennv;
    private String gioitinh;
    private Date ngaysinh;
    private String sdt;
    private String chucvu;
    private String trangthai;
}
