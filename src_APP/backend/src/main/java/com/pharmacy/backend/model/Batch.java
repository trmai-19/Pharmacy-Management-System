package com.pharmacy.backend.model;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LOSANPHAM")
public class Batch {
    @Id
    @Column(name = "MALO")
    private String malo;

    @Column(name = "MASP")
    private String masp;

    @Column(name = "MADM")
    private String madm;

    @Column(name = "NGAYSX")
    private Date ngaysx;

    @Column(name = "NGAYNHAP")
    private Date ngaynhap;

    @Column(name = "HSD")
    private Date hsd;

    @Column(name = "SLSP")
    private Integer slsp;

    @Column(name = "TRANGTHAI")
    private String trangthai;
}