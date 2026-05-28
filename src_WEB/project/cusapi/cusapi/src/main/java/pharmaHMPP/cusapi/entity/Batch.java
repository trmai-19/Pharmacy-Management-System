package pharmaHMPP.cusapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "LOSANPHAM")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch {

    @Id
    @Column(name = "MALO", length = 20)
    private String malo;

    @Column(name = "MASP", length = 20)
    private String masp;

    @Column(name = "MAKHO", length = 20)
    private String makho;

    @Column(name = "NGAYSX")
    private LocalDate ngaysx;

    @Column(name = "NGAYNHAP")
    private LocalDate ngaynhap;

    @Column(name = "HSD")
    private LocalDate hsd;

    @Column(name = "SLSP")
    private Integer slsp;

    @Column(name = "TRANGTHAI", length = 50)
    private String trangthai;
}
