package pharmaHMPP.cusapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pharmaHMPP.cusapi.dto.SanPhamSearchResponse;
import pharmaHMPP.cusapi.dto.SanPhamSearchResponse.SanPhamDTO;
import pharmaHMPP.cusapi.entity.DanhMuc;
import pharmaHMPP.cusapi.service.SanPhamService;

import java.util.List;

@RestController
@RequestMapping("/api/sanpham")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;

    /** GET /api/sanpham/search?q={keyword} – tìm kiếm sản phẩm (public) */
    @GetMapping("/search")
    public ResponseEntity<SanPhamSearchResponse> search(@RequestParam(name = "q", defaultValue = "") String keyword) {
        return ResponseEntity.ok(sanPhamService.search(keyword));
    }

    /** GET /api/sanpham/{maSP} – chi tiết sản phẩm (public) */
    @GetMapping("/{maSP}")
    public ResponseEntity<?> getDetail(@PathVariable String maSP) {
        try {
            SanPhamDTO detail = sanPhamService.getDetail(maSP);
            return ResponseEntity.ok(detail);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/sanpham/browse?q={keyword}&maDM={maDM} – duyệt toàn bộ sản phẩm (public) */
    @GetMapping("/browse")
    public ResponseEntity<List<SanPhamDTO>> browse(
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(name = "maDM", required = false) String maDM) {
        return ResponseEntity.ok(sanPhamService.browse(keyword, maDM));
    }

    /** GET /api/sanpham/danhmuc – lấy danh sách danh mục (public) */
    @GetMapping("/danhmuc")
    public ResponseEntity<List<DanhMuc>> getDanhMuc() {
        return ResponseEntity.ok(sanPhamService.getAllDanhMuc());
    }
}
