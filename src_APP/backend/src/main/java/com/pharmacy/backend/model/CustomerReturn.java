package com.pharmacy.backend.model;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PHIEUTRA_KH")
public class CustomerReturn {
    @Id
    @Column(name = "MAPT_KH")
    private String maptKh;

    @Column(name = "MAHD")
    private String mahd;

    @Column(name = "MANV")
    private String manv;

    @Column(name = "NGAYTRA")
    private Date ngaytra;

    @Column(name = "LYDOTRA")
    private String lydotra;

    @Column(name = "TONGTIENHOAN")
    private Double tongtienhoan;
}