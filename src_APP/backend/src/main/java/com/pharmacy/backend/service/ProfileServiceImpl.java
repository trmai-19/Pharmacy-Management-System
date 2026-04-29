package com.pharmacy.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void updateProfile(String sdt, UpdateProfileRequest request) {
        Account acc = accountRepo.findBySdt(sdt)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        String vaitro = acc.getVaitro();

        if (!"STAFF".equalsIgnoreCase(vaitro)) {
            throw new RuntimeException("Loại tài khoản này không được phép cập nhật hồ sơ!");
        }

        Employee employee = employeeRepo.findBySdt(sdt)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ nhân viên!"));
            
        employee.setTennv(request.getHoten());
        employee.setGioitinh(request.getGioitinh());
        employee.setNgaysinh(request.getNgaysinh());
        employeeRepo.save(employee);
    }
}