package com.pharmacy.backend.model;

import java.util.Date;
import org.hibernate.annotations.DynamicInsert; // dòng này tối ưu câu lệnh insert trong sql 
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TAIKHOAN")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATK")
    private String matk;

    @Column(name = "VAITRO")
    private String vaitro;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "NGAYTAO")
    private Date ngaytao;

    @Column(name = "IS_FIRST_LOGIN")
    private boolean isFirstLogin = true;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "TRANGTHAI")
    private String trangthai;
}