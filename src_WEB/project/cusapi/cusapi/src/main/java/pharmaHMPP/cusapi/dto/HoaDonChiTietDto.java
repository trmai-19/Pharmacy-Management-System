package pharmaHMPP.cusapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonChiTietDto {
    private String maHD;
    private String maLo;
    private String maSP;
    private String tenSanPham;
    private String dvt;
    private Integer sl;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private String ghiChu;
}
