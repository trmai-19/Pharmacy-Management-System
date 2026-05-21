package com.pharmacy.backend.service;

import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pharmacy.backend.dto.AccountResponse;
import com.pharmacy.backend.dto.CreateUserRequest;
import com.pharmacy.backend.dto.LoginResponse;
import com.pharmacy.backend.mapper.AccountMapper;
import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.model.Employee;
import com.pharmacy.backend.repository.AccountRepository;
import com.pharmacy.backend.repository.EmployeeRepository;
import com.pharmacy.backend.security.JwtUtils;
import java.util.List; 
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final JdbcTemplate jdbcTemplate;

    private final AccountRepository accountRepo;
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final AccountMapper accountMapper;
    
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public LoginResponse checkLogin(String sdt, String password) {
        Account acc = accountRepo.findBySdt(sdt)
            .orElseThrow(() -> new RuntimeException("Số điện thoại hoặc mật khẩu không chính xác!"));

        if ("LOCKED".equals(acc.getTrangthai())) {
            throw new RuntimeException("Tài khoản của bạn đã bị khóa!");
        }

        if (!"STAFF".equals(acc.getVaitro())) {
            throw new RuntimeException("Tài khoản không có quyền truy cập vào hệ thống này!");
        }
        
        if (!passwordEncoder.matches(password, acc.getPassword())) {
            throw new RuntimeException("Số điện thoại hoặc mật khẩu không chính xác!");
        }

        Employee emp = employeeRepo.findByMatk(acc.getMatk())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên liên kết!"));

        String generatedToken = jwtUtils.generateToken(emp.getSdt(), emp.getChucvu());

        return accountMapper.toLoginResponse(acc, emp, generatedToken);
    }

    @Override
    @Transactional
    public void createAccount(CreateUserRequest request) {
        if(accountRepo.findBySdt(request.getSdt()).isPresent()) {
            throw new RuntimeException("Tài khoản với số điện thoại này đã tồn tại trong hệ thống!");
        }
        
        Account newAccount = new Account();
        accountMapper.updateNewAccountFromRequest(newAccount, request);
        
        String rawPassword = generateRandomPassword();
        newAccount.setPassword(passwordEncoder.encode(rawPassword));
        newAccount.setNgaytao(new java.util.Date());

        accountRepo.save(newAccount);

        Employee newEmployee = new Employee();
        accountMapper.updateNewEmployeeFromRequest(newEmployee, request, newAccount.getMatk());
            
        employeeRepo.save(newEmployee);

        emailService.sendAccountCreationEmail(request.getEmail(), request.getSdt(), rawPassword);
    }

   @Override
    public void toggleAccountStatus(String id) {
        Account acc = accountRepo.findBySdt(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với SĐT: " + id));

        if ("ACTIVE".equalsIgnoreCase(acc.getTrangthai())) {
            
            if ("STAFF".equalsIgnoreCase(acc.getVaitro())) {
                Employee emp = employeeRepo.findByMatk(acc.getMatk())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên liên kết!"));

                // Logic của bạn: Bắt buộc nhân viên phải nghỉ việc mới được khóa tài khoản
                if (!"RESIGNED".equalsIgnoreCase(emp.getTrangthai())) {
                    throw new RuntimeException("Không thể khóa tài khoản! Vui lòng chuyển trạng thái nhân sự sang ĐÃ NGHỈ VIỆC (RESIGNED) trước.");
                }
            }
            acc.setTrangthai("LOCKED");
            
        } else {

            if ("STAFF".equalsIgnoreCase(acc.getVaitro())) {
                Employee emp = employeeRepo.findByMatk(acc.getMatk())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên liên kết!"));

                if (!"WORKING".equalsIgnoreCase(emp.getTrangthai())) {
                    throw new RuntimeException("Không thể mở khóa! Vui lòng chuyển trạng thái nhân sự sang ĐANG LÀM VIỆC (WORKING) trước.");
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
   

   
    @Override
    public List<AccountResponse> getAllAccountsInSystem() {
        String sql = "SELECT * FROM V_DANH_SACH_TAI_KHOAN";
        
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> new AccountResponse(
                    rs.getString("username"),
                    rs.getString("ownerName"),
                    rs.getString("role"),
                    rs.getString("status"),
                    rs.getString("email"),
                    rs.getString("accountType")
            ));
            } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Lỗi khi đọc hoặc thực thi file SQL script: " + e.getMessage());
    }
        }
}