package com.pharmacy.backend.dto;

import java.util.Date;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportItemRequest {
    private String masp;
    private String makho;
    private Integer sl;
    private Double gianhap;
    private String dvt;
    private String ghichu;
    private Date ngaysx;
    private Date hsd;
}