package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.CreateCustomerRequest;
import com.pharmacy.backend.dto.CustomerResponse;
import com.pharmacy.backend.dto.UpdateProfileRequest;
import com.pharmacy.backend.model.Customer;

public class CustomerMapper {
    public static CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .makh(customer.getMakh())
                .tenkh(customer.getTenkh())
                .gioitinh(customer.getGioitinh())
                .sdt(customer.getSdt())
                .tongdoanhthu(customer.getTongdoanhthu())
                .diemtichluy(customer.getDiemtichluy())
                .hangtv(customer.getHangtv())
                .build();
    }

    public static void updateCustomerFromRequest(Customer customer, CreateCustomerRequest request) {
        customer.setTenkh(request.getTenkh());
        customer.setGioitinh(request.getGioitinh());
        customer.setSdt(request.getSdt());
    }

    public static void updateProfileFromRequest(Customer customer, UpdateProfileRequest request) {
        customer.setTenkh(request.getHoten());
        customer.setGioitinh(request.getGioitinh());
        customer.setNgaysinh(request.getNgaysinh());
    }
}