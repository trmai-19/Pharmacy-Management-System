package com.pharmacy.backend.dto;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportReceiptRequest {
    private String manv;
    private String mancc;
    private List<ImportItemRequest> items;
}