package com.pharmacy.backend.dto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ApiResponse<T> {
    
    private int status;
    private String message;
    private T data;

}