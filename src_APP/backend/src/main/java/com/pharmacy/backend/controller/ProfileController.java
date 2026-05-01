package com.pharmacy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.pharmacy.backend.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<Void>> updateProfile(@RequestBody UpdateProfileRequest request) {
        String currentSdt = SecurityContextHolder.getContext().getAuthentication().getName();

        profileService.updateProfile(currentSdt, request);

        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật thông tin thành công!", null));
    }
}