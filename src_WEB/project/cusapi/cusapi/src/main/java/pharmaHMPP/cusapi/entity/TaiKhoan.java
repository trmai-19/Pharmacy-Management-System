package pharmaHMPP.cusapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "TAIKHOAN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaiKhoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATK", length = 20)
    private String maTK;

    @Column(name = "VAITRO", length = 50)
    private String vaiTro;

    @Column(name = "PASSWORD", length = 255)
    private String password;

    @Column(name = "SDT", length = 10)
    private String sdt;

    @Column(name = "NGAYTAO")
    private LocalDate ngayTao;

    @Column(name = "IS_FIRST_LOGIN")
    @Builder.Default
    private boolean isFirstLogin = true;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "TRANGTHAI", length = 50)
    private String trangThai;
}