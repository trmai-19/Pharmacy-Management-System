package pharmaHMPP.cusapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHangProfileDto {
    private String maKH;
    private String maTK;
    private String tenKH;
    private String gioiTinh;
    private LocalDate ngaySinh;
    private String sdt;
    private BigDecimal tongDoanhThu;
    private Integer diemTichLuy;
    private String hangTV;
    private String email;
}
