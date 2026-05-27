package com.pharmacy.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "MEDICINE_CACHE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "KEYWORD", nullable = false, unique = true)
    private String keyword;

    @Lob
    @Column(name = "RESPONSE_DATA", nullable = false)
    private String responseData;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    // Tự động gán thời gian khi insert vào DB
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}