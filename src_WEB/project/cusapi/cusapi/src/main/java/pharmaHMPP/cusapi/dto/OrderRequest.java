package pharmaHMPP.cusapi.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private List<OrderItem> items;
    private Integer diemSuDung;
    private String ghiChu;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItem {
        private String masp;
        private Integer sl;
    }
}
