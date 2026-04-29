package com.pharmacy.backend.config;

import java.util.Date;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.model.Employee;
import com.pharmacy.backend.repository.AccountRepository;
import com.pharmacy.backend.repository.EmployeeRepository;

@Component
public class AdminSeeder implements CommandLineRunner {
    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepo;

    public AdminSeeder(AccountRepository accountRepo, PasswordEncoder passwordEncoder, EmployeeRepository employeeRepo) {
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
        this.employeeRepo = employeeRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        String rootAdminSdt = "0987654321";

        Optional<Account> adminOpt = accountRepo.findBySdt(rootAdminSdt);

        if(!adminOpt.isPresent()) {
            Account rootAdmin = new Account();


            long ts = System.currentTimeMillis() % 100000;
            String MATK = "TK" + ts;
            String MANV = "NV" + ts;

            rootAdmin.setMatk(MATK);
            rootAdmin.setSdt(rootAdminSdt);
            rootAdmin.setVaitro("STAFF");
            rootAdmin.setNgaytao(new Date());
            rootAdmin.setFirstLogin(false);
            
            String hashedPassword = passwordEncoder.encode("admin123");
            rootAdmin.setPassword(hashedPassword);

            accountRepo.save(rootAdmin);

            Employee newEmployee = new Employee();
            newEmployee.setManv(MANV);
            newEmployee.setMatk(MATK);
            newEmployee.setSdt(rootAdminSdt);
            newEmployee.setChucvu("ADMIN");
            newEmployee.setTrangthai("WORKING"); 
                
            employeeRepo.save(newEmployee);

            //test
            System.out.println("==================================================");
            System.out.println("DA KHOI TAO TAI KHOAN ADMIN MAC DINH THANH CONG");
            System.out.println("   - SDT : " + rootAdminSdt);
            System.out.println("   - PASSWORD     : admin123");
            System.out.println("==================================================");
        }
        else {
            System.out.println("TAI KHOAN ADMIN MAC DINH DA TON TAI");
        }
    }
}
