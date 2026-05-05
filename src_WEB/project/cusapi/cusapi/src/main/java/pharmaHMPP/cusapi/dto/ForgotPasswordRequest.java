package pharmaHMPP.cusapi.dto;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String sdt;
    private String email;
}
