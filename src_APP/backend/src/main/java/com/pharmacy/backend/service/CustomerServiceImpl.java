package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.CreateCustomerRequest;
import com.pharmacy.backend.dto.CustomerResponse;
import com.pharmacy.backend.mapper.CustomerMapper;
import com.pharmacy.backend.model.Customer;
import com.pharmacy.backend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<CustomerResponse> searchCustomers(String keyword) {
        return customerRepository.searchByTenkhOrSdt(keyword).stream()
                .map(CustomerMapper::toResponse)
                .toList();
    }

    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.findBySdt(request.getSdt()).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã được đăng ký!");
        }

        Customer newCustomer = new Customer();
        
        CustomerMapper.updateCustomerFromRequest(newCustomer, request);

        Customer savedCustomer = customerRepository.save(newCustomer);
        return CustomerMapper.toResponse(savedCustomer);
    }
}