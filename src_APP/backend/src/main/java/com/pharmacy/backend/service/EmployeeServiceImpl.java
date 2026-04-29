package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.EmployeeUpdateRequest;
import com.pharmacy.backend.dto.EmployeeResponse;
import com.pharmacy.backend.model.Employee;
import com.pharmacy.backend.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeResponse> responses = new ArrayList<>();
        
        for (Employee emp : employees) {
            EmployeeResponse response = new EmployeeResponse();
            response.setManv(emp.getManv());
            response.setMatk(emp.getMatk());
            response.setTennv(emp.getTennv());
            response.setGioitinh(emp.getGioitinh());
            response.setNgaysinh(emp.getNgaysinh());
            response.setSdt(emp.getSdt());
            response.setChucvu(emp.getChucvu());
            response.setTrangthai(emp.getTrangthai());
            
            responses.add(response);
        }
        return responses;
    }

    @Override
    public EmployeeResponse updateEmployeeProfile(String id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với mã: " + id));
            
        employee.setTennv(request.getTennv());
        employee.setGioitinh(request.getGioitinh());
        employee.setNgaysinh(request.getNgaysinh());
        employee.setSdt(request.getSdt());
        employee.setChucvu(request.getChucvu());
        employee.setTrangthai(request.getTrangthai());
        
        Employee updatedEmployee = employeeRepository.save(employee);
        
        EmployeeResponse response = new EmployeeResponse();
        response.setManv(updatedEmployee.getManv());
        response.setMatk(updatedEmployee.getMatk());
        response.setTennv(updatedEmployee.getTennv());
        response.setGioitinh(updatedEmployee.getGioitinh());
        response.setNgaysinh(updatedEmployee.getNgaysinh());
        response.setSdt(updatedEmployee.getSdt());
        response.setChucvu(updatedEmployee.getChucvu());
        response.setTrangthai(updatedEmployee.getTrangthai());
        
        return response;
    }

    @Override
    public void deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với mã: " + id));
        
        employee.setTrangthai("RESIGNED"); 
        
        employeeRepository.save(employee);
    }
}