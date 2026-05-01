package com.pharmacy.backend.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
class ImportReceiptDetailId implements Serializable {
    private String mapn;
    private String malo;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CTPN")
@IdClass(ImportReceiptDetailId.class)
public class ImportReceiptDetail {
    @Id
    @Column(name = "MAPN")
    private String mapn;

    @Id
    @Column(name = "MALO")
    private String malo;

    @Column(name = "SL")
    private Integer sl;

    @Column(name = "GIANHAP")
    private Double gianhap;

    @Column(name = "DVT")
    private String dvt;

    @Column(name = "GHICHU")
    private String ghichu;

    @Column(name = "THANHTIEN")
    private Double thanhtien;
}