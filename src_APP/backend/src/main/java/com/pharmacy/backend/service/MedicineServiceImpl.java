package com.pharmacy.backend.service; 

import com.pharmacy.backend.dto.MedicineResponse;
import com.pharmacy.backend.mapper.MedicineMapper;
import com.pharmacy.backend.model.Product;
import com.pharmacy.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final ProductRepository productRepository;
    private final MedicineMapper medicineMapper;
    @Override
    public List<MedicineResponse> searchMedicines(String keyword) {
        List<Product> products = productRepository.searchProducts(keyword);
        return products.stream()
                .map(medicineMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MedicineResponse getMedicineDetail(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại thuốc với mã: " + id));
        
        return medicineMapper.toResponse(product);
    }
}