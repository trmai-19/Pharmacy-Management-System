package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CTPT_KH")
@IdClass(ReturnReceiptDetailId.class) // Gọi file khóa chính kép ở trên vào
public class ReturnReceiptDetail {

    @Id
    @Column(name = "MAPT_KH")
    private String maptKh;

    @Id
    @Column(name = "MALO")
    private String malo;

    @Column(name = "SL")
    private Integer sl;

    @Column(name = "DONGIAHOAN")
    private Double dongiahoan;

    @Column(name = "THANHTIEN")
    private Double thanhtien;
}