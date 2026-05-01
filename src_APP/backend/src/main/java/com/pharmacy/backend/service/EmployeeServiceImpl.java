package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.EmployeeUpdateRequest;
import com.pharmacy.backend.dto.EmployeeResponse;
import com.pharmacy.backend.mapper.EmployeeMapper;
import com.pharmacy.backend.model.Employee;
import com.pharmacy.backend.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeMapper::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponse updateEmployeeProfile(String id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với mã: " + id));
            
        EmployeeMapper.updateEmployeeFromRequest(employee, request);
        
        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với mã: " + id));
        
        employee.setTrangthai("RESIGNED"); 
        employeeRepository.save(employee);
    }
}