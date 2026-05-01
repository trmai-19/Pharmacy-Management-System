package com.pharmacy.backend.model;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HOADON")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MAHD")
    private String mahd;

    @Column(name = "MANV")
    private String manv;

    @Column(name = "MAKH")
    private String makh;

    @Column(name = "NGAYBAN")
    private Date ngayban;

    @Column(name = "TONGTIEN")
    private Double tongtien;

    @Column(name = "DIEMSUDUNG")
    private Integer diemsudung;

    @Column(name = "TIENTHANHTOAN")
    private Double tienthanhtoan;

    @Column(name = "TRANGTHAI")
    private String trangthai = "KHOI TAO";
}