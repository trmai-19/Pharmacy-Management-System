package com.pharmacy.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharmacy.dto.ApiResponse;
import com.pharmacy.dto.PerformanceReportDTO;
import com.pharmacy.dto.CustomerReportDTO;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ReportApiService {

    // Đổi port 8080 nếu backend của cậu chạy port khác
    private static final String BASE_API_URL = "http://localhost:8080/api/admin/reports/performance";
    private static final String CUSTOMER_API_URL = "http://localhost:8080/api/admin/reports/customer"; // Endpoint cho Customer
    
    private final HttpClient client;
    private final Gson gson;

    public ReportApiService() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    // =================================================================
    // API CALL CHO TAB PERFORMANCE
    // =================================================================
    public CompletableFuture<PerformanceReportDTO> fetchPerformanceData(String year, String quarter, String productGroup, String customerType, String metric) {
        StringBuilder urlBuilder = new StringBuilder(BASE_API_URL).append("?");
        
        if (year != null && !year.equals("Year")) urlBuilder.append("year=").append(year).append("&");
        if (quarter != null && !quarter.equals("All Quarters")) urlBuilder.append("quarter=").append(quarter.replace("Q", "")).append("&");
        if (productGroup != null && !productGroup.equals("All Product Groups")) urlBuilder.append("productGroup=").append(java.net.URLEncoder.encode(productGroup, java.nio.charset.StandardCharsets.UTF_8)).append("&");
        if (customerType != null && !customerType.equals("All Customers")) urlBuilder.append("customerType=").append(customerType.replace(" ", "")).append("&");
        if (metric != null) urlBuilder.append("metric=").append(metric).append("&");

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) finalUrl = finalUrl.substring(0, finalUrl.length() - 1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(finalUrl))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + Session.getToken())
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    Type type = new TypeToken<ApiResponse<PerformanceReportDTO>>(){}.getType();
                    ApiResponse<PerformanceReportDTO> response = gson.fromJson(json, type);
                    if (response != null && response.getStatus() == 200) return response.getData();
                    throw new RuntimeException("API Performance trả về lỗi hoặc không có dữ liệu!");
                });
    }

    // =================================================================
    // API CALL CHO TAB CUSTOMER 
    // =================================================================
    public CompletableFuture<CustomerReportDTO> fetchCustomerData(String year, String quarter, String productGroup, String customerType) {
        StringBuilder urlBuilder = new StringBuilder(CUSTOMER_API_URL).append("?");
        
        if (year != null && !year.equals("Year")) urlBuilder.append("year=").append(year).append("&");
        if (quarter != null && !quarter.equals("All Quarters")) urlBuilder.append("quarter=").append(quarter.replace("Q", "")).append("&");
        if (productGroup != null && !productGroup.equals("All Product Groups")) urlBuilder.append("productGroup=").append(java.net.URLEncoder.encode(productGroup, java.nio.charset.StandardCharsets.UTF_8)).append("&");
        if (customerType != null && !customerType.equals("All Customers")) urlBuilder.append("customerType=").append(java.net.URLEncoder.encode(customerType, java.nio.charset.StandardCharsets.UTF_8)).append("&");

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) finalUrl = finalUrl.substring(0, finalUrl.length() - 1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(finalUrl))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + Session.getToken()) // Lấy token để xác thực
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    Type type = new TypeToken<ApiResponse<CustomerReportDTO>>(){}.getType();
                    ApiResponse<CustomerReportDTO> response = gson.fromJson(json, type);
                    if (response != null && response.getStatus() == 200) return response.getData();
                    throw new RuntimeException("API Customer trả về lỗi hoặc không có dữ liệu!");
                });
    }
}