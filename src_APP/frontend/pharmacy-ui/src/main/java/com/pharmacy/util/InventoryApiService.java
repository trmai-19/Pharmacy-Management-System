package com.pharmacy.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharmacy.dto.ApiResponse; 
import com.pharmacy.dto.InventoryReportDTO;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class InventoryApiService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private static final String BASE_API_URL = "http://localhost:8080/api/admin/reports/inventory/dashboard";

    public CompletableFuture<InventoryReportDTO> fetchInventoryData(String year, String quarter, String productGroup, String customerType) {
        StringBuilder urlBuilder = new StringBuilder(BASE_API_URL).append("?");
        
        try {
            if (year != null && !year.equals("Year") && !year.equals("Năm")) {
                urlBuilder.append("year=").append(year).append("&");
            }
            if (quarter != null && !quarter.equals("All Quarters") && !quarter.equals("Quý") && !quarter.equals("Quarter")) {
                urlBuilder.append("quarter=").append(quarter.replace("Q", "")).append("&");
            }
            if (productGroup != null && !productGroup.equals("All Product Groups") && !productGroup.equals("Nhóm sản phẩm")) {
                urlBuilder.append("productGroup=").append(java.net.URLEncoder.encode(productGroup, "UTF-8")).append("&");
            }
            if (customerType != null && !customerType.equals("All Customers") && !customerType.equals("Đối tượng khách hàng")) {
                urlBuilder.append("customerType=").append(java.net.URLEncoder.encode(customerType, "UTF-8")).append("&");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(finalUrl))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + com.pharmacy.util.Session.getToken())
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    Type type = new TypeToken<ApiResponse<InventoryReportDTO>>(){}.getType();
                    ApiResponse<InventoryReportDTO> response = gson.fromJson(json, type);
                    if (response != null && response.getStatus() == 200) {
                        return response.getData();
                    }
                    throw new RuntimeException("Lỗi khi tải dữ liệu từ API kho!");
                    
                });
    }
}