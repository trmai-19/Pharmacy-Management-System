package com.pharmacy.backend.controller;

import com.pharmacy.backend.dto.MedicineSuggestionDTO;
import com.pharmacy.backend.service.MedicineAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedicineAiController {

    private final MedicineAiService medicineAiService;

    @GetMapping("/ai-suggest")
    public ResponseEntity<List<MedicineSuggestionDTO>> getAiSuggestions(@RequestParam String keyword) {
        List<MedicineSuggestionDTO> res = medicineAiService.getSuggestions(keyword);
        return ResponseEntity.ok(res);
    }
}