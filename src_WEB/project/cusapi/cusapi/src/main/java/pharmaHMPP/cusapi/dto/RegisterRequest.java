package pharmaHMPP.cusapi.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String tenKH;
    private String sdt;
    private String password;
    private String gioiTinh;
}