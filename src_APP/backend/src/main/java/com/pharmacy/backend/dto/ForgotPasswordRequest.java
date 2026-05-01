package com.pharmacy.backend.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {
    private String sdt;
    private String email;
}
