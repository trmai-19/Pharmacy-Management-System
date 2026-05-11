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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MAKHO")
    private String makho;

    @Column(name = "SLTON")
    private Integer slton;

    @Column(name = "DVSP")
    private String dvsp;
}