package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.MedicineResponse;
import java.util.List;

public interface MedicineService {
    List<MedicineResponse> searchMedicines(String keyword);
    MedicineResponse getMedicineDetail(String id);
}