package com.pharmacy.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharmacy.dto.ApiResponse;
import com.pharmacy.dto.PerformanceReportDTO;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ReportApiService {

    // Đổi port 8080 nếu backend của cậu chạy port khác
    private static final String API_URL = "http://localhost:8080/api/admin/reports/performance";
    private final HttpClient client;
    private final Gson gson;

    public ReportApiService() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    // Hàm gọi API bất đồng bộ bằng CompletableFuture của Java 17
    public CompletableFuture<PerformanceReportDTO> fetchPerformanceData() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Accept", "application/json")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    // Định nghĩa cấu trúc Generic cho Gson
                    Type type = new TypeToken<ApiResponse<PerformanceReportDTO>>(){}.getType();
                    ApiResponse<PerformanceReportDTO> response = gson.fromJson(json, type);
                    
                    if (response != null && response.getStatus() == 200) {
                        return response.getData();
                    }
                    throw new RuntimeException("API trả về lỗi hoặc không có dữ liệu!");
                });
    }
}