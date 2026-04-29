package com.pharmacy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;      // 200 cho thành công, 400 cho thất bại [cite: 20]
    private String message;   // Thông báo cho Frontend [cite: 22]
    private T data;           // Dữ liệu thực tế (DTO) [cite: 19, 23]
}