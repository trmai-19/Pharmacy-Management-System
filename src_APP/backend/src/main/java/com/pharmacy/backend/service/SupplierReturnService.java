package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.SupplierReturnRequest;
import com.pharmacy.backend.dto.SupplierReturnResponse;

public interface SupplierReturnService {
    SupplierReturnResponse createSupplierReturn(SupplierReturnRequest request);
}