package pharmaHMPP.cusapi.service;

import pharmaHMPP.cusapi.dto.SanPhamSearchResponse;
import pharmaHMPP.cusapi.dto.SanPhamSearchResponse.SanPhamDTO;
import pharmaHMPP.cusapi.entity.DanhMuc;
import pharmaHMPP.cusapi.entity.SanPham;
import pharmaHMPP.cusapi.repository.DanhMucRepository;
import pharmaHMPP.cusapi.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository sanPhamRepo;
    private final DanhMucRepository danhMucRepo;

    /** Tìm kiếm theo tên hoặc công dụng, trả kết quả + sản phẩm tương tự (cùng danh mục) */
    public SanPhamSearchResponse search(String keyword) {
        List<SanPham> ketQua = sanPhamRepo
                .findByTenSanPhamContainingIgnoreCaseOrCongDungContainingIgnoreCase(keyword, keyword);

        List<SanPhamDTO> ketQuaDTO = toDTO(ketQua);

        // Lấy sản phẩm tương tự: cùng danh mục với kết quả đầu tiên, loại trừ kết quả đã có
        List<SanPhamDTO> tuongTuDTO = List.of();
        if (!ketQua.isEmpty()) {
            String maDM = ketQua.get(0).getMaDM();
            if (maDM != null) {
                List<String> maSPDaHien = ketQua.stream()
                        .map(SanPham::getMaSP)
                        .collect(Collectors.toList());

                List<SanPham> tuongTu = sanPhamRepo.findAll().stream()
                        .filter(sp -> maDM.equals(sp.getMaDM()) && !maSPDaHien.contains(sp.getMaSP()))
                        .limit(6)
                        .collect(Collectors.toList());

                tuongTuDTO = toDTO(tuongTu);
            }
        }

        return new SanPhamSearchResponse(ketQuaDTO, tuongTuDTO);
    }

    /** Lấy chi tiết một sản phẩm */
    public SanPhamDTO getDetail(String maSP) {
        SanPham sp = sanPhamRepo.findById(maSP)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));
        return toSingleDTO(sp);
    }

    /** Lấy tất cả sản phẩm (browse), có thể filter theo danh mục và tìm kiếm */
    public List<SanPhamDTO> browse(String keyword, String maDM) {
        List<SanPham> products;

        if ((keyword == null || keyword.isBlank()) && (maDM == null || maDM.isBlank())) {
            // Không filter gì → lấy hết
            products = sanPhamRepo.findAll();
        } else if (maDM != null && !maDM.isBlank()) {
            // Có filter danh mục (có thể kèm keyword)
            products = sanPhamRepo.searchWithCategory(
                    (keyword != null && !keyword.isBlank()) ? keyword : null,
                    maDM
            );
        } else {
            // Chỉ có keyword
            products = sanPhamRepo.searchAll(keyword);
        }

        return toDTO(products);
    }

    /** Lấy danh sách tất cả danh mục */
    public List<DanhMuc> getAllDanhMuc() {
        return danhMucRepo.findAll();
    }

    private List<SanPhamDTO> toDTO(List<SanPham> list) {
        return list.stream().map(this::toSingleDTO).collect(Collectors.toList());
    }

    private SanPhamDTO toSingleDTO(SanPham sp) {
        return new SanPhamDTO(
                sp.getMaSP(),
                sp.getTenSanPham(),
                sp.getCongDung(),
                sp.getDvt(),
                sp.getGiaBan(),
                sp.getMaDM(),
                sp.getThanhPhan()
        );
    }
}
