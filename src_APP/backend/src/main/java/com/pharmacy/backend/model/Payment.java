package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "THANHTOAN")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATT")
    private String matt;

    @Column(name = "MAHD")
    private String mahd;

    @Column(name = "PHUONGTHUC")
    private String phuongthuc;

    @Column(name = "TRANGTHAI")
    private String trangthai;
}