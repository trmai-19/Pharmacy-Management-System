package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.CreateCustomerRequest;
import com.pharmacy.backend.dto.CustomerResponse;
import com.pharmacy.backend.service.CustomerService;
import jakarta.validation.Valid; // Thư viện cho @Valid
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales/customers") // Đúng Prefix [cite: 45]
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> searchCustomers(
            @RequestParam(value = "keyword", required = false) String keyword) {
        
        List<CustomerResponse> data = customerService.searchCustomers(keyword);

        // Bọc dữ liệu vào ApiResponse theo đúng quy tắc [cite: 19, 32]
        ApiResponse<List<CustomerResponse>> response = ApiResponse.<List<CustomerResponse>>builder()
                .status(200) 
                .message("Tra cứu danh sách khách hàng thành công")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) { // Sử dụng @Valid để lọc dữ liệu rác [cite: 111, 447]
        
        CustomerResponse data = customerService.createCustomer(request);

        ApiResponse<CustomerResponse> response = ApiResponse.<CustomerResponse>builder()
                .status(200)
                .message("Tạo hồ sơ khách hàng mới thành công")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}