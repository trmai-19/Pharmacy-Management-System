package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.SupplierRequest;
import com.pharmacy.backend.dto.SupplierResponse;
import java.util.List;

public interface SupplierService {
    SupplierResponse createSupplier(SupplierRequest request);
    SupplierResponse updateSupplier(String id, SupplierRequest request);
    List<SupplierResponse> searchSuppliers(String keyword);
}