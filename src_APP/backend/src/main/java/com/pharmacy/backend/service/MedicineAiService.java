package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.MedicineSuggestionDTO;
import java.util.List;

public interface MedicineAiService {
    List<MedicineSuggestionDTO> getSuggestions(String keyword);
}