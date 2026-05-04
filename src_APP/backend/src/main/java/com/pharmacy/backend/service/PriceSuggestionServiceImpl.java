package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.PriceSuggestionResponse;
import com.pharmacy.backend.mapper.PriceSuggestionMapper;
import com.pharmacy.backend.model.Category;
import com.pharmacy.backend.model.ImportReceipt;
import com.pharmacy.backend.model.ImportReceiptDetail;
import com.pharmacy.backend.model.Product;
import com.pharmacy.backend.repository.CategoryRepository;
import com.pharmacy.backend.repository.ImportReceiptDetailRepository;
import com.pharmacy.backend.repository.ImportReceiptRepository;
import com.pharmacy.backend.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceSuggestionServiceImpl implements PriceSuggestionService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImportReceiptDetailRepository importReceiptDetailRepository;
    private final ImportReceiptRepository importReceiptRepository;
    private final PriceSuggestionMapper priceSuggestionMapper;

    // =========================================================
    // Logic trung tâm: tính gợi ý giá cho 1 sản phẩm
    // =========================================================
    private PriceSuggestionResponse buildSuggestion(Product product,
                                                     Map<String, Category> categoryMap) {
        Category category = categoryMap.get(product.getMadm());
        String tenDanhMuc = category != null ? category.getTendm() : "N/A";
        Double tyle = category != null ? category.getTyleloinhuan() : null;

        // Lấy chi tiết nhập kho gần nhất của sản phẩm (từ phiếu đã HOANTAT)
        List<ImportReceiptDetail> details =
                importReceiptDetailRepository.findLatestByProductId(product.getMasp());

        // Trường hợp chưa có lịch sử nhập kho nào hoàn tất
        if (details.isEmpty()) {
            return priceSuggestionMapper.toPriceSuggestionResponse(
                    product, tenDanhMuc, null, null, tyle, null, null, "Chưa có lịch sử nhập kho hoàn tất để tính giá gợi ý" );
        }

        // Lấy bản ghi đầu tiên = lần nhập gần nhất (đã ORDER BY ngaynhap DESC trong query)
        ImportReceiptDetail latestDetail = details.get(0);
        Double giaNhap = latestDetail.getGianhap();

        // Lấy ngày nhập từ phiếu nhập
        Optional<ImportReceipt> receiptOpt = importReceiptRepository.findById(latestDetail.getMapn());
        java.util.Date ngayNhap = receiptOpt.map(ImportReceipt::getNgaynhap).orElse(null);

        // Tính giá đề xuất
        Double giaDeXuat = null;
        String ghiChu = null;

        if (giaNhap != null && tyle != null) {
            giaDeXuat = Math.round(giaNhap * (1 + tyle / 100.0) * 100.0) / 100.0;
        } else if (tyle == null) {
            ghiChu = "Danh mục chưa cấu hình tỉ lệ lợi nhuận";
        }

        Double chenhLech = (giaDeXuat != null && product.getGiaban() != null)
                ? Math.round((giaDeXuat - product.getGiaban()) * 100.0) / 100.0
                : null;

        return priceSuggestionMapper.toPriceSuggestionResponse(
                product, tenDanhMuc, giaNhap, ngayNhap, tyle, giaDeXuat, chenhLech, ghiChu
        );
    }

    // =========================================================
    // API 7a: Gợi ý giá cho 1 sản phẩm
    // =========================================================
    @Override
    public PriceSuggestionResponse getSuggestionForProduct(String masp) {
        Product product = productRepository.findById(masp)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + masp));

        Map<String, Category> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getMadm, c -> c));

        return buildSuggestion(product, categoryMap);
    }

    // =========================================================
    // API 7b: Gợi ý giá cho tất cả sản phẩm
    // =========================================================
    @Override
    public List<PriceSuggestionResponse> getAllPriceSuggestions() {
        List<Product> products = productRepository.findAll();

        Map<String, Category> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getMadm, c -> c));

        return products.stream()
                .map(p -> buildSuggestion(p, categoryMap))
                // Ưu tiên sản phẩm có chênh lệch lớn (cần cập nhật giá nhất) lên đầu
                .sorted((a, b) -> {
                    double da = a.getChenhLech() != null ? Math.abs(a.getChenhLech()) : -1;
                    double db = b.getChenhLech() != null ? Math.abs(b.getChenhLech()) : -1;
                    return Double.compare(db, da);
                })
                .collect(Collectors.toList());
    }
}
