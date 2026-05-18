package pharmaHMPP.cusapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DANHMUC")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhMuc {

    @Id
    @Column(name = "MADM", length = 20)
    private String maDM;

    @Column(name = "TENDM", length = 100)
    private String tenDM;

    @Column(name = "MOTA", length = 255)
    private String moTa;

    @Column(name = "TRANGTHAI", length = 50)
    private String trangThai;
}
