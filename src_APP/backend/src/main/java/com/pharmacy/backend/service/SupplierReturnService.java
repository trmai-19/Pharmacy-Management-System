package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.SupplierReturnRequest;
import com.pharmacy.backend.dto.SupplierReturnResponse;
import java.util.List;

public interface SupplierReturnService {
    SupplierReturnResponse createSupplierReturn(SupplierReturnRequest request);
    List<SupplierReturnResponse> getReturnsByReceiptId(String mapn);
}