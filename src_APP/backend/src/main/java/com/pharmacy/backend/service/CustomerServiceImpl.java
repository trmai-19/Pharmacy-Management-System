package com.pharmacy.backend.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pharmacy.backend.dto.CreateCustomerRequest;
import com.pharmacy.backend.dto.CustomerResponse;
import com.pharmacy.backend.dto.InvoiceResponse;
import com.pharmacy.backend.dto.QuickCreateCustomerRequest;
import com.pharmacy.backend.dto.UpgradeAccountRequest;
import com.pharmacy.backend.mapper.CustomerMapper;
import com.pharmacy.backend.mapper.InvoiceMapper;
import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.model.Customer;
import com.pharmacy.backend.repository.AccountRepository;
import com.pharmacy.backend.repository.CustomerRepository;
import com.pharmacy.backend.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository; 
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final InvoiceRepository invoiceRepository;
    private final CustomerMapper customerMapper;
    /* Tạo hồ sơ KH chỉ dùng sdt */
    @Override
    @Transactional
    public CustomerResponse quickCreate(QuickCreateCustomerRequest request) {
        if (customerRepository.findBySdt(request.getSdt()).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã được đăng ký tích điểm!"); 
        }

        Customer customer = new Customer();
        customer.setSdt(request.getSdt());
        customer.setTenkh(request.getTenkh() != null && !request.getTenkh().isEmpty() 
            ? request.getTenkh() : "Khách tích điểm SĐT");
        customer.setTongdoanhthu(0.0);
        customer.setGioitinh(request.getGioitinh());
        customer.setHangtv("THANH VIEN");

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional
    public void upgradeToAccount(String makh, UpgradeAccountRequest request) {
        Customer customer = customerRepository.findById(makh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ khách hàng!"));

        if (customer.getMatk() != null) {
            throw new RuntimeException("Khách hàng đã có tài khoản!");
        }

        String rawPassword = UUID.randomUUID().toString().substring(0, 8);

        Account account = new Account();
        account.setSdt(customer.getSdt());
        account.setPassword(passwordEncoder.encode(rawPassword)); 
        account.setVaitro("CUSTOMER");
        account.setEmail(request.getEmail());
        account.setTrangthai("ACTIVE");
        account.setNgaytao(new Date());
        accountRepository.save(account);

        customer.setMatk(account.getMatk());
        customerRepository.save(customer);

        emailService.sendAccountCreationEmail(request.getEmail(), customer.getSdt(), rawPassword);
    }

    @Override
    public CustomerResponse findBySdt(String sdt) {
        Customer customer = customerRepository.findBySdt(sdt)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với SĐT: " + sdt));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.findBySdt(request.getSdt()).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã được đăng ký!");
        }

        Customer newCustomer = new Customer();
        customerMapper.updateCustomerFromRequest(newCustomer, request);
        return customerMapper.toResponse(customerRepository.save(newCustomer));
    }

    @Override
    public List<InvoiceResponse> getPurchaseHistory(String makh) {
        if (!customerRepository.existsById(makh)) {
            throw new RuntimeException("Không tìm thấy khách hàng với mã: " + makh);
        }
        
        LocalDateTime twoYearsAgo = LocalDateTime.now().minusYears(2);
        
        return invoiceRepository.findPurchaseHistory(makh, twoYearsAgo)
                .stream()
                .map(InvoiceMapper::toResponse)
                .collect(Collectors.toList());
    }
}