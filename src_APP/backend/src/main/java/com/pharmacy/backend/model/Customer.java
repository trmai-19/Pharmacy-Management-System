package com.pharmacy.backend.model;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "KHACHHANG")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MAKH")
    private String makh;

    @Column(name = "MATK")
    private String matk;

    @Column(name = "MADTL")
    private String madtl;

    @Column(name = "TENKH")
    private String tenkh;

    @Column(name = "GIOITINH")
    private String gioitinh;

    @Column(name = "NGAYSINH")
    private Date ngaysinh;
    
    @Column(name = "SDT")
    private String sdt;

    @Column(name = "TONGDOANHTHU")
    private Double tongdoanhthu = 0.0;

    @Column(name = "HANGTV")
    private String hangtv = "THANH VIEN";
}