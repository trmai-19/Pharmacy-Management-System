package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.pharmacy.backend.dto.UpdateProfileRequest;
import com.pharmacy.backend.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateProfile(@RequestBody UpdateProfileRequest request) {
        // Trích xuất SĐT từ token đang đăng nhập để tránh user tự ý sửa thông tin người khác
        String currentSdt = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean success = profileService.updateProfile(currentSdt, request);

        if (success) {
            return ResponseEntity.ok("Cập nhật thông tin thành công!");
        }
        return ResponseEntity.badRequest().body("Lỗi khi cập nhật thông tin!");
    }
}