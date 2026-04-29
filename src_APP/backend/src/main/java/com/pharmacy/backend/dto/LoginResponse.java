package com.pharmacy.backend.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String vaitro;
    private String token;
    private String hoten;
    private boolean firstLogin;
}
