package pharmaHMPP.cusapi.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private String maHD;
    private String trangThai;
    private LocalDate ngayBan;
    private BigDecimal tongTien;
    private BigDecimal tienThanhToan;
    private Integer diemSuDung;
    private String ghiChu;
    private List<HoaDonChiTietDto> items;
}
