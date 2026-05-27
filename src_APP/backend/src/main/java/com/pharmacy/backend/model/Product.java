package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SANPHAM")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MASP")
    private String masp;

    @Column(name = "MADM")
    private String madm;

    @Column(name = "TENSANPHAM")
    private String tensanpham;

    @Column(name = "DVT")
    private String dvt;

    @Column(name = "CONGDUNG")
    private String congdung;

    @Column(name = "THANHPHAN")
    private String thanhphan;

    @Column(name = "GIABAN")
    private Double giaban;

    @Column(name = "TRANGTHAI")
    private String trangthai;
}