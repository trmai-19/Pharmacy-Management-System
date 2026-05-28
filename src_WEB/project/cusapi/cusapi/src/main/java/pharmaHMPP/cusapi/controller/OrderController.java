package pharmaHMPP.cusapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pharmaHMPP.cusapi.dto.OrderRequest;
import pharmaHMPP.cusapi.dto.OrderResponse;
import pharmaHMPP.cusapi.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(Authentication auth, @RequestBody OrderRequest request) {
        String maTK = auth.getName();
        return ResponseEntity.ok(orderService.createOrder(maTK, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication auth) {
        String maTK = auth.getName();
        return ResponseEntity.ok(orderService.getMyOrders(maTK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetails(Authentication auth, @PathVariable String id) {
        String maTK = auth.getName();
        return ResponseEntity.ok(orderService.getOrderDetails(maTK, id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(Authentication auth, @PathVariable String id) {
        String maTK = auth.getName();
        orderService.cancelOrder(maTK, id);
        return ResponseEntity.ok().build();
    }
}
