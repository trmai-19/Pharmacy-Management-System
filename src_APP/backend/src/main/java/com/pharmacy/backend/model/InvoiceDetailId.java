package com.pharmacy.backend.model;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetailId implements Serializable {
    private String mahd;
    private String malo;
}