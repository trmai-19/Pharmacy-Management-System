package com.pharmacy.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.model.Employee;
import com.pharmacy.backend.model.Customer;
import com.pharmacy.backend.repository.AccountRepository;
import com.pharmacy.backend.repository.EmployeeRepository;
import com.pharmacy.backend.repository.CustomerRepository;
import com.pharmacy.backend.dto.UpdateProfileRequest;
import com.pharmacy.backend.mapper.EmployeeMapper;
import com.pharmacy.backend.mapper.CustomerMapper;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final AccountRepository accountRepo;
    private final EmployeeRepository employeeRepo;
    private final CustomerRepository customerRepo;

    public ProfileServiceImpl(AccountRepository accountRepo, EmployeeRepository employeeRepo, CustomerRepository customerRepo) {
        this.accountRepo = accountRepo;
        this.employeeRepo = employeeRepo;
        this.customerRepo = customerRepo;
    }

    @Override
    @Transactional
    public void updateProfile(String sdt, UpdateProfileRequest request) {
        Account acc = accountRepo.findBySdt(sdt)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        String vaitro = acc.getVaitro();

        if ("STAFF".equalsIgnoreCase(vaitro)) {
            Employee employee = employeeRepo.findBySdt(sdt)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ nhân viên!"));
                
            EmployeeMapper.updateProfileFromRequest(employee, request);
            employeeRepo.save(employee);
            
        } else if("CUSTOMER".equalsIgnoreCase(vaitro)) {
            Customer customer = customerRepo.findBySdt(sdt)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ khách hàng!"));
                
            CustomerMapper.updateProfileFromRequest(customer, request);
            customerRepo.save(customer);
        }
    }
}