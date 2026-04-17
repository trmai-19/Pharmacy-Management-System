package pharmaHMPP.cusapi.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
public class DiemResponse {
    private String maKH;
    private String tenKH;
    private Integer tongDiem;
    private List<DiemChiTiet> lichSuDiem;

    @Data
    @AllArgsConstructor
    public static class DiemChiTiet {
        private String maDTL;
        private String loaiGD;
        private Integer sl;
    }
}