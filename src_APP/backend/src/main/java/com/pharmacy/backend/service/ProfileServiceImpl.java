package com.pharmacy.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.model.Employee;
import com.pharmacy.backend.repository.AccountRepository;
import com.pharmacy.backend.repository.EmployeeRepository;
import com.pharmacy.backend.dto.UpdateProfileRequest;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final AccountRepository accountRepo;
    private final EmployeeRepository employeeRepo;

    public ProfileServiceImpl(AccountRepository accountRepo, EmployeeRepository employeeRepo) {
        this.accountRepo = accountRepo;
        this.employeeRepo = employeeRepo;
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

        if ("STAFF".equalsIgnoreCase(vaitro)) {

            Optional<Employee> employeeOpt = employeeRepo.findBySdt(sdt);
            if (employeeOpt.isPresent()) {
                Employee employee = employeeOpt.get();
                employee.setTennv(request.getHoten());
                employee.setGioitinh(request.getGioitinh());
                employee.setNgaysinh(request.getNgaysinh());
                employeeRepo.save(employee);
                return true;
            }
        }
        
        return false;
    }
}