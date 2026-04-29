package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.CreateCustomerRequest;
import com.pharmacy.backend.dto.CustomerResponse;
import com.pharmacy.backend.model.Customer;
import com.pharmacy.backend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<CustomerResponse> searchCustomers(String keyword) {
        List<Customer> customers = customerRepository.searchByTenkhOrSdt(keyword);
        
        // Map Entity sang DTO
        return customers.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        // Kiểm tra số điện thoại đã tồn tại
        if (customerRepository.findBySdt(request.getSdt()).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã được đăng ký!");
        }

        Customer newCustomer = new Customer();
        // Cắt UUID lấy 8 ký tự ghép với KH để tạo MAKH
        String generatedMaKH = "KH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Gọi đúng các hàm setter do Lombok sinh ra từ file gốc của bạn
        newCustomer.setMakh(generatedMaKH);
        newCustomer.setTenkh(request.getTenkh());
        newCustomer.setGioitinh(request.getGioitinh());
        newCustomer.setSdt(request.getSdt());
        // Không cần setTongdoanhthu(0.0) và setHangtv("THANH VIEN") nữa 
        // vì trong file Customer.java của bạn đã có sẵn giá trị mặc định rồi!

        Customer savedCustomer = customerRepository.save(newCustomer);
        return mapToResponse(savedCustomer);
    }

    // Hàm phụ trợ để map Entity -> DTO
    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .makh(customer.getMakh())
                .tenkh(customer.getTenkh())
                .gioitinh(customer.getGioitinh())
                .sdt(customer.getSdt())
                .tongdoanhthu(customer.getTongdoanhthu()) // Lỗi đã được giải quyết!
                .hangtv(customer.getHangtv())
                .build();
    }
}