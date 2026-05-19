package com.pharmacy.controller.admin;

import com.pharmacy.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.pharmacy.model.Product;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ProductApiClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper(); // Dùng Jackson
    private final String API_URL = "http://localhost:8080/api/products"; // API công khai (Quy tắc 5.1)

    public List<Product> fetchAllProducts() throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .GET()
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    // Dùng Jackson bóc tách JSON kể cả khi lỗi để lấy message
    ApiResponse<List<Product>> apiRes = mapper.readValue(
        response.body(), 
        new TypeReference<ApiResponse<List<Product>>>() {}
    );

    if (response.statusCode() == 200 && apiRes.getStatus() == 200) {
        return apiRes.getData();
    } else {
        // Ném lỗi với thông báo từ Backend (ví dụ: "Lỗi thao tác cơ sở dữ liệu")
        throw new RuntimeException(apiRes.getMessage());
    }
}
}