package pharmaHMPP.cusapi.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String sdt;
    private String password;
}