package com.pharmacy.backend.config;

import java.util.Date;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.repository.AccountRepository;

@Component
public class AdminSeeder implements CommandLineRunner {
    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(AccountRepository accountRepo, PasswordEncoder passwordEncoder) {
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String rootAdminSdt = "0987654321";

        Optional<Account> adminOpt = accountRepo.findBySdt(rootAdminSdt);

        if(!adminOpt.isPresent()) {
            Account rootAdmin = new Account();

            String MATK = "TK0000";

            rootAdmin.setMatk(MATK);
            rootAdmin.setSdt(rootAdminSdt);
            rootAdmin.setVaitro("ADMIN");
            rootAdmin.setNgaytao(new Date());
            rootAdmin.setFirstLogin(false);
            
            String hashedPassword = passwordEncoder.encode("admin123");
            rootAdmin.setPassword(hashedPassword);

            accountRepo.save(rootAdmin);

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
