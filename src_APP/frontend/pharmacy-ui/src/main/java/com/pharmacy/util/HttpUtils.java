package com.pharmacy.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class HttpUtils {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Gửi yêu cầu GET tới API và trả về kết quả dưới dạng đối tượng T
     * @param endpoint Ví dụ: "/products"
     * @param typeReference Kiểu dữ liệu mong muốn (ví dụ: new TypeReference<ApiResponse<List<MedicineResponse>>>() {})
     * @return CompletableFuture chứa kết quả
     */
    public static <T> CompletableFuture<T> get(String endpoint, TypeReference<T> typeReference) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("API error: " + response.statusCode());
                    }
                    try {
                        return mapper.readValue(response.body(), typeReference);
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi giải mã JSON: " + e.getMessage(), e);
                    }
                });
    }

    // Bạn có thể thêm các phương thức post, put, delete sau này
}
