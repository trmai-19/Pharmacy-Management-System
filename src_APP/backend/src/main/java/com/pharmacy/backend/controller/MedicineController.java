package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.ApiResponse;
import com.pharmacy.backend.dto.MedicineResponse;
import com.pharmacy.backend.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products") 
@RequiredArgsConstructor 
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicineResponse>>> getMedicines(
            @RequestParam(required = false) String search) {
        
        List<MedicineResponse> data = medicineService.searchMedicines(search);
        
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách thuốc thành công", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicineResponse>> getDetail(@PathVariable String id) {
        
        MedicineResponse data = medicineService.getMedicineDetail(id);
        
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin chi tiết thành công", data));
    }
}