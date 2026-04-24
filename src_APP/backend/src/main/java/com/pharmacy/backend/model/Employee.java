package com.pharmacy.backend.model;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NHANVIEN")
public class Employee {
    @Id
    @Column(name = "MANV")
    private String manv;

    @Column(name = "MATK")
    private String matk;

    @Column(name = "TENNV")
    private String tennv;

    @Column(name = "GIOITINH")
    private String gioitinh;

    @Column(name = "NGAYSINH")
    private Date ngaysinh;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "CHUCVU")
    private String chucvu;
}