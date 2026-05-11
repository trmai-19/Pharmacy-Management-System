package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CTPT_NCC")
@IdClass(SupplierReturnDetailId.class)
public class SupplierReturnDetail {
    @Id
    @Column(name = "MAPT_NCC")
    private String maptNcc;

    @Id
    @Column(name = "MALO")
    private String malo;

    @Column(name = "SL")
    private Integer sl;

    @Column(name = "DONGIATRA")
    private Double dongiatra;

    @Column(name = "THANHTIEN")
    private Double thanhtien;
}