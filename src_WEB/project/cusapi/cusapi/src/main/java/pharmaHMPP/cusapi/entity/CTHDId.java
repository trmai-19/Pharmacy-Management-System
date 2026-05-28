package pharmaHMPP.cusapi.entity;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CTHDId implements Serializable {
    private String maHD;
    private String maLO;
}
