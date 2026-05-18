package com.pharmacy.backend.model;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PHIEUNHAP")
public class ImportReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MAPN")
    private String mapn;

    @Column(name = "MANV")
    private String manv;

    @Column(name = "MANCC")
    private String mancc;

    @Column(name = "NGAYNHAP")
    private Date ngaynhap;

    @Column(name = "TONGTIEN")
    private Double tongtien;

    @Column(name = "TRANGTHAI")
    private String trangthai = "KHOI_TAO";
}