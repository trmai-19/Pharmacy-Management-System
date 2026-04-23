package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DIEMTL")
public class RewardPoint {
    @Id
    @Column(name = "MADTL")
    private String madtl;

    @Column(name = "LOAIGD")
    private String loaigd;

    @Column(name = "SL")
    private Integer sl;
}