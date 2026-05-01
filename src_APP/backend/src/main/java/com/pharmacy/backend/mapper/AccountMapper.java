package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.CreateUserRequest;
import com.pharmacy.backend.dto.LoginResponse;
import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.model.Employee;

public class AccountMapper {
    public static LoginResponse toLoginResponse(Account account, Employee employee, String token) {
        return LoginResponse.builder()
                .token(token)
                .vaitro(employee.getChucvu())
                .firstLogin(account.isFirstLogin())
                .hoten(employee.getTennv())
                .build();
    }

    public static void updateNewAccountFromRequest(Account account, CreateUserRequest request) {
        account.setSdt(request.getSdt());
        account.setEmail(request.getEmail());
        account.setVaitro("STAFF");
        account.setFirstLogin(true);
        account.setTrangthai("ACTIVE");
    }

    public static void updateNewEmployeeFromRequest(Employee employee, CreateUserRequest request, String matk) {
        employee.setMatk(matk);
        employee.setSdt(request.getSdt());
        employee.setChucvu(request.getVaitro());
        employee.setTrangthai("WORKING");
    }
}