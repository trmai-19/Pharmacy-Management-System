package pharmaHMPP.cusapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "KHACHHANG")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MAKH", length = 20)
    private String maKH;

    @Column(name = "MATK", length = 20)
    private String maTK;

    @Column(name = "TENKH", length = 100)
    private String tenKH;

    @Column(name = "GIOITINH", length = 10)
    private String gioiTinh;

    @Column(name = "NGAYSINH")
    private LocalDate ngaySinh;

    @Column(name = "SDT", length = 10)
    private String sdt;

    @Column(name = "TONGDOANHTHU")
    private BigDecimal tongDoanhThu;

    @Column(name = "DIEMTICHLUY")
    private Integer diemTichLuy;

    @Column(name = "HANGTV", length = 50)
    private String hangTV;
}