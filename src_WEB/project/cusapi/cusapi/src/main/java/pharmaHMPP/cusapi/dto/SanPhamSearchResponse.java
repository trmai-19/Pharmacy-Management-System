package pharmaHMPP.cusapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamSearchResponse {
    private List<SanPhamDTO> ketQua;
    private List<SanPhamDTO> sanPhamTuongTu;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SanPhamDTO {
        private String maSP;
        private String tenSanPham;
        private String congDung;
        private String dvt;
        private Long giaBan;
        private String maDM;
    }
}
