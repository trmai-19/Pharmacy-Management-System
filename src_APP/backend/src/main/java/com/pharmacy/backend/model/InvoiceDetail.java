package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CTHD")
@IdClass(InvoiceDetailId.class)
public class InvoiceDetail {
    @Id
    @Column(name = "MAHD")
    private String mahd;

    @Id
    @Column(name = "MALO")
    private String malo;

    @Column(name = "SL")
    private Integer sl;

    @Column(name = "DONGIA")
    private Double dongia;

    @Column(name = "THANHTIEN")
    private Double thanhtien;

    @Column(name = "GHICHU")
    private String ghichu;
}