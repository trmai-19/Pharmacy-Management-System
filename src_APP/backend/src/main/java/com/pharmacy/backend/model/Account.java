package com.pharmacy.backend.model;

import java.util.Date;
import org.hibernate.annotations.DynamicInsert;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TAIKHOAN")
public class Account {
    @Id
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