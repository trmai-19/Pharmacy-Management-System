package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.CreateCustomerRequest;
import com.pharmacy.backend.dto.CustomerResponse;
import com.pharmacy.backend.dto.CustomerStatsResponse;
import com.pharmacy.backend.dto.InvoiceResponse;
import com.pharmacy.backend.dto.UpgradeAccountRequest;
import com.pharmacy.backend.service.CustomerService;
import com.pharmacy.backend.dto.QuickCreateCustomerRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/sales/customers") 
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> searchCustomers(
            @RequestParam(required = false) String keyword) {
        List<CustomerResponse> data = customerService.searchCustomers(keyword);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách khách hàng thành công", data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> findBySdt(@RequestParam String sdt) {
        CustomerResponse data = customerService.findBySdt(sdt);
        
        return ResponseEntity.ok(new ApiResponse<>(200, "Tìm thấy thông tin khách hàng", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse data = customerService.createCustomer(request);
        
        return ResponseEntity.ok(new ApiResponse<>(200, "Tạo hồ sơ khách hàng thành công", data));
    }

    @PostMapping("/quick-create")
    public ResponseEntity<ApiResponse<CustomerResponse>> quickCreate(@Valid @RequestBody QuickCreateCustomerRequest request) {
        CustomerResponse data = customerService.quickCreate(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đã tạo hồ sơ tích điểm thành công", data));
    }

    @PostMapping("/{makh}/upgrade")
    public ResponseEntity<ApiResponse<Void>> upgrade(@PathVariable String makh, @Valid @RequestBody UpgradeAccountRequest request) {
        customerService.upgradeToAccount(makh, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cấp tài khoản thành công! Mật khẩu đã gửi về email khách hàng", null));
    }

    @GetMapping("/{makh}/invoices")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getHistory(@PathVariable String makh) {
        List<InvoiceResponse> data = customerService.getPurchaseHistory(makh);
        
        ApiResponse<List<InvoiceResponse>> response = ApiResponse.<List<InvoiceResponse>>builder()
                .status(200)
                .message("Lấy lịch sử mua hàng thành công")
                .data(data)
                .build();
                
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<CustomerStatsResponse>> getStats() {
        CustomerStatsResponse data = customerService.getCustomerStats();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy số liệu thống kê thành công", data));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tier) {
        List<CustomerResponse> data = customerService.getCustomerList(search, tier);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách khách hàng thành công", data));
    }

}