package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.EmployeeResponse;
import com.pharmacy.backend.dto.EmployeeUpdateRequest;
import com.pharmacy.backend.dto.UpdateProfileRequest;
import com.pharmacy.backend.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .manv(employee.getManv())
                .matk(employee.getMatk())
                .tennv(employee.getTennv())
                .gioitinh(employee.getGioitinh())
                .ngaysinh(employee.getNgaysinh())
                .sdt(employee.getSdt())
                .chucvu(employee.getChucvu())
                .trangthai(employee.getTrangthai())
                .build();
    }

    public void updateEmployeeFromRequest(Employee employee, EmployeeUpdateRequest request) {
        employee.setTennv(request.getTennv());
        employee.setGioitinh(request.getGioitinh());
        employee.setNgaysinh(request.getNgaysinh());
        employee.setSdt(request.getSdt());
        employee.setChucvu(request.getChucvu());
        employee.setTrangthai(request.getTrangthai());
    }

    public void updateProfileFromRequest(Employee employee, UpdateProfileRequest request) {
        employee.setTennv(request.getHoten());
        employee.setGioitinh(request.getGioitinh());
        employee.setNgaysinh(request.getNgaysinh());
    }
}