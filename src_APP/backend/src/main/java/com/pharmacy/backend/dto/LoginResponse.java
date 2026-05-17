package com.pharmacy.backend.dto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String manv;
    private String vaitro;
    private String token;
    private String hoten;
    private boolean firstLogin;
}
