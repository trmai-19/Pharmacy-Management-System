package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.SupplierRequest;
import com.pharmacy.backend.dto.SupplierResponse;
import com.pharmacy.backend.mapper.WarehouseMapper;
import com.pharmacy.backend.model.Supplier;
import com.pharmacy.backend.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final WarehouseMapper warehouseMapper;
    @Override
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(warehouseMapper::toSupplierResponse)
                .toList();
    }

    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        
        if (supplierRepository.findBySdt(request.getSdt()).isPresent()) {
            throw new RuntimeException("Số điện thoại nhà cung cấp đã tồn tại!");
        }
        if (supplierRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email nhà cung cấp đã tồn tại!");
        }

        Supplier supplier = new Supplier();
        warehouseMapper.updateSupplierFromRequest(supplier, request);
        
        Supplier savedSupplier = supplierRepository.save(supplier);
        return warehouseMapper.toSupplierResponse(savedSupplier);
    }

    @Override
    public SupplierResponse updateSupplier(String id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với mã: " + id));
            
        warehouseMapper.updateSupplierFromRequest(supplier, request);
        
        Supplier updatedSupplier = supplierRepository.save(supplier);
        return warehouseMapper.toSupplierResponse(updatedSupplier);
    }
}