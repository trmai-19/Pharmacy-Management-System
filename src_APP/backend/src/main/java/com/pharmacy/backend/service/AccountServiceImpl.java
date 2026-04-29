package com.pharmacy.backend.service;

import org.springframework.transaction.annotation.Transactional;
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
        Account acc = accountRepo.findBySdt(sdt)
            .orElseThrow(() -> new RuntimeException("Số điện thoại hoặc mật khẩu không chính xác!"));

        if (!passwordEncoder.matches(password, acc.getPassword())) {
            throw new RuntimeException("Số điện thoại hoặc mật khẩu không chính xác!");
        }

        if ("LOCKED".equals(acc.getTrangthai())) {
            throw new RuntimeException("Tài khoản của bạn đã bị khóa!");
        }

        if (!"STAFF".equals(acc.getVaitro())) {
            throw new RuntimeException("Tài khoản không có quyền truy cập vào hệ thống này!");
        }

        Employee emp = employeeRepo.findByMatk(acc.getMatk())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên liên kết!"));

        String generatedToken = jwtUtils.generateToken(emp.getSdt(), emp.getChucvu());

        LoginResponse res = new LoginResponse();
        res.setToken(generatedToken);
        res.setVaitro(emp.getChucvu());
        res.setFirstLogin(acc.isFirstLogin());
        res.setHoten(emp.getTennv());
        
        return res;
    }

    @Override
    @Transactional
    public void createAccount(String sdt, String email, String vaitro) {
        if(accountRepo.findBySdt(sdt).isPresent()) {
            throw new RuntimeException("Tài khoản với số điện thoại này đã tồn tại trong hệ thống!");
        }
        long ts = System.currentTimeMillis() % 100000;
        String MATK = "TK" + ts;
        String MANV = "NV" + ts;
        Account newAccount = new Account();
        newAccount.setMatk(MATK);
        newAccount.setSdt(sdt);
        newAccount.setVaitro("STAFF");
        newAccount.setEmail(email);
        newAccount.setFirstLogin(true);
        newAccount.setTrangthai("ACTIVE");

        String rawPassword = generateRandomPassword();
        newAccount.setPassword(passwordEncoder.encode(rawPassword));
        newAccount.setNgaytao(new java.util.Date());

        accountRepo.save(newAccount);

        Employee newEmployee = new Employee();
        newEmployee.setManv(MANV);
        newEmployee.setMatk(MATK);
        newEmployee.setSdt(sdt);
        newEmployee.setChucvu(vaitro);
        newEmployee.setTrangthai("WORKING"); 
            
        employeeRepo.save(newEmployee);

        emailService.sendAccountCreationEmail(email, sdt, rawPassword);
    }

    @Override
    public void toggleAccountStatus(String id) {
        Account acc = accountRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với mã: " + id));

        if ("ACTIVE".equalsIgnoreCase(acc.getTrangthai())) {
            
            if ("STAFF".equalsIgnoreCase(acc.getVaitro())) {
                Employee emp = employeeRepo.findByMatk(acc.getMatk())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên liên kết!"));

                if (!"RESIGNED".equalsIgnoreCase(emp.getTrangthai())) {
                    throw new RuntimeException("Không thể khóa tài khoản! Vui lòng chuyển trạng thái nhân viên sang đã nghỉ trước.");
                }
            }
            acc.setTrangthai("LOCKED");
            
        } else {

            if ("STAFF".equalsIgnoreCase(acc.getVaitro())) {
                Employee emp = employeeRepo.findByMatk(acc.getMatk())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên liên kết!"));

                if (!"WORKING".equalsIgnoreCase(emp.getTrangthai())) {
                    throw new RuntimeException("Không thể khóa tài khoản! Vui lòng chuyển trạng thái nhân viên sang đang làm trước.");
                }
            }
            acc.setTrangthai("ACTIVE");
        }
        
        accountRepo.save(acc);
    }

    @Override
    public void resetPassword(String sdt, String email) {
        Account acc = accountRepo.findBySdt(sdt)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với số điện thoại này!"));
            
        if(acc.getEmail() == null || !acc.getEmail().equals(email)) {
            throw new RuntimeException("Email cung cấp không khớp với thông tin tài khoản!");
        }

        String tempPassword = generateRandomPassword();
        acc.setPassword(passwordEncoder.encode(tempPassword));
        acc.setFirstLogin(true);
        accountRepo.save(acc);

        emailService.sendPasswordResetEmail(email, sdt, tempPassword);
    }

    @Override
    public void changePasswordFirstLogin(String sdt, String newPassword) {
        Account acc = accountRepo.findBySdt(sdt)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));
            
        if (!acc.isFirstLogin()) {
            throw new RuntimeException("Tài khoản đã thực hiện đổi mật khẩu lần đầu rồi!"); 
        }
        
        acc.setPassword(passwordEncoder.encode(newPassword));
        acc.setFirstLogin(false);
        accountRepo.save(acc);
    }

    @Override
    public void changePasswordSetting(String sdt, String oldPassword, String newPassword) {
        Account acc = accountRepo.findBySdt(sdt)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));
            
        if (acc.isFirstLogin()) {
            throw new RuntimeException("Vui lòng thực hiện đổi mật khẩu lần đầu tiên trước khi dùng chức năng này!"); 
        }

        if (!passwordEncoder.matches(oldPassword, acc.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác!");
        }
        
        acc.setPassword(passwordEncoder.encode(newPassword));
        accountRepo.save(acc);
    }
}