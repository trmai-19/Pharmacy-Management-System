package com.pharmacy.backend.model;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportReceiptDetailId implements Serializable {
    private String mapn;
    private String malo;
}