package pharmaHMPP.cusapi.service;

import pharmaHMPP.cusapi.dto.OrderRequest;
import pharmaHMPP.cusapi.dto.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(String maTK, OrderRequest request);
    List<OrderResponse> getMyOrders(String maTK);
    OrderResponse getOrderDetails(String maTK, String maHD);
    void cancelOrder(String maTK, String maHD);
}
