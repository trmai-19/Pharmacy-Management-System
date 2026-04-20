package pharmaHMPP.cusapi.service;

import pharmaHMPP.cusapi.dto.SanPhamSearchResponse;
import pharmaHMPP.cusapi.dto.SanPhamSearchResponse.SanPhamDTO;
import pharmaHMPP.cusapi.entity.SanPham;
import pharmaHMPP.cusapi.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository sanPhamRepo;

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
                sp.getMaDM()
        );
    }
}
