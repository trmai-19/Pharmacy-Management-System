package com.pharmacy.backend.config;

import com.pharmacy.backend.dto.ApiResponse;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.dao.DataAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<Void> response = new ApiResponse<>(400, ex.getMessage(), null);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(403)
            .body(new ApiResponse<>(403, "Bạn không có quyền thực hiện thao tác này", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ApiResponse<>(400, msg, null));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(404)
            .body(new ApiResponse<>(404, "API không tồn tại: " + ex.getRequestURL(), null));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDatabaseException(DataAccessException ex) {
        // Lấy nguyên gốc thông báo lỗi từ Database
        String msg = ex.getMostSpecificCause().getMessage();
        
        // Cắt chuỗi để lấy đúng nội dung từ ORA-20xxx (mã lỗi tự định nghĩa của chúng ta)
        if (msg != null && msg.contains("ORA-20")) {
            // Tách bằng regex tìm chữ ORA-20...
            String[] parts = msg.split("ORA-20\\d{2}:");
            if (parts.length > 1) {
                // Chỉ lấy dòng đầu tiên, bỏ các dòng trace (ORA-06512...) bên dưới
                msg = parts[1].split("\n")[0].trim(); 
            }
        } else {
            msg = "Lỗi thao tác cơ sở dữ liệu!"; // Fallback nếu lỗi DB khác
        }
        
        return ResponseEntity.badRequest().body(new ApiResponse<>(400, msg, null));
    }
}