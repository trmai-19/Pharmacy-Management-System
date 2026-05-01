package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.*;
import com.pharmacy.backend.model.*;

public class WarehouseMapper {

    public static SupplierResponse toSupplierResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .mancc(supplier.getMancc())
                .tenncc(supplier.getTenncc())
                .sdt(supplier.getSdt())
                .email(supplier.getEmail())
                .diachi(supplier.getDiachi())
                .build();
    }

    public static void updateSupplierFromRequest(Supplier supplier, SupplierRequest request) {
        supplier.setTenncc(request.getTenncc());
        supplier.setSdt(request.getSdt());
        supplier.setEmail(request.getEmail());
        supplier.setDiachi(request.getDiachi());
    }

    public static BatchResponse toBatchResponse(Batch batch) {
        return BatchResponse.builder()
                .malo(batch.getMalo())
                .masp(batch.getMasp())
                .madm(batch.getMadm())
                .ngaysx(batch.getNgaysx())
                .ngaynhap(batch.getNgaynhap())
                .hsd(batch.getHsd())
                .slsp(batch.getSlsp())
                .trangthai(batch.getTrangthai())
                .build();
    }

    public static InventoryResponse toInventoryResponse(Warehouse warehouse) {
        return InventoryResponse.builder()
                .makho(warehouse.getMakho())
                .malo(warehouse.getMalo())
                .slton(warehouse.getSlton())
                .dvsp(warehouse.getDvsp())
                .build();
    }

    public static ImportReceiptResponse toImportReceiptResponse(ImportReceipt receipt) {
        return ImportReceiptResponse.builder()
                .mapn(receipt.getMapn())
                .manv(receipt.getManv())
                .mancc(receipt.getMancc())
                .ngaynhap(receipt.getNgaynhap())
                .tongtien(receipt.getTongtien())
                .trangthai(receipt.getTrangthai())
                .build();
    }
}