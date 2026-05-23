package com.pharmacy.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.backend.dto.MedicineSuggestionDTO;
import com.pharmacy.backend.model.MedicineCache;
import com.pharmacy.backend.repository.MedicineCacheRepository;
import com.pharmacy.backend.service.MedicineAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicineAiServiceImpl implements MedicineAiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final MedicineCacheRepository cacheRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<MedicineSuggestionDTO> getSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String cleanKeyword = keyword.trim().toLowerCase();

        // 1. Check Oracle Cache
        Optional<MedicineCache> cachedData = cacheRepository.findByKeyword(cleanKeyword);
        if (cachedData.isPresent()) {
            try {
                log.info("Hit cache for keyword: {}", cleanKeyword);
                return objectMapper.readValue(cachedData.get().getResponseData(), new TypeReference<List<MedicineSuggestionDTO>>() {});
            } catch (Exception e) {
                log.error("Lỗi parse JSON từ Cache DB: {}", e.getMessage());
            }
        }

        // 2. Call AI API if Cache miss
        try {
            log.info("Cache miss. Calling Gemini API for keyword: {}", cleanKeyword);
            String aiResponseRaw = callGeminiApi(cleanKeyword);
            String jsonArrayString = extractJsonArray(aiResponseRaw);

            List<MedicineSuggestionDTO> suggestions = objectMapper.readValue(jsonArrayString, new TypeReference<List<MedicineSuggestionDTO>>() {});

            // 3. Save to Cache
            MedicineCache newCache = MedicineCache.builder()
                    .keyword(cleanKeyword)
                    .responseData(jsonArrayString)
                    .build();
            cacheRepository.save(newCache);

            return suggestions;
        } catch (Exception e) {
            log.error("Lỗi gọi API AI hoặc xử lý kết quả: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private String callGeminiApi(String keyword) {
        RestTemplate restTemplate = new RestTemplate();
        String urlWithKey = apiUrl + "?key=" + apiKey;

        String prompt = String.format(
            "Bạn là một chuyên gia dược phẩm. Người dùng cung cấp từ khóa tên thuốc gần đúng: '%s'. " +
            "Hãy tìm kiếm và đưa ra danh sách từ 5 đến 10 sản phẩm thuốc thương mại chính xác, phổ biến tại thị trường Việt Nam khớp hoặc gần giống nhất với từ khóa này. " +
            "Yêu cầu trả về kết quả dưới dạng một JSON Array duy nhất. Các key bắt buộc: " +
            "\"tenChuan\", \"donViTinh\", \"thanhPhan\", \"congDung\". " +
            "Ví dụ: [{\"tenChuan\": \"Panadol Extra\", \"donViTinh\": \"Hộp\", \"thanhPhan\": \"Paracetamol 500mg, Caffeine 65mg\", \"congDung\": \"Hạ sốt, giảm đau\"}]. " +
            "Chỉ trả về chuỗi JSON Array đúng cấu trúc, không kèm bất kỳ text giải thích nào.",
            keyword
        );

        Map<String, Object> textMap = Map.of("text", prompt);
        Map<String, Object> partsMap = Map.of("parts", List.of(textMap));
        Map<String, Object> contentsMap = Map.of("contents", List.of(partsMap));
        Map<String, Object> generationConfig = Map.of("responseMimeType", "application/json");

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(partsMap),
            "generationConfig", generationConfig
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(urlWithKey, entity, String.class);

        return response.getBody();
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