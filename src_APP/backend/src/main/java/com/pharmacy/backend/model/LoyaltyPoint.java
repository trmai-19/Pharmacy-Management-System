package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DIEMTL") 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPoint {

    @Id
    @Column(name = "MADTL")
    private String id;

    @Column(name = "MAKH") 
    private String customerId;

    @Column(name = "MAHD") 
    private String invoiceId;

    @Column(name = "LOAIGD") 
    private String transactionType;

    @Column(name = "DIEMTHAYDOI") 
    private Integer pointAmount;

    @Column(name = "NGAYGD")
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(name = "GHICHU")
    private String note;
}