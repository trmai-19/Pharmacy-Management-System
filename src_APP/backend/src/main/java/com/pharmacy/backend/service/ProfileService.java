package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.UpdateProfileRequest;

public interface ProfileService {
    boolean updateProfile(String sdt, UpdateProfileRequest request);
}