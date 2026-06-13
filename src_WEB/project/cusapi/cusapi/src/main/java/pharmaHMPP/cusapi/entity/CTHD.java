package pharmaHMPP.cusapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CTHD")
@IdClass(CTHDId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CTHD {

    @Id
    @Column(name = "MAHD", length = 20)
    private String maHD;

    @Id
    @Column(name = "MALO", length = 20)
    private String maLO;

    @Column(name = "SL")
    private Integer sl;

    @Column(name = "DONGIA")
    private Double dongia;

    @Column(name = "THANHTIEN")
    private Double thanhtien;

    @Column(name = "GHICHU", length = 255)
    private String ghiChu;
}
