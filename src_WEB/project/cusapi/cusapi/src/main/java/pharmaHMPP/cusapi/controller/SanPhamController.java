package pharmaHMPP.cusapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pharmaHMPP.cusapi.dto.SanPhamSearchResponse;
import pharmaHMPP.cusapi.dto.SanPhamSearchResponse.SanPhamDTO;
import pharmaHMPP.cusapi.service.SanPhamService;

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
}
