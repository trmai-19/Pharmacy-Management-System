package com.pharmacy.backend.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pharmacy.backend.dto.LoginResponse;
import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.model.Employee;
import com.pharmacy.backend.repository.AccountRepository;
import com.pharmacy.backend.repository.EmployeeRepository;

import com.pharmacy.backend.security.JwtUtils;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    public AccountServiceImpl(AccountRepository accountRepo, EmployeeRepository employeeRepo, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, EmailService emailService) {
        this.accountRepo = accountRepo;
        this.employeeRepo = employeeRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }
    
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public LoginResponse checkLogin(String sdt, String password) {
        Optional<Account> accountOpt = accountRepo.findBySdt(sdt);
        if(accountOpt.isPresent()) {
            Account acc = accountOpt.get();
            boolean isPasswordMatch = passwordEncoder.matches(password, acc.getPassword());

            if(isPasswordMatch && acc.getVaitro().equals("STAFF")) {

                Optional<Employee> employeeOpt = employeeRepo.findByMatk(acc.getMatk());
                Employee emp = employeeOpt.get();

                String generatedToken = jwtUtils.generateToken(emp.getSdt(), emp.getChucvu());

                LoginResponse res = new LoginResponse();
                res.setToken(generatedToken);
                res.setVaitro(emp.getChucvu());
                res.setFirstLogin(acc.isFirstLogin());
                res.setHoten(emp.getTennv());
                return res;

            } else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    @Override
    @Transactional // rollback nếu lỗi
    public boolean createAccount(String sdt, String email, String vaitro) {
        if(accountRepo.findBySdt(sdt).isPresent()) return false;

        Account newAccount = new Account();
        long ts = System.currentTimeMillis() % 100000;
        String generatedMATK = "TK" + ts;

        newAccount.setMatk(generatedMATK);
        newAccount.setSdt(sdt);
        newAccount.setVaitro("STAFF");
        newAccount.setEmail(email);
        newAccount.setFirstLogin(true);

        String rawPassword = generateRandomPassword();
        newAccount.setPassword(passwordEncoder.encode(rawPassword));
        newAccount.setNgaytao(new java.util.Date());

        accountRepo.save(newAccount);

        long tsProfile = (System.currentTimeMillis() + 1) % 100000;

        Employee newEmployee = new Employee();
        newEmployee.setManv("NV" + tsProfile);
        newEmployee.setMatk(generatedMATK);
        newEmployee.setSdt(sdt);
        newEmployee.setChucvu(vaitro); 
        // Các thông tin khác user sẽ tự update
            
        employeeRepo.save(newEmployee);

        // GỬI EMAIL THÔNG BÁO
        emailService.sendAccountCreationEmail(email, sdt, rawPassword);
        return true;
    }

    @Override
    public boolean resetPassword(String sdt, String email) {
        Optional<Account> accOpt = accountRepo.findBySdt(sdt);
        if(accOpt.isPresent()) {
            Account acc = accOpt.get();
            if(acc.getEmail() != null && acc.getEmail().equals(email) ) {
                String tempPassword = generateRandomPassword();
                acc.setPassword(passwordEncoder.encode(tempPassword));
                acc.setFirstLogin(true);
                accountRepo.save(acc);

                emailService.sendPasswordResetEmail(email, sdt, tempPassword);

                return true;
            }
        }
        return false;
    }

    @Override
    public boolean changePasswordFirstLogin(String sdt, String newPassword) {
        Optional<Account> accOpt = accountRepo.findBySdt(sdt);
        if(accOpt.isPresent()) {
            Account acc = accOpt.get();
            if (!acc.isFirstLogin()) {
                return false; 
            }
            acc.setPassword(passwordEncoder.encode(newPassword));
            acc.setFirstLogin(false);
            accountRepo.save(acc);
            return true;
        }
        return false;
    }

    @Override
    public boolean changePasswordSetting(String sdt, String oldPassword, String newPassword) {

        Optional<Account> accOpt = accountRepo.findBySdt(sdt);
        if(accOpt.isPresent()) {
            Account acc = accOpt.get();
            
            if (acc.isFirstLogin()) {
                return false; 
            }

            if (!passwordEncoder.matches(oldPassword, acc.getPassword())) {
                return false;
            }
            
            acc.setPassword(passwordEncoder.encode(newPassword));
            accountRepo.save(acc);
            return true;
        }

        return false;
    }

}
