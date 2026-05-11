package com.pharmacy.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierReturnDetailId implements Serializable {
    private String maptNcc;
    private String malo;
}