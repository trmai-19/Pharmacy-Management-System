package pharmaHMPP.cusapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DIEMTL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiemTL {

    @Id
    @Column(name = "MADTL", length = 20)
    private String maDTL;

    @Column(name = "LOAIGD", length = 50)
    private String loaiGD;

    @Column(name = "SL")
    private Integer sl;
}