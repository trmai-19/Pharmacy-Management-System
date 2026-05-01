package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.CreateCustomerRequest;
import com.pharmacy.backend.dto.CustomerResponse;
import java.util.List;
public interface CustomerService {
    // Tìm 1 khách hàng bằng SĐT
    List<CustomerResponse> searchCustomers(String keyword);
    CustomerResponse createCustomer(CreateCustomerRequest request);
}