package pharmaHMPP.cusapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "HOADON")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDon {

    @Id
    @Column(name = "MAHD", length = 20)
    private String maHD;

    @Column(name = "MANV", length = 20)
    private String maNV;

    @Column(name = "MAKH", length = 20)
    private String maKH;

    @Column(name = "NGAYBAN")
    private LocalDate ngayBan;

    @Column(name = "TONGTIEN", precision = 15, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "TIENTHANHTOAN", precision = 15, scale = 2)
    private BigDecimal tienThanhToan;

    @Column(name = "DIEMSUDUNG")
    private Integer diemSuDung;

    @Column(name = "TRANGTHAI", length = 50)
    private String trangThai;
}