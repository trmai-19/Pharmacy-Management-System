package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.EmployeeUpdateRequest;
import com.pharmacy.backend.dto.EmployeeResponse;
import com.pharmacy.backend.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employees")
public class EmployeeAdminController {

    private final EmployeeService employeeService;

    public EmployeeAdminController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees() {
        List<EmployeeResponse> data = employeeService.getAllEmployees();
        ApiResponse<List<EmployeeResponse>> response = new ApiResponse<>(200, "Lấy danh sách nhân viên thành công", data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable String id, 
            @RequestBody EmployeeUpdateRequest request) {
        EmployeeResponse data = employeeService.updateEmployeeProfile(id, request);
        ApiResponse<EmployeeResponse> response = new ApiResponse<>(200, "Cập nhật hồ sơ nhân viên thành công", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        ApiResponse<Void> response = new ApiResponse<>(200, "Đã xóa nhân viên thành công", null);
        return ResponseEntity.ok(response);
    }

    
}