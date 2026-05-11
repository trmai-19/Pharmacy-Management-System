package com.pharmacy.backend.dto;

import java.util.Date;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchResponse {
    private String malo;
    private String masp;
    private Date ngaysx;
    private Date ngaynhap;
    private Date hsd;
    private Integer slsp;
    private String trangthai;
}