package com.pharmacy.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.model.Customer;
import com.pharmacy.backend.model.Employee;
import com.pharmacy.backend.repository.AccountRepository;
import com.pharmacy.backend.repository.CustomerRepository;
import com.pharmacy.backend.repository.EmployeeRepository;
import com.pharmacy.backend.dto.UpdateProfileRequest;

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
    public boolean updateProfile(String sdt, UpdateProfileRequest request) {
        Optional<Account> accountOpt = accountRepo.findBySdt(sdt);
        if (accountOpt.isEmpty()) {
            return false;
        }

        Account acc = accountOpt.get();
        String vaitro = acc.getVaitro();

        if ("KHACHHANG".equalsIgnoreCase(vaitro)) {
            Optional<Customer> customerOpt = customerRepo.findBySdt(sdt); 
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                customer.setTenkh(request.getFullName());
                customer.setGioitinh(request.getGender());
                customerRepo.save(customer);
                return true;
            }
        } else {
            Optional<Employee> employeeOpt = employeeRepo.findBySdt(sdt);
            if (employeeOpt.isPresent()) {
                Employee employee = employeeOpt.get();
                employee.setTennv(request.getFullName());
                employee.setGioitinh(request.getGender());
                employeeRepo.save(employee);
                return true;
            }
        }
        
        return false;
    }
}