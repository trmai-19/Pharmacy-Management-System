package pharmaHMPP.cusapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "KHACHHANG")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHang {

    @Id
    @Column(name = "MAKH", length = 20)
    private String maKH;

    @Column(name = "MATK", length = 20)
    private String maTK;

    @Column(name = "MADTL", length = 20)
    private String maDTL;

    @Column(name = "TENKH", length = 100, nullable = false)
    private String tenKH;

    @Column(name = "GIOITINH", length = 10)
    private String gioiTinh;

    @Column(name = "SDT", length = 10, unique = true, nullable = false)
    private String sdt;
}