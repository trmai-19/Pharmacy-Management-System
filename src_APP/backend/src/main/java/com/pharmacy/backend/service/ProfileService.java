package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.ProfileResponse;
import com.pharmacy.backend.dto.UpdateProfileRequest;

public interface ProfileService {
    void updateProfile(String sdt, UpdateProfileRequest request);
    ProfileResponse getProfile(String sdt);
}