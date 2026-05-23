package pharmaHMPP.cusapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SANPHAM")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MASP", length = 20)
    private String maSP;

    @Column(name = "MADM", length = 20)
    private String maDM;

    @Column(name = "TENSANPHAM", length = 100)
    private String tenSanPham;

    @Column(name = "DVT", length = 50)
    private String dvt;

    @Column(name = "CONGDUNG", length = 255)
    private String congDung;

    @Column(name = "THANHPHAN", length = 500)
    private String thanhPhan;

    @Column(name = "GIABAN")
    private Double giaBan;
}
