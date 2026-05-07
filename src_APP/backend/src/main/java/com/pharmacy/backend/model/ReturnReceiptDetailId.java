package com.pharmacy.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnReceiptDetailId implements Serializable {
    private String maptKh;
    private String malo;
}