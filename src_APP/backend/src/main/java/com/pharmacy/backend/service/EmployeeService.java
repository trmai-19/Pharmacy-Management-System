package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.EmployeeUpdateRequest;
import com.pharmacy.backend.dto.EmployeeResponse;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponse> getAllEmployees();
    EmployeeResponse updateEmployeeProfile(String id, EmployeeUpdateRequest request);
    void deleteEmployee(String id);
}