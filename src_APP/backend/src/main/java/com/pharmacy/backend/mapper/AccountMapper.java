package com.pharmacy.backend.mapper;

import com.pharmacy.backend.dto.CreateUserRequest;
import com.pharmacy.backend.dto.LoginResponse;
import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public LoginResponse toLoginResponse(Account account, Employee employee, String token) {
        return LoginResponse.builder()
                .manv(employee.getManv())
                .token(token)
                .vaitro(employee.getChucvu())
                .firstLogin(account.isFirstLogin())
                .hoten(employee.getTennv())
                .build();
    }

    public void updateNewAccountFromRequest(Account account, CreateUserRequest request) {
        account.setSdt(request.getSdt());
        account.setEmail(request.getEmail());
        account.setVaitro("STAFF");
        account.setFirstLogin(true);
        account.setTrangthai("ACTIVE");
    }

    public void updateNewEmployeeFromRequest(Employee employee, CreateUserRequest request, String matk) {
        employee.setMatk(matk);
        employee.setSdt(request.getSdt());
        employee.setChucvu(request.getVaitro());
        employee.setTrangthai("WORKING");
    }
}