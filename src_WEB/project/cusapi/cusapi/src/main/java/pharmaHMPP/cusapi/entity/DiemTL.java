package pharmaHMPP.cusapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    @Column(name = "MAKH", length = 20)
    private String maKH;

    @Column(name = "MAHD", length = 20)
    private String maHD;

    @Column(name = "DIEMTHAYDOI")
    private Integer diemThayDoi;

    @Column(name = "NGAYGD")
    private LocalDateTime ngayGD;

    @Column(name = "LOAIGD", length = 50)
    private String loaiGD;

    @Column(name = "GHICHU", length = 255)
    private String ghiChu;
}