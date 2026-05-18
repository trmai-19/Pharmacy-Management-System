package com.pharmacy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchSalesResponse {
    private String malo;
    private Date nsx; 
    private Date hsd;
    private Integer sl;
    private String trangthai;
}