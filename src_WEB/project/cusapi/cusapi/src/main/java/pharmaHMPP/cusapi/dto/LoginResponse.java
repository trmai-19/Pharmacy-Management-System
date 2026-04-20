package pharmaHMPP.cusapi.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String maKH;
    private String tenKH;
    private String sdt;
    private String vaiTro;
    private boolean isFirstLogin;

    public LoginResponse(String token, String maKH, String tenKH, String sdt, String vaiTro) {
        this.token = token;
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.sdt = sdt;
        this.vaiTro = vaiTro;
        this.isFirstLogin = false;
    }

    public LoginResponse(String token, String maKH, String tenKH, String sdt, String vaiTro, boolean isFirstLogin) {
        this.token = token;
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.sdt = sdt;
        this.vaiTro = vaiTro;
        this.isFirstLogin = isFirstLogin;
    }
}