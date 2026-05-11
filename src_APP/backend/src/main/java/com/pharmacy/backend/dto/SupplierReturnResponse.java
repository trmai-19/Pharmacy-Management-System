package com.pharmacy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierReturnResponse {
    private String maptNcc;
    private String mapn;
    private String manv;
    private Date ngaytra;
    private String lydotra;
    private Double tongtien;
    private List<SupplierReturnItemResponse> items;
}