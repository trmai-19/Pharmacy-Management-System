package com.pharmacy.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.backend.dto.MedicineSuggestionDTO;
import com.pharmacy.backend.model.MedicineCache;
import com.pharmacy.backend.repository.MedicineCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class MedicineAiServiceImpl implements MedicineAiService {

    private final MedicineCacheRepository cacheRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient;
    private final String apiUrlWithKey;

    public MedicineAiServiceImpl(MedicineCacheRepository cacheRepository,
                                 @Value("${gemini.api.key}") String apiKey,
                                 @Value("${gemini.api.url}") String apiUrl) {
        this.cacheRepository = cacheRepository;
        this.apiUrlWithKey = apiUrl + "?key=" + apiKey;

        // Cài đặt Timeout để server không bị treo nếu AI phản hồi chậm
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(30000);

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public List<MedicineSuggestionDTO> getSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String cleanKeyword = keyword.trim().toLowerCase();

        Optional<MedicineCache> cachedData = cacheRepository.findByKeyword(cleanKeyword);
        if (cachedData.isPresent()) {
            try {
                log.info("Hit cache for keyword: {}", cleanKeyword);
                return objectMapper.readValue(cachedData.get().getResponseData(), new TypeReference<>() {});
            } catch (Exception e) {
                log.error("Lỗi parse JSON từ Cache DB: {}", e.getMessage());
            }
        }

        try {
            log.info("Cache miss. Calling Gemini via RestClient for: {}", cleanKeyword);
            String aiResponseRaw = callGeminiApi(cleanKeyword);
            String jsonArrayString = extractJsonArray(aiResponseRaw);

            List<MedicineSuggestionDTO> suggestions = objectMapper.readValue(jsonArrayString, new TypeReference<>() {});

            MedicineCache newCache = MedicineCache.builder()
                    .keyword(cleanKeyword)
                    .responseData(jsonArrayString)
                    .build();
            cacheRepository.save(newCache);

            return suggestions;
        } catch (Exception e) {
            log.error("Lỗi gọi API AI hoặc xử lý kết quả: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private String callGeminiApi(String keyword) {
        String prompt = String.format(
            "Bạn là một chuyên gia dược phẩm. Từ khóa tên thuốc: '%s'. " +
            "Trả về JSON Array 5-10 sản phẩm thực tế ở VN. Các key: tenChuan, donViTinh, thanhPhan, congDung. " +
            "Không kèm text giải thích.", keyword
        );

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
            "generationConfig", Map.of("responseMimeType", "application/json")
        );

        return restClient.post()
                .uri(apiUrlWithKey)
                .body(requestBody)
                .retrieve()
                .body(String.class);
    }

    private String extractJsonArray(String rawResponse) throws Exception {
        var rootNode = objectMapper.readTree(rawResponse);
        String rawText = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText().trim();

        if (rawText.startsWith("```json")) {
            rawText = rawText.substring(7, rawText.length() - 3).trim();
        } else if (rawText.startsWith("```")) {
            rawText = rawText.substring(3, rawText.length() - 3).trim();
        }
        return rawText;
    }
}