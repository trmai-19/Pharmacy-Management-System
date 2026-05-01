package com.pharmacy.backend.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirstLoginChangePasswordRequest {
    private String newPassword;
}