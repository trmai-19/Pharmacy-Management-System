package pharmaHMPP.cusapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHangUpdateRequest {
    private String tenKH;
    private String gioiTinh;
    private LocalDate ngaySinh;
    private String sdt;
    private String email;
}
