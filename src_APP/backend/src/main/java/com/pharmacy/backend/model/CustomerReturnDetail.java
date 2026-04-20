package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CTPT_KH")
public class CustomerReturnDetail {
    @Id
    @Column(name = "MACTPT_KH")
    private String mactptKh;

    @Column(name = "MAPT_KH")
    private String maptKh;

    @Column(name = "MALO")
    private String malo;

    @Column(name = "SL")
    private Integer sl;

    @Column(name = "DONGIAHOAN")
    private Double dongiahoan;

    @Column(name = "THANHTIEN")
    private Double thanhtien;
}