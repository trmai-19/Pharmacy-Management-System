package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "KHO")
public class Warehouse {
    @Id
    @Column(name = "MAKHO")
    private String makho;

    @Column(name = "MALO")
    private String malo;

    @Column(name = "SLTON")
    private Integer slton;

    @Column(name = "DVSP")
    private String dvsp;
}