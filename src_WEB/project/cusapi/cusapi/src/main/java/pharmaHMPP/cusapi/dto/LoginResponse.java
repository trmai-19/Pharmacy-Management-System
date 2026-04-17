package pharmaHMPP.cusapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String maKH;
    private String tenKH;
    private String sdt;
    private String vaiTro;
}