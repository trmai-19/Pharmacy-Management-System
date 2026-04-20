package com.pharmacy.backend.model;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PHIEUTRA_NCC")
public class SupplierReturn {
    @Id
    @Column(name = "MAPT_NCC")
    private String maptNcc;

    @Column(name = "MAPN")
    private String mapn;

    @Column(name = "MANV")
    private String manv;

    @Column(name = "NGAYTRA")
    private Date ngaytra;

    @Column(name = "LYDOTRA")
    private String lydotra;

    @Column(name = "TONGTIEN")
    private Double tongtien;
}