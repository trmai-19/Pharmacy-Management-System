package com.pharmacy.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.model.Employee;
import com.pharmacy.backend.model.Customer;
import com.pharmacy.backend.repository.AccountRepository;
import com.pharmacy.backend.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

import com.pharmacy.backend.repository.CustomerRepository;
import com.pharmacy.backend.dto.ProfileResponse;
import com.pharmacy.backend.dto.UpdateProfileRequest;
import com.pharmacy.backend.mapper.EmployeeMapper;
import com.pharmacy.backend.mapper.CustomerMapper;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final AccountRepository accountRepo;
    private final EmployeeRepository employeeRepo;
    private final CustomerRepository customerRepo;
    private final EmployeeMapper employeeMapper;
    private final CustomerMapper customerMapper;
    @Override
    @Transactional
    public void updateProfile(String sdt, UpdateProfileRequest request) {
        Account acc = accountRepo.findBySdt(sdt)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        String vaitro = acc.getVaitro();

        if ("STAFF".equalsIgnoreCase(vaitro)) {
            Employee employee = employeeRepo.findBySdt(sdt)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ nhân viên!"));
                
            employeeMapper.updateProfileFromRequest(employee, request);
            employeeRepo.save(employee);
            
        } else if("CUSTOMER".equalsIgnoreCase(vaitro)) {
            Customer customer = customerRepo.findBySdt(sdt)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ khách hàng!"));
                
            customerMapper.updateProfileFromRequest(customer, request);
            customerRepo.save(customer);
        }
    }

    @Override
    public ProfileResponse getProfile(String sdt) {
        // 1. Tìm tài khoản để lấy vai trò và ngày tạo (dùng làm ngày vào làm)
        Account acc = accountRepo.findBySdt(sdt)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        String vaitro = acc.getVaitro();
        ProfileResponse.ProfileResponseBuilder builder = ProfileResponse.builder()
                .sdt(sdt)
                .email(acc.getEmail())
                .ngayvaolam(acc.getNgaytao()); // Lấy ngaytao từ Account

        // 2. Nếu là nhân viên thì lấy từ bảng Employee
        if ("STAFF".equalsIgnoreCase(vaitro)) {
            Employee emp = employeeRepo.findBySdt(sdt)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ nhân viên!"));
            
            builder.hoten(emp.getTennv()) // ĐÃ SỬA: getTennv() cho khớp model
                .gioitinh(emp.getGioitinh())
                .ngaysinh(emp.getNgaysinh())
                .vaitro(emp.getChucvu());
        }
        return builder.build();
    }
}