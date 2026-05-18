package com.pharmacy.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.pharmacy.backend.model.Warehouse;
import com.pharmacy.backend.repository.WarehouseRepository;

@Component
public class WarehouseSeeder implements CommandLineRunner {

    private final WarehouseRepository warehouseRepo;

    public WarehouseSeeder(WarehouseRepository warehouseRepo) {
        this.warehouseRepo = warehouseRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        
        // 1. Kiểm tra và khởi tạo KHO THUỐC
        if (!warehouseRepo.existsByLoaikho("THUOC")) {
            Warehouse khoThuoc = new Warehouse();
            khoThuoc.setLoaikho("THUOC");
            khoThuoc.setSlton(0);
            khoThuoc.setDvsp("Chung"); 
            warehouseRepo.save(khoThuoc);
            
            System.out.println("==================================================");
            System.out.println("DA KHOI TAO [KHO THUOC] MAC DINH THANH CONG");
            System.out.println("==================================================");
        } else {
            System.out.println("[KHO THUOC] MAC DINH DA TON TAI");
        }

        // 2. Kiểm tra và khởi tạo KHO VẬT TƯ Y TẾ (VTYT)
        if (!warehouseRepo.existsByLoaikho("VTYT")) {
            Warehouse khoVtyt = new Warehouse();
            khoVtyt.setLoaikho("VTYT");
            khoVtyt.setSlton(0);
            khoVtyt.setDvsp("Chung"); 
            warehouseRepo.save(khoVtyt);
            
            System.out.println("==================================================");
            System.out.println("DA KHOI TAO [KHO VAT TU Y TE] MAC DINH THANH CONG");
            System.out.println("==================================================");
        } else {
            System.out.println("[KHO VAT TU Y TE] MAC DINH DA TON TAI");
        }
    }
}