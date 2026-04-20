package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NHACUNGCAP")
public class Supplier {
    @Id
    @Column(name = "MANCC")
    private String mancc;

    @Column(name = "TENNCC")
    private String tenncc;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "DIACHI")
    private String diachi;
}