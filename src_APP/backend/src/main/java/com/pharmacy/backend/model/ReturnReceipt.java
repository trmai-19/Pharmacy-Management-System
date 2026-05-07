package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PHIEUTRA_KH")
public class ReturnReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MAPT_KH")
    private String maptKh;

    @Column(name = "MAHD")
    private String mahd;

    @Column(name = "MANV")
    private String manv;

    @Column(name = "NGAYTRA") 
    private LocalDateTime ngaytra;

    @Column(name = "LYDOTRA")
    private String lydotra;

    @Column(name = "TONGTIENHOAN")
    private Double tongtienhoan = 0.0;
}