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
    @Column(name = "MADM")
    private String madm;

    @Column(name = "TENDM")
    private String tendm;

    @Column(name = "MOTA")
    private String mota;

    @Column(name = "TYLELOINHUAN")
    private Double tyleloinhuan;
}