package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DANHMUC")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MADM")
    private String madm;

    @Column(name = "TENDM")
    private String tendm;

    @Column(name = "MOTA")
    private String mota;

    @Column(name = "TRANGTHAI")
    private String trangthai;
}