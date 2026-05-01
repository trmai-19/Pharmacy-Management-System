package com.pharmacy.backend.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
